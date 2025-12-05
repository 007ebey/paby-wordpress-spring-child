package com.site.abyp.controller;

import com.site.abyp.dto.PostDTO;
import com.site.abyp.dto.PostTypeDTO;
import com.site.abyp.service.PostService;
import com.site.abyp.service.PostTypeService;
import com.site.abyp.service.SchemaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    private final PostService postService;
    private final SchemaService schemaService;
    private final PostTypeService postTypeService;

    public PostController(PostService postService,
                          SchemaService schemaService,
                          PostTypeService postTypeService
    ) {
        this.postService = postService;
        this.schemaService = schemaService;
        this.postTypeService = postTypeService;
    }

    @PostMapping("/{postType}")
    public ResponseEntity<?> createPost(
            @PathVariable String postType,
            @RequestBody PostDTO dto
    ) {
        PostTypeDTO type = schemaService.getByName(postType);
        if (type == null) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of(
                            "error", "Post type does not exist",
                            "postType", postType
                    ));
        }

        var missing = postService.validateRequiredFields(dto, type);
        if (!missing.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of(
                            "error", "Missing required fields",
                            "missing", missing
                    ));
        }

        PostDTO cleaned = postService.collectSchemeFields(dto, type);

        PostDTO created = postService.create(cleaned, type.name());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(created);
    }

    @GetMapping("/{postType}")
    public ResponseEntity<?> getPostsByType(@PathVariable String postType) {
        PostTypeDTO type = schemaService.getByName(postType);
        if (type == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of(
                            "error", "Post type does exist",
                            "postType", postType
                    ));
        }
        List<PostDTO> posts = postService.findAllByType(type);
        return ResponseEntity.ok(posts);
    }

    @PutMapping("/{postType}")
    public ResponseEntity<?> updatePost(
            @PathVariable String postType,
            @RequestBody PostDTO dto
    ) {
        PostTypeDTO type = schemaService.getByName(postType);
        if (type == null) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of(
                            "error", "Post type does not exist",
                            "postType", postType
                    ));
        }
        PostDTO existing = postService.findBySlugAndType(dto.slug(), postType);
        PostDTO cleaned = postService.collectSchemeFields(dto, type);
        System.out.println("-------- DEBUG" + cleaned.postTypeName());
        PostDTO update = postService.update(existing.id(), cleaned);
        return  ResponseEntity.ok(update);
    }
}
