package com.bookverse.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StoryEpisodeResponseDTO {
    private Long id;
    private Integer episodeNumber;
    private String title;
    private String description;
    private Integer durationSeconds;
    // Null if this episode has no active audio yet (e.g. an admin created
    // the episode's text/metadata but hasn't uploaded a file). The frontend
    // player should treat null here as "not playable yet", not crash on it.
    private String audioUrl;
}
