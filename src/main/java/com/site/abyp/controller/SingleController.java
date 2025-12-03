package com.site.abyp.controller;

import com.site.abyp.dto.PostDTO;
import com.site.abyp.dto.PostTypeDTO;
import com.site.abyp.service.PostService;
import com.site.abyp.service.PostTypeService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/post/{type}")
public class SingleController {

    private final PostTypeService postTypeService;
    private final PostService postService;

    public SingleController(
            PostTypeService postTypeService,
            PostService postService
    ) {
        this.postTypeService = postTypeService;
        this.postService = postService;
    }

    @GetMapping("/{slug}")
    public String single(
            @PathVariable String type,
            @PathVariable String slug,
            Model model
    ) {
        PostTypeDTO postTypeDTO = postTypeService.getByName(type);
        if (postTypeDTO == null) {
            return "error/404";
        }

        PostDTO found = postService.findBySlugAndType(slug, type);
        if (found == null) {
            return "error/404";
        }

        model.addAttribute("postType", postTypeDTO);
        model.addAttribute("post", found);
        model.addAttribute("meta", found.meta());

        model.addAttribute("contentView", type + "/single" );
        return "index";
    }

}
