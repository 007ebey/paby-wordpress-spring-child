package com.site.abyp.service;
import com.site.abyp.dto.PostDTO;
import com.site.abyp.model.Post;
import com.site.abyp.repository.PostRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class PostService {

    private final PostRepository postRepository;
    private final PostTypeService postTypeService;

    public PostService(PostRepository postRepository, PostTypeService postTypeService) {
        this.postRepository = postRepository;
        this.postTypeService = postTypeService;
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

        // Optional: validate that type exists in PostType
        postTypeService.getByName(type);

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




}
