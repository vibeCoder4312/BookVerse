package com.bookverse.mapper;

import com.bookverse.dto.AuthorSummaryDTO;
import com.bookverse.dto.BookResponseDTO;
import com.bookverse.entity.Book;
import com.bookverse.entity.Category;
import com.bookverse.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class BookMapper {

    private final ReviewRepository reviewRepository;

    public BookResponseDTO toResponseDTO(Book book) {
        Double avgRating = reviewRepository.findAverageRatingByBookId(book.getId());
        Long reviewCount = reviewRepository.countByBookId(book.getId());

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
                .averageRating(avgRating)
                .reviewCount(reviewCount)
                .createdAt(book.getCreatedAt())
                .content(book.getContent())
                .price(book.getPrice()) // NEW
                .build();
    }
}
