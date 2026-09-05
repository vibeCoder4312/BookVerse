package com.bookverse.dto;

import com.bookverse.entity.StoryLanguage;
import com.bookverse.entity.VoiceStyle;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StoryRequestDTO {

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Description is required")
    private String description;

    private String shortDescription;
    private String coverImage;

    @NotBlank(message = "Author is required")
    private String author;

    private String narrator;

    @NotNull(message = "Voice style is required")
    private VoiceStyle voiceStyle;

    @NotNull(message = "Language is required")
    private StoryLanguage language;

    @NotNull(message = "Category is required")
    private Long categoryId;

    @NotNull(message = "isPremium must be specified")
    private Boolean isPremium;
}
