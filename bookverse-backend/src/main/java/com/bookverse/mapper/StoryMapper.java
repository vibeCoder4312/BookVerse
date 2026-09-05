package com.bookverse.mapper;

import com.bookverse.dto.StoryCardResponseDTO;
import com.bookverse.dto.StoryCategoryResponseDTO;
import com.bookverse.dto.StoryDetailResponseDTO;
import com.bookverse.dto.StoryEpisodeResponseDTO;
import com.bookverse.entity.Story;
import com.bookverse.entity.StoryAudio;
import com.bookverse.entity.StoryEpisode;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class StoryMapper {

    public StoryCardResponseDTO toCardDTO(Story story) {
        return StoryCardResponseDTO.builder()
                .id(story.getId())
                .title(story.getTitle())
                .shortDescription(story.getShortDescription())
                .coverImage(story.getCoverImage())
                .author(story.getAuthor())
                .narrator(story.getNarrator())
                .voiceStyle(story.getVoiceStyle())
                .language(story.getLanguage())
                .category(toCategoryDTO(story))
                .isPremium(story.getIsPremium())
                .totalDurationSeconds(story.getTotalDurationSeconds())
                .totalEpisodes(story.getTotalEpisodes())
                .playCount(story.getPlayCount())
                .build();
    }

    public StoryDetailResponseDTO toDetailDTO(Story story, List<StoryEpisode> publishedEpisodes) {
        return StoryDetailResponseDTO.builder()
                .id(story.getId())
                .title(story.getTitle())
                .description(story.getDescription())
                .shortDescription(story.getShortDescription())
                .coverImage(story.getCoverImage())
                .author(story.getAuthor())
                .narrator(story.getNarrator())
                .voiceStyle(story.getVoiceStyle())
                .language(story.getLanguage())
                .category(toCategoryDTO(story))
                .isPremium(story.getIsPremium())
                .totalDurationSeconds(story.getTotalDurationSeconds())
                .totalEpisodes(story.getTotalEpisodes())
                .playCount(story.getPlayCount())
                .episodes(publishedEpisodes.stream().map(this::toEpisodeDTO).toList())
                .build();
    }

    public StoryEpisodeResponseDTO toEpisodeDTO(StoryEpisode episode) {
        // getActiveAudio() (defined on the entity itself) returns null if
        // no audio version is currently marked active - we handle that
        // gracefully here rather than throwing, since an episode existing
        // without playable audio yet is a normal admin-in-progress state.
        StoryAudio active = episode.getActiveAudio();
        String audioUrl = active != null ? "/media/" + active.getFilePath() : null;

        return StoryEpisodeResponseDTO.builder()
                .id(episode.getId())
                .episodeNumber(episode.getEpisodeNumber())
                .title(episode.getTitle())
                .description(episode.getDescription())
                .durationSeconds(episode.getDurationSeconds())
                .audioUrl(audioUrl)
                .build();
    }

    private StoryCategoryResponseDTO toCategoryDTO(Story story) {
        return StoryCategoryResponseDTO.builder()
                .id(story.getCategory().getId())
                .name(story.getCategory().getName())
                .build();
    }
}
