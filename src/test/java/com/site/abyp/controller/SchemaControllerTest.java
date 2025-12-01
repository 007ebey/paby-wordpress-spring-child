package com.site.abyp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.site.abyp.dto.FieldDTO;
import com.site.abyp.dto.PostTypeDTO;
import com.site.abyp.service.SchemaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        controllers = SchemaController.class,
        excludeAutoConfiguration = {
                org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration.class,
                org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration.class,
                org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration.class,
                SecurityAutoConfiguration.class
        }
)
public class SchemaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private SchemaService schemaService;

    @Test
    public void testCreateSchema() throws Exception {
        FieldDTO titleField = new FieldDTO(10L, "title", "text", true, "Untitled", List.of());
        PostTypeDTO dto = new PostTypeDTO(1L, "project", "Project Post Type", Map.of("title", titleField));

        when(schemaService.create(any(PostTypeDTO.class))).thenReturn(dto);

        mockMvc.perform(post("/api/schema")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("project"))
                .andExpect(jsonPath("$.fields.title.name").value("title"))
                .andExpect(jsonPath("$.fields.title.type").value("text"))
                .andExpect(jsonPath("$.fields.title.required").value(true))
                .andExpect(jsonPath("$.fields.title.defaultValue").value("Untitled"));

        mockMvc.perform(get("/api/schema/article"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(3))
                .andExpect(jsonPath("$.name").value("article"))
                .andExpect(jsonPath("$.fields.seo.name").value("seo"))
                .andExpect(jsonPath("$.fields.seo.type").value("text"))
                .andExpect(jsonPath("$.fields.seo.required").value(false));

    }
}
