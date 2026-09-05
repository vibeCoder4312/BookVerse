package com.bookverse.service.impl;

import com.bookverse.dto.StoryCardResponseDTO;
import com.bookverse.dto.StoryDetailResponseDTO;
import com.bookverse.dto.StoryEpisodeResponseDTO;
import com.bookverse.entity.Story;
import com.bookverse.entity.StoryEpisode;
import com.bookverse.entity.StoryLanguage;
import com.bookverse.entity.StoryStatus;
import com.bookverse.exception.ResourceNotFoundException;
import com.bookverse.mapper.StoryMapper;
import com.bookverse.repository.StoryEpisodeRepository;
import com.bookverse.repository.StoryRepository;
import com.bookverse.service.StoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StoryServiceImpl implements StoryService {

    private final StoryRepository storyRepository;
    private final StoryEpisodeRepository episodeRepository;
    private final StoryMapper storyMapper;

    @Override
    public Page<StoryCardResponseDTO> getPublishedStories(Pageable pageable) {
        return storyRepository.findByStatus(StoryStatus.PUBLISHED, pageable)
                .map(storyMapper::toCardDTO);
    }

    @Override
    public StoryDetailResponseDTO getStoryDetail(Long id) {
        Story story = getPublishedStoryOrThrow(id);
        var publishedEpisodes = episodeRepository.findByStoryAndIsPublishedTrueOrderByEpisodeNumberAsc(story);
        return storyMapper.toDetailDTO(story, publishedEpisodes);
    }

    @Override
    public Page<StoryCardResponseDTO> getStoriesByCategory(String categoryName, Pageable pageable) {
        return storyRepository.findByStatusAndCategory_Name(StoryStatus.PUBLISHED, categoryName, pageable)
                .map(storyMapper::toCardDTO);
    }

    @Override
    public Page<StoryCardResponseDTO> getStoriesByLanguage(StoryLanguage language, Pageable pageable) {
        return storyRepository.findByStatusAndLanguage(StoryStatus.PUBLISHED, language, pageable)
                .map(storyMapper::toCardDTO);
    }

    @Override
    public Page<StoryCardResponseDTO> searchStories(String keyword, Pageable pageable) {
        return storyRepository.searchPublished(StoryStatus.PUBLISHED, keyword, pageable)
                .map(storyMapper::toCardDTO);
    }

    @Override
    public Page<StoryCardResponseDTO> getFreeStories(Pageable pageable) {
        return storyRepository.findByStatusAndIsPremiumFalse(StoryStatus.PUBLISHED, pageable)
                .map(storyMapper::toCardDTO);
    }

    @Override
    public List<StoryEpisodeResponseDTO> getStoryEpisodes(Long storyId) {
        Story story = getPublishedStoryOrThrow(storyId);
        return episodeRepository.findByStoryAndIsPublishedTrueOrderByEpisodeNumberAsc(story).stream()
                .map(storyMapper::toEpisodeDTO)
                .toList();
    }

    @Override
    public StoryEpisodeResponseDTO getEpisodeById(Long episodeId) {
        StoryEpisode episode = episodeRepository.findById(episodeId)
                .orElseThrow(() -> new ResourceNotFoundException("Episode not found with id: " + episodeId));

        // Same visibility rule as everywhere else: an unpublished episode,
        // or an episode belonging to an unpublished story, doesn't exist
        // as far as a public request is concerned.
        if (!episode.getIsPublished() || episode.getStory().getStatus() != StoryStatus.PUBLISHED) {
            throw new ResourceNotFoundException("Episode not found with id: " + episodeId);
        }

        return storyMapper.toEpisodeDTO(episode);
    }

    // Shared by getStoryDetail, getStoryEpisodes - both need "find this
    // story, but only if it's actually published" with the same 404
    // behavior for anything else.
    private Story getPublishedStoryOrThrow(Long id) {
        Story story = storyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Story not found with id: " + id));

        if (story.getStatus() != StoryStatus.PUBLISHED) {
            throw new ResourceNotFoundException("Story not found with id: " + id);
        }
        return story;
    }
}
