package com.site.abyp.dto;

import java.util.List;

public record FieldDTO(
        Long id,
        String name,
        String type,
        boolean required,
        Object defaultValue,
        List<FieldDTO> subFields
) { }
