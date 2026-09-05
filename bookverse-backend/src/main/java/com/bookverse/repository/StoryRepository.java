package com.bookverse.repository;

import com.bookverse.entity.Story;
import com.bookverse.entity.StoryLanguage;
import com.bookverse.entity.StoryStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface StoryRepository extends JpaRepository<Story, Long> {

    // Only PUBLISHED stories should ever be visible to normal users -
    // draft/unpublished stories exist in the table (so an admin can edit
    // them before going live) but must never leak into public listings.
    Page<Story> findByStatus(StoryStatus status, Pageable pageable);

    Page<Story> findByStatusAndCategory_Name(StoryStatus status, String categoryName, Pageable pageable);

    Page<Story> findByStatusAndLanguage(StoryStatus status, StoryLanguage language, Pageable pageable);

    Page<Story> findByStatusAndIsPremiumFalse(StoryStatus status, Pageable pageable);

    @Query("SELECT s FROM Story s WHERE s.status = :status AND (" +
            "LOWER(s.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(s.author) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(s.narrator) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(s.category.name) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Story> searchPublished(@Param("status") StoryStatus status, @Param("keyword") String keyword, Pageable pageable);
}
