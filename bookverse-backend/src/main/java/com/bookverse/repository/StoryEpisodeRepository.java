package com.bookverse.repository;

import com.bookverse.entity.Story;
import com.bookverse.entity.StoryEpisode;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface StoryEpisodeRepository extends JpaRepository<StoryEpisode, Long> {
    List<StoryEpisode> findByStoryAndIsPublishedTrueOrderByEpisodeNumberAsc(Story story);

    // Used by admin management (Phase 5) where drafts need to be visible too
    List<StoryEpisode> findByStoryOrderByEpisodeNumberAsc(Story story);

    Optional<StoryEpisode> findByStoryAndEpisodeNumber(Story story, Integer episodeNumber);
}
