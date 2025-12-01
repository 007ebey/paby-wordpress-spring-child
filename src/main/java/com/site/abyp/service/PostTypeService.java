package com.site.abyp.service;

import com.site.abyp.dto.FieldDTO;
import com.site.abyp.dto.PostTypeDTO;
import com.site.abyp.dto.PostTypeFieldDTO;
import com.site.abyp.model.PostType;
import com.site.abyp.model.PostTypeField;
import com.site.abyp.repository.PostTypeRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class PostTypeService {

    private final PostTypeRepository postTypeRepository;

    public PostTypeService(PostTypeRepository postTypeRepository) {
        this.postTypeRepository = postTypeRepository;
    }

    public PostTypeDTO create(PostType postType) {
        PostType saved = postTypeRepository.save(postType);
        return toDTO(saved);
    }

    public List<PostTypeDTO> getAll() {
        return postTypeRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public PostTypeDTO update(Long id, PostType updated) {
        PostType existing = postTypeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("PostType not found"));
        existing.setName(updated.getName());
        existing.setDescription(updated.getDescription());
        return toDTO(postTypeRepository.save(existing));
    }

    public void delete(Long id) {
        if (!postTypeRepository.existsById(id)) {
            throw new RuntimeException("PostType not found");
        }
        postTypeRepository.deleteById(id);
    }

    private PostTypeDTO toDTO(PostType postType) {
        Map<String, FieldDTO> fieldMap = postType.getFields()
                .stream()
                .collect(Collectors.toMap(
                        PostTypeField::getName,
                        this::toFieldDTO
                ));

        return new PostTypeDTO(
                postType.getId(),
                postType.getName(),
                postType.getDescription(),
                fieldMap
        );
    }

    private FieldDTO toFieldDTO(PostTypeField f) {

        List<FieldDTO> subFields = f.getSubFields()
                .stream()
                .map(this::toFieldDTO) // recursive
                .collect(Collectors.toList());

        return new FieldDTO(
                f.getId(),
                f.getName(),
                f.getType(),
                f.isRequired(),
                f.getDefaultValue(),
                subFields
        );
    }

    public PostTypeDTO getByName(String name) {
       PostType postType =  postTypeRepository.findByName(name)
                .orElseThrow(() -> new RuntimeException("Post type not found: " + name));
       return toDTO(postType);
    }

}
