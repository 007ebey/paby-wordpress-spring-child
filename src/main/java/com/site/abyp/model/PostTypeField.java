package com.site.abyp.model;

import com.site.abyp.config.JsonMapConverter;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Entity
@Data
@Table(name = "post_type_field")
@NoArgsConstructor
public class PostTypeField {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String type;
    private boolean required;

    @Column(columnDefinition = "jsonb")
    @Convert(converter = JsonMapConverter.class)
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> defaultValue = new HashMap<>();

    @ManyToOne
    @JoinColumn(name = "post_type_id")
    private PostType postType;

    @ManyToOne
    @JoinColumn(name = "parent_field_id")
    private PostTypeField parentField;

    @OneToMany(mappedBy = "parentField", cascade = CascadeType.ALL)
    private List<PostTypeField> subFields;

    // getters/setters
}