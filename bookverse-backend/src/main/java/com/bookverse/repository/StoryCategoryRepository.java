package com.bookverse.repository;

import com.bookverse.entity.StoryCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface StoryCategoryRepository extends JpaRepository<StoryCategory, Long> {
    Optional<StoryCategory> findByName(String name);
}
