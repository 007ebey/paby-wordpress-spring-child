package com.site.abyp.repository;

import com.site.abyp.model.PostType;
import com.site.abyp.model.PostTypeField;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostTypeFieldRepository extends JpaRepository<PostTypeField, Long> {
    // Get all fields for a PostType (root level only = no parent)
    List<PostTypeField> findByPostTypeAndParentFieldIsNull(PostType postType);

    // Get all fields for a PostType (including nested)
    List<PostTypeField> findByPostType(PostType postType);

    // Get children of a specific field (nested fields)
    List<PostTypeField> findByParentField(PostTypeField parentField);

    // Fetch by name inside a PostType
    PostTypeField findByPostTypeAndName(PostType postType, String name);
}
