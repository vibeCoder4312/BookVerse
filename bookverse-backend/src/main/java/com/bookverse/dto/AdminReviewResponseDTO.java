package com.bookverse.dto;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminReviewResponseDTO {
    private Long id;
    private Long bookId;
    private String bookTitle;
    private String userName;
    private Integer rating;
    private String comment;
    private LocalDateTime createdAt;
}
