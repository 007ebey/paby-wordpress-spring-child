package com.site.abyp.controller;

import com.site.abyp.dto.PageComponentDTO;
import com.site.abyp.service.PageComponentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/components")
public class PageComponentController {

    private final PageComponentService service;

    public PageComponentController(PageComponentService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody PageComponentDTO dto) {
        try {
            PageComponentDTO created = service.create(dto);
            return ResponseEntity.status(201).body(created);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    java.util.Map.of("error", e.getMessage())
            );
        }
    }

    @GetMapping
    public List<PageComponentDTO> getAll() {
        return service.getAll();
    }

    @GetMapping("/schema/{schema}")
    public List<PageComponentDTO> getBySchema(@PathVariable String schema) {
        return service.getBySchema(schema);
    }
}
