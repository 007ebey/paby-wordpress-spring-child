package com.site.abyp.repository;

import com.site.abyp.model.PostType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PostTypeRepository extends JpaRepository<PostType, Long> {

    // Optional: find by name if you need it
    boolean existsByName(String name);
    Optional<PostType> findByName(String name);

}

