package com.bookverse.repository;

import com.bookverse.entity.Story;
import com.bookverse.entity.StoryFavorite;
import com.bookverse.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface StoryFavoriteRepository extends JpaRepository<StoryFavorite, Long> {
    List<StoryFavorite> findByUserOrderByCreatedAtDesc(User user);
    Optional<StoryFavorite> findByUserAndStory(User user, Story story);
    boolean existsByUserAndStory(User user, Story story);
    void deleteByUserAndStory(User user, Story story);
}
