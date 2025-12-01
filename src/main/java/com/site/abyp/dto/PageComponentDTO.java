package com.site.abyp.dto;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Map;

public record PageComponentDTO(
        Long id,
        String name,
        String schemaName,
        String templateName,
        Map<String, Object> data,
        Instant createdAt,
        Instant updatedAt
) {}
