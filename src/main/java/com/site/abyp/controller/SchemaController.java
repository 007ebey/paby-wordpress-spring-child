package com.site.abyp.controller;

import com.site.abyp.dto.PostTypeDTO;
import com.site.abyp.service.SchemaService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/schema")
public class SchemaController {

    private final SchemaService schemaService;

    public SchemaController(SchemaService schemaService) {
        this.schemaService = schemaService;
    }

    @PostMapping
    public PostTypeDTO createSchema(@RequestBody PostTypeDTO dto) {
        return schemaService.create(dto);
    }

    @GetMapping
    public List<PostTypeDTO> getAllSchemas() {
        return schemaService.getAll();
    }

    @GetMapping("/{name}")
    public PostTypeDTO getSchema(@PathVariable String name) {
        return schemaService.getByName(name);
    }

}
