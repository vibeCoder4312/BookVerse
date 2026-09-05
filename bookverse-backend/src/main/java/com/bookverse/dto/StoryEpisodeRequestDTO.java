package com.bookverse.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StoryEpisodeRequestDTO {

    @NotNull(message = "Episode number is required")
    @Min(value = 1, message = "Episode number must be at least 1")
    private Integer episodeNumber;

    @NotBlank(message = "Title is required")
    private String title;

    private String description;

    @Min(value = 0, message = "Duration cannot be negative")
    private Integer durationSeconds;
}
