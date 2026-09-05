package com.bookverse.service;

import com.bookverse.dto.StoryCardResponseDTO;
import com.bookverse.dto.StoryDetailResponseDTO;
import com.bookverse.dto.StoryEpisodeResponseDTO;
import com.bookverse.entity.StoryLanguage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface StoryService {
    Page<StoryCardResponseDTO> getPublishedStories(Pageable pageable);
    StoryDetailResponseDTO getStoryDetail(Long id);

    // Phase 4 additions - all built on the repository queries already
    // written in Phase 3's foundation.
    Page<StoryCardResponseDTO> getStoriesByCategory(String categoryName, Pageable pageable);
    Page<StoryCardResponseDTO> getStoriesByLanguage(StoryLanguage language, Pageable pageable);
    Page<StoryCardResponseDTO> searchStories(String keyword, Pageable pageable);
    Page<StoryCardResponseDTO> getFreeStories(Pageable pageable);
    List<StoryEpisodeResponseDTO> getStoryEpisodes(Long storyId);
    StoryEpisodeResponseDTO getEpisodeById(Long episodeId);
}
