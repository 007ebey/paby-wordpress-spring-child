package com.site.abyp.controller;

import com.site.abyp.dto.PostDTO;
import com.site.abyp.dto.PostTypeDTO;
import com.site.abyp.service.PostService;
import com.site.abyp.service.PostTypeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/page/{type}")
public class SinglePageController {

    private final PostTypeService postTypeService;
    private final PostService postService;

    public SinglePageController(
            PostTypeService postTypeService,
            PostService postService
    ) {
        this.postTypeService = postTypeService;
        this.postService = postService;
    }

    @GetMapping("/{slug}")
    public ResponseEntity<?> single(
            @PathVariable String type,
            @PathVariable String slug,
            Model model
    ) {
        PostTypeDTO postTypeDTO = postTypeService.getByName(type);
        if (postTypeDTO == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of(
                       "error", "Post type not found",
                       "type", type
                    ));

        }

        PostDTO found = postService.findBySlugAndType(slug, type);
        if (found == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of(
                            "error", "Post not found",
                            "slug", slug,
                            "type", type
                    ));
        }

        model.addAttribute("postType", postTypeDTO);
        model.addAttribute("post", found);
        model.addAttribute("meta", found.meta());

        String view = "single-" + type;
        return ResponseEntity.ok(view);

    }

}
