package com.site.abyp.service;
import com.site.abyp.dto.FieldDTO;
import com.site.abyp.dto.PostDTO;
import com.site.abyp.dto.PostTypeDTO;
import com.site.abyp.model.Post;
import com.site.abyp.model.PostType;
import com.site.abyp.model.User;
import com.site.abyp.repository.PostRepository;
import com.site.abyp.repository.PostTypeRepository;
import com.site.abyp.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class PostService {

    private final PostRepository postRepository;
    private final PostTypeRepository postTypeRepository;
    private final UserRepository userRepository;

    public PostService(PostRepository postRepository,
                       PostTypeRepository postTypeRepository,
                       UserRepository userRepository
    ) {
        this.postRepository = postRepository;
        this.postTypeRepository = postTypeRepository;
        this.userRepository = userRepository;
    }

    /* --------------------------------
     * CREATE
     * -------------------------------- */
    public PostDTO create(Post post) {
        Post saved = postRepository.save(post);
        return toDTO(saved);
    }

    /* --------------------------------
     * READ — ALL
     * -------------------------------- */
    public List<PostDTO> getAll() {
        return postRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /* --------------------------------
     * READ — BY ID
     * -------------------------------- */
    public PostDTO getById(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        return toDTO(post);
    }

    /* --------------------------------
     * FIND — BY SLUG (Optional)
     * -------------------------------- */
    public Optional<PostDTO> findBySlug(String slug) {
        return postRepository.findBySlug(slug)
                .map(this::toDTO);
    }

    /* --------------------------------
     * FIND — BY POST TYPE (archive)
     * -------------------------------- */
    public List<PostDTO> findByType(String type) {

        return postRepository.findByPostType_Name(type)
                .stream()
                .map(this::toDTO)
                .toList();
    }

    public PostDTO findBySlugAndType(String slug, String type) {
        Post post = postRepository.findBySlugAndPostType_Name(slug, type)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        return toDTO(post);
    }

    /* --------------------------------
     * UPDATE
     * -------------------------------- */
    public PostDTO update(Long id, Post updated) {
        Post existing = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        existing.setTitle(updated.getTitle());
        existing.setSlug(updated.getSlug());
        existing.setContent(updated.getContent());
        existing.setPostType(updated.getPostType());
        existing.setMeta(updated.getMeta());

        return toDTO(postRepository.save(existing));
    }

    /* --------------------------------
     * DELETE
     * -------------------------------- */
    public void delete(Long id) {
        if (!postRepository.existsById(id)) {
            throw new RuntimeException("Post not found");
        }
        postRepository.deleteById(id);
    }

    /* --------------------------------
     * DTO MAPPING
     * -------------------------------- */
    private PostDTO toDTO(Post post) {

        Long authorId = null;
        String authorName = null;

        if (post.getAuthor() != null) {
            authorId = post.getAuthor().getId();
            authorName = post.getAuthor().getUsername(); // correct for your User entity
        }

        Long postTypeId = null;
        String postTypeName = null;

        if (post.getPostType() != null) {
            postTypeId = post.getPostType().getId();
            postTypeName = post.getPostType().getName();
        }

        return new PostDTO(
                post.getId(),
                post.getTitle(),
                post.getContent(),
                post.getSlug(),
                authorId,
                authorName,
                postTypeId,
                postTypeName,
                post.isPublished(),
                post.getCreatedAt(),
                post.getUpdatedAt(),
                post.getVersion(),
                post.getMeta()
        );
    }

    public List<String> validateRequiredFields(PostDTO dto, PostTypeDTO type) {
        Map<String, Object> input = dto.meta();
        Map<String, FieldDTO> schemaFields = type.fields();

        List<String> missing = new ArrayList<>();
        for (Map.Entry<String, FieldDTO> entry : schemaFields.entrySet()) {
            validateFieldRecursively(
                    entry.getValue(),
                    input,
                    entry.getKey(),
                    missing
            );
        }

        return missing;
    }

    private void validateFieldRecursively(FieldDTO field,
                                          Map<String, Object> input,
                                          String path,
                                          List<String> missing) {
        boolean required = field.required();
        Object value = input != null ? input.get(field.name()) : null;
        if (required && (value == null || value.toString().isBlank())) {
            missing.add(path);
            return;
        }
        if (field.subFields() == null || field.subFields().isEmpty()) {
            return;
        }

        if (!(value instanceof Map)) {
            if (required) missing.add(path + "(must be an object)");
            return;
        }

        @SuppressWarnings("unchecked")
        Map<String, Object> nested = (Map<String, Object>)  value;

        for (FieldDTO sub: field.subFields()) {
            validateFieldRecursively(
                    sub,
                    nested,
                    path + "." + sub.name(),
                    missing
            );
        }
    }

    public PostDTO create(PostDTO dto, String typeName) {

        PostType postType = postTypeRepository
                .findByName(typeName)
                .orElseThrow(() -> new RuntimeException("Invalid post type"));

        User author = userRepository.findById(dto.authorId())
                .orElseThrow(() -> new RuntimeException("Invalid author ID"));

        Post post = new Post();
        post.setTitle(dto.title());
        post.setContent(dto.content());
        post.setPublished(dto.published());
        post.setAuthor(author);
        post.setPostType(postType);

        post.setMeta(dto.meta());

        if (dto.slug() != null) {
            post.setSlug(dto.slug());
        } else {
            post.setSlug(generateSlug(dto.title()));
        }

        Post saved = postRepository.save(post);
        return toDTO(saved);
    }

    private String generateSlug(String title) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Title cannot be empty when generating a slug");
        }

        String slug = title.toLowerCase();

        slug = slug.replaceAll("[^a-z0-9]", "-");
        slug = slug.replaceAll("(^-+|-+$)", "");
        slug = slug.replaceAll("-{2,}", "");
        String uniqueSlug = slug;
        int counter = 1;
        while (postRepository.existsBySlug(uniqueSlug)) {
            uniqueSlug = slug + "-" + counter;
            counter++;
        }
        return uniqueSlug;
    }

    public PostDTO collectSchemeFields(PostDTO dto, PostTypeDTO type) {
        Map<String, Object> incoming = dto.meta() != null ? dto.meta() : new HashMap<>();
        Map<String, Object> collected = new HashMap<>();
        List<String> missing = new ArrayList<>();
        for (FieldDTO field : type.fields().values()) {
            String key = field.name();
            if (field.required()) {
                if (!incoming.containsKey(key) ||  incoming.get(key) == null || incoming.get(key).toString().trim().isEmpty()) {
                   missing.add(key);
                   continue;
                }
            }
            if (incoming.containsKey(key)) {
                collected.put(key, incoming.get(key));
            }
        }
        if(!missing.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Missing required fields: " + String.join(", ", missing)
            );
        }

        return new PostDTO(
                dto.id(),
                dto.title(),
                dto.content(),
                dto.slug(),
                dto.authorId(),
                dto.authorName(),
                dto.postTypeId(),
                dto.postTypeName(),
                dto.published(),
                dto.createdAt(),
                dto.updatedAt(),
                dto.version(),
                collected
        );
    }

    public List<PostDTO> findAllByType(PostTypeDTO postTypeDTO) {
        if (postTypeDTO == null || postTypeDTO.name() == null) {
            return List.of(); // or throw error if you prefer
        }

        String typeName = postTypeDTO.name();

        return postRepository.findByPostType_Name(typeName)
                .stream()
                .map(this::toDTO)     // your entity → dto converter
                .toList();
    }
}
