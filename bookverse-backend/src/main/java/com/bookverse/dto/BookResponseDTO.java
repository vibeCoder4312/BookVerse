package com.bookverse.dto;

import com.bookverse.entity.ContentType;
import lombok.*;
import java.math.BigDecimal;
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
    private String content;
    private BigDecimal price; // NEW - real per-book price for the Buy Physical Copy feature
}
