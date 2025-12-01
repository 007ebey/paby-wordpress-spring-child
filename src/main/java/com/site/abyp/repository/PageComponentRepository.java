package com.site.abyp.repository;

import com.site.abyp.model.PageComponent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PageComponentRepository extends JpaRepository<PageComponent, Long> {
    List<PageComponent> findBySchema_Name(String schemaName);
}
