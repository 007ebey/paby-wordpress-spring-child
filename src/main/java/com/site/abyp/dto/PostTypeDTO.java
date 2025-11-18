package com.site.abyp.dto;

import java.util.Map;

public record PostTypeDTO(
        Long id,
    String name,
    String description,
    Map<String, FieldDTO> fields
) {}
