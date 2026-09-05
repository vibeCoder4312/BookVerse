package com.bookverse.repository;

import com.bookverse.entity.Story;
import com.bookverse.entity.StoryEpisode;
import com.bookverse.entity.StoryProgress;
import com.bookverse.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface StoryProgressRepository extends JpaRepository<StoryProgress, Long> {
    Optional<StoryProgress> findByUserAndEpisode(User user, StoryEpisode episode);

    // "Continue Listening" needs the most recently listened episode PER
    // STORY - ordering by lastListenedAt descending lets the service layer
    // pick the first matching row per story easily.
    List<StoryProgress> findByUserAndStoryOrderByLastListenedAtDesc(User user, Story story);

    List<StoryProgress> findByUserOrderByLastListenedAtDesc(User user);
}
