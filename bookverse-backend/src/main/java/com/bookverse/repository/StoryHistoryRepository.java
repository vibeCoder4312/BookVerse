package com.bookverse.repository;

import com.bookverse.entity.Story;
import com.bookverse.entity.StoryHistory;
import com.bookverse.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface StoryHistoryRepository extends JpaRepository<StoryHistory, Long> {
    List<StoryHistory> findByUserOrderByPlayedAtDesc(User user);
    Optional<StoryHistory> findByUserAndStory(User user, Story story);
    void deleteByUser(User user); // powers the "Clear History" button
}
