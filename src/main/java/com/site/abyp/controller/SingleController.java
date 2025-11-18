package com.site.abyp.controller;

import com.site.abyp.dto.PostDTO;
import com.site.abyp.dto.PostTypeDTO;
import com.site.abyp.model.Post;
import com.site.abyp.model.PostType;
import com.site.abyp.repository.PostRepository;
import com.site.abyp.repository.PostTypeRepository;
import com.site.abyp.service.PostService;
import com.site.abyp.service.PostTypeService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;

@Controller
@RequestMapping
public class SingleController {

    private final PostTypeService postTypeService;
    private final PostService postService;

    public SingleController(PostTypeService postTypeService, PostService postService) {
       this.postTypeService = postTypeService;
       this.postService = postService;
    }

    @GetMapping("/{type}/{slug}")
    public String single (
            @PathVariable String type,
            @PathVariable String slug,
            Model model
    ) {
        PostTypeDTO postType = postTypeService.getByName(type);
        PostDTO post = postService.findBySlugAndType(slug, type);
        model.addAttribute("postType", postType);
        model.addAttribute("post", post);
        return "single-" + type;
    }

}
