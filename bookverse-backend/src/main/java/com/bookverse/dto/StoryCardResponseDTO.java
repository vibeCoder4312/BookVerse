package com.bookverse.dto;

import com.bookverse.entity.StoryLanguage;
import com.bookverse.entity.VoiceStyle;
import lombok.*;

// Deliberately lightweight - used for story CARDS in listing endpoints
// (trending, category browse, search results), where fetching the full
// description and episode list for every card in a grid would be wasteful.
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StoryCardResponseDTO {
    private Long id;
    private String title;
    private String shortDescription;
    private String coverImage;
    private String author;
    private String narrator;
    private VoiceStyle voiceStyle;
    private StoryLanguage language;
    private StoryCategoryResponseDTO category;
    private Boolean isPremium;
    private Integer totalDurationSeconds;
    private Integer totalEpisodes;
    private Long playCount;
}
