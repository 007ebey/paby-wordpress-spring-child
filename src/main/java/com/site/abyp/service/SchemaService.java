package com.site.abyp.service;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.site.abyp.dto.FieldDTO;
import com.site.abyp.dto.PostTypeDTO;
import com.site.abyp.model.PostType;
import com.site.abyp.model.PostTypeField;
import com.site.abyp.repository.PostTypeFieldRepository;
import com.site.abyp.repository.PostTypeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class SchemaService {
    private final PostTypeRepository postTypeRepository;
    private final PostTypeFieldRepository postTypeFieldRepository;

    public SchemaService(PostTypeRepository postTypeRepository,
                         PostTypeFieldRepository postTypeFieldRepository) {
        this.postTypeRepository = postTypeRepository;
        this.postTypeFieldRepository = postTypeFieldRepository;
    }

    public PostTypeDTO create(PostTypeDTO dto) {

        PostType postType = new PostType(dto.name(), dto.description());

        PostType saved = postTypeRepository.save(postType);

        Map<String, FieldDTO> fields = dto.fields();
        if (fields != null) {
            fields.values().forEach(fieldDTO -> saveFieldRecursively(fieldDTO, saved, null));
        }

        return toDTO(saved);
    }

    public PostTypeDTO update(Long id, PostTypeDTO dto) {
        PostType existing = postTypeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("PostType not found"));
        existing.setName(dto.name());
        existing.setDescription(dto.description());

        postTypeFieldRepository.deleteById(existing.getId());
        Map<String, FieldDTO> fields = dto.fields();
        if (fields != null) {
            fields.values().forEach(fieldDTO -> saveFieldRecursively(fieldDTO, existing, null)
                    );
        }

        return toDTO(existing);
    }

    public PostTypeDTO get(Long id) {
        PostType postType = postTypeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("PostType not found"));
        return toDTO(postType);
    }

    public PostTypeDTO getByName(String name) {
        PostType postType = postTypeRepository.findByName(name)
                .orElseThrow(() -> new RuntimeException("PostType not found"));
        return toDTO(postType);
    }

    public List<PostTypeDTO> getAll() {
        return postTypeRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public void delete(Long id) {
        if (!postTypeRepository.existsById(id)) {
            throw new RuntimeException("PostType not found");
        }
        postTypeFieldRepository.deleteById(id);
        postTypeRepository.deleteById(id);
    }

    private  PostTypeDTO toDTO(PostType postType) {
        Map<String, FieldDTO> fieldMap = postType.getFields()
                .stream()
                .filter(f -> f.getParentField() == null)
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

    private  FieldDTO toFieldDTO(PostTypeField field) {
        List<FieldDTO> sub = field.getSubFields()
                .stream()
                .map(this::toFieldDTO)
                .toList();
        return new FieldDTO(
                field.getId(),
                field.getName(),
                field.getType(),
                field.isRequired(),
                field.getDefaultValue(),
                sub
        );
    }

    private final ObjectMapper mapper = new ObjectMapper();

    private PostTypeField saveFieldRecursively(FieldDTO dto, PostType postType, PostTypeField parent) {
        PostTypeField field;
        if (dto.id() != null) {
            field = postTypeFieldRepository.findById(dto.id())
                    .orElse(new PostTypeField());
        } else {
            field = new PostTypeField();
        }

        field.setPostType(postType);
        field.setParentField(parent);
        field.setName(dto.name());
        field.setType(dto.type());
        field.setRequired(dto.required());

        Map<String, Object> defaultMap = new HashMap<>();
        try {
            Object dtoValue = dto.defaultValue();
            if (dtoValue instanceof Map<?, ?> map) {
                // Safe cast using ObjectMapper
                defaultMap = mapper.convertValue(map, new TypeReference<>() {});
            } else if (dtoValue instanceof String str && !str.isBlank()) {
                // String JSON → Map
                defaultMap = mapper.readValue(str, new TypeReference<>() {});
            }
        } catch (Exception e) {
            throw new RuntimeException("Invalid defaultValue for field: " + dto.name(), e);
        }

        field.setDefaultValue(defaultMap);

        PostTypeField saved = postTypeFieldRepository.save(field);
        if (dto.subFields() != null) {
            List<PostTypeField> subFieldEntities = dto.subFields()
                    .stream()
                    .map(sub -> saveFieldRecursively(sub, postType, saved))
                    .toList();
            saved.setSubFields(subFieldEntities);
        }

        return postTypeFieldRepository.save(saved);
    }
}
