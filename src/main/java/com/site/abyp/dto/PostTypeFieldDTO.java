package com.site.abyp.dto;

import java.util.List;

public record PostTypeFieldDTO(
        Long id,
        String name,
        String type,
        boolean required,
        Object defaultValue,
        List<PostTypeFieldDTO> subFields
) {}
