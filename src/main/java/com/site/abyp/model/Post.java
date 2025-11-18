package com.site.abyp.model;

import com.site.abyp.config.JsonMapConverter;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLRestriction;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "posts")
@SQLRestriction("deleted = false")
public class Post extends BaseEntity {

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "post_type_id", nullable = false)
    private PostType postType;

    @Column(nullable = false)
    private boolean published = false;

    private boolean deleted = false;

    @Version
    private Long version;

    @Column(nullable = false, unique = true)
    private String slug;

    @Column(columnDefinition = "jsonb")
    @Convert(converter = JsonMapConverter.class)
    private Map<String, Object> meta = new HashMap<>();

}
