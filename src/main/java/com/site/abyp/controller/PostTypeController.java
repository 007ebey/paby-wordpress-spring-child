package com.site.abyp.controller;

import com.site.abyp.dto.PostTypeDTO;
import com.site.abyp.model.PostType;
import com.site.abyp.service.PostTypeService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/post-types")
public class PostTypeController {

    private final PostTypeService postTypeService;

    public PostTypeController (PostTypeService postTypeService) {
        this.postTypeService = postTypeService;
    }

    @GetMapping
    public List<PostTypeDTO> getAll() {
        return postTypeService.getAll();
    }

    @GetMapping("/{name}")
    public PostTypeDTO getOne(@PathVariable String name) {
        return  postTypeService.getByName(name);
    }

}
