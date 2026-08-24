package com.bookverse.dto;

import com.bookverse.entity.ContentType;
import lombok.*;
import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookResponseDTO {
    private Long id;
    private String title;
    private String description;
    private AuthorSummaryDTO author;
    private ContentType contentType;
    private String coverUrl;
    private Integer publicationYear;
    private Long views;
    private Set<String> categories; // just names, not full Category objects
    private Double averageRating; // null if the book has no reviews yet
    private Long reviewCount;
    private LocalDateTime createdAt;

    // Populated only for books that have readable text (Phase 14).
    // Frontend uses this to decide whether to show a "Read" button at all.
    private String content;
}
