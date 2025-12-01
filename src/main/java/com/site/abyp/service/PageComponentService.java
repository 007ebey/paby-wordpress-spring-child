package com.site.abyp.service;

import com.site.abyp.dto.PageComponentDTO;
import com.site.abyp.dto.PostTypeDTO;
import com.site.abyp.model.PageComponent;
import com.site.abyp.model.PostType;
import com.site.abyp.repository.PageComponentRepository;
import com.site.abyp.repository.PostTypeRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PageComponentService {

    private final PageComponentRepository repository;
    private final PostTypeRepository postTypeRepository;
    private final PostTypeService postTypeService;

    public PageComponentService (
            PageComponentRepository repository,
            PostTypeService postTypeService,
            PostTypeRepository postTypeRepository
    ) {
        this.repository = repository;
        this.postTypeService = postTypeService;
        this.postTypeRepository = postTypeRepository;
    }

    public PageComponentDTO create(PageComponentDTO dto) {
        PostTypeDTO schemaDTO = postTypeService.getByName(dto.schemaName());
        PostType schemaEntity = postTypeRepository.findById(schemaDTO.id())
                .orElseThrow(() -> new RuntimeException("Schema entity missing"))
                ;
        PageComponent component = new PageComponent();
        component.setName(dto.name());
        component.setSchema(schemaEntity);
        component.setTemplateName(dto.templateName());
        component.setData(dto.data());

        PageComponent saved = repository.save(component);
        return new PageComponentDTO(
                saved.getId(),
                saved.getName(),
                saved.getSchema().getName(),
                saved.getTemplateName(),
                saved.getData(),
                saved.getCreatedAt(),
                saved.getUpdatedAt()
        );
    }

    public List<PageComponentDTO> getAll() {
        return repository.findAll()
                .stream()
                .map(this::toDTO)
                .toList();
    }

    public List<PageComponentDTO> getBySchema(String schemaName) {
        PostTypeDTO schema = postTypeService.getByName(schemaName);
        return repository.findBySchema_Name(schemaName)
                .stream()
                .map(this::toDTO)
                .toList();
    }

    private  PageComponentDTO toDTO(PageComponent pc) {
        return new PageComponentDTO(
                pc.getId(),
                pc.getName(),
                pc.getSchema().getName(),
                pc.getTemplateName(),
                pc.getData(),
                pc.getCreatedAt(),
                pc.getUpdatedAt()
        );
    }
}
