package com.site.abyp.repository;

import com.site.abyp.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {

    List<Post> findByPostType_Name(String name);

    Optional<Post> findBySlugAndPostType_Name(String slug, String type);

    Optional<Post> findBySlug(String slug);

    boolean existsBySlug(String slug);
}
