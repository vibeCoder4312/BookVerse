package com.bookverse.service;

import com.bookverse.dto.*;
import org.springframework.web.multipart.MultipartFile;

public interface AdminStoryService {
    StoryDetailResponseDTO createStory(StoryRequestDTO dto);
    StoryDetailResponseDTO updateStory(Long storyId, StoryRequestDTO dto);
    void deleteStory(Long storyId);
    void publishStory(Long storyId);
    void unpublishStory(Long storyId);

    StoryEpisodeResponseDTO addEpisode(Long storyId, StoryEpisodeRequestDTO dto);
    StoryEpisodeResponseDTO updateEpisode(Long episodeId, StoryEpisodeRequestDTO dto);
    void deleteEpisode(Long episodeId);
    void publishEpisode(Long episodeId);
    void unpublishEpisode(Long episodeId);
    void reorderEpisodes(Long storyId, ReorderEpisodesRequestDTO dto);

    StoryEpisodeResponseDTO uploadEpisodeAudio(Long episodeId, MultipartFile file);
}
