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

import java.util.List;

@Controller
@RequestMapping("/page/{type}")
public class ArchiveController {

    private final PostTypeService postTypeService;
    private final PostService postService;

    public ArchiveController(
                             PostTypeService postTypeService,
                             PostService postService
                             ) {
        this.postTypeService = postTypeService;
        this.postService = postService;
    }

    @GetMapping
    public String archive(@PathVariable String type, Model model) {
        PostTypeDTO postType = postTypeService.getByName(type);
        List<PostDTO> posts = postService.findByType(type);
        model.addAttribute("postType", postType);
        model.addAttribute("posts", posts);
        return "archive-" + type;
    }

}
