package com.bookverse.mapper;

import com.bookverse.dto.AuthorSummaryDTO;
import com.bookverse.dto.BookResponseDTO;
import com.bookverse.entity.Book;
import com.bookverse.entity.Category;
import com.bookverse.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.stream.Collectors;

// We're writing this mapper BY HAND (no MapStruct/ModelMapper library)
// so you can see exactly what's happening: reading fields off the Entity
// and copying them into a DTO shape. Libraries that auto-generate this
// are nice once you understand the concept - not before.
@Component
@RequiredArgsConstructor
public class BookMapper {

    private final ReviewRepository reviewRepository;

    public BookResponseDTO toResponseDTO(Book book) {
        // NOTE: this runs one extra query per book to compute its rating.
        // Fine for a college project's data volume - if this were serving
        // millions of requests, we'd instead maintain a cached rating on
        // the Book row itself, updated whenever a review changes.
        Double avgRating = reviewRepository.findAverageRatingByBookId(book.getId());
        long reviewCount = reviewRepository.countByBookId(book.getId());

        return BookResponseDTO.builder()
                .id(book.getId())
                .title(book.getTitle())
                .description(book.getDescription())
                .author(AuthorSummaryDTO.builder()
                        .id(book.getAuthor().getId())
                        .name(book.getAuthor().getName())
                        .build())
                .contentType(book.getContentType())
                .coverUrl(book.getCoverUrl())
                .publicationYear(book.getPublicationYear())
                .views(book.getViews())
                .categories(book.getCategories().stream()
                        .map(Category::getName)
                        .collect(Collectors.toSet()))
                .averageRating(avgRating != null ? Math.round(avgRating * 10.0) / 10.0 : null)
                .reviewCount(reviewCount)
                .createdAt(book.getCreatedAt())
                .content(book.getContent())
                .build();
    }
}
