package com.bookverse.repository;

import com.bookverse.entity.StoryAudio;
import com.bookverse.entity.StoryEpisode;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface StoryAudioRepository extends JpaRepository<StoryAudio, Long> {
    List<StoryAudio> findByEpisode(StoryEpisode episode);
    Optional<StoryAudio> findByEpisodeAndIsActiveTrue(StoryEpisode episode);
}
