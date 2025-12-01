package com.site.abyp.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Map;

public record PostDTO(
        Long id,
        String title,
        String content,

        String slug,

        Long authorId,
        String authorName,

        Long postTypeId,
        String postTypeName,

        boolean published,

        LocalDateTime createdAt,
        LocalDateTime updatedAt,

        Long version,
        Map<String, Object> meta
) {}
