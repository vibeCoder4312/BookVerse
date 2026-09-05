package com.bookverse.dto;

import com.bookverse.entity.StoryLanguage;
import com.bookverse.entity.VoiceStyle;
import lombok.*;
import java.util.List;

// Used for the single story details page - the ONE place where the full
// description and complete episode list are actually needed at once.
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StoryDetailResponseDTO {
    private Long id;
    private String title;
    private String description;
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
    private List<StoryEpisodeResponseDTO> episodes;
}
