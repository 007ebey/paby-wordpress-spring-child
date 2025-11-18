package com.site.abyp.model;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@Setter
@Entity
@Table(name = "post_types")
@NoArgsConstructor
public class PostType extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String name;

    private String description;

    @OneToMany(mappedBy = "postType", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Post> posts;

    @OneToMany(mappedBy = "postType", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<PostTypeField> fields;

    public PostType(String name, String description) {
        this.name = name;
        this.description = description;
    }

    @Transient
    private Map<String, Object> options = new HashMap<>();

}
