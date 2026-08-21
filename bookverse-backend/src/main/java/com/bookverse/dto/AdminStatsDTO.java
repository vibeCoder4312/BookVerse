package com.bookverse.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminStatsDTO {
    private long totalUsers;
    private long totalBooks;
    private long totalManga;
    private long totalComics;
    private long totalReviews;
    private long totalCategories;
}
