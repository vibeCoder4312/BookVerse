package com.bookverse.service.impl;

import com.bookverse.dto.ReviewRequestDTO;
import com.bookverse.dto.ReviewResponseDTO;
import com.bookverse.entity.Book;
import com.bookverse.entity.Review;
import com.bookverse.entity.User;
import com.bookverse.exception.DuplicateResourceException;
import com.bookverse.exception.ForbiddenException;
import com.bookverse.exception.ResourceNotFoundException;
import com.bookverse.repository.BookRepository;
import com.bookverse.repository.ReviewRepository;
import com.bookverse.service.ReviewService;
import com.bookverse.util.CurrentUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final BookRepository bookRepository;
    private final CurrentUserService currentUserService;

    @Override
    public List<ReviewResponseDTO> getReviewsForBook(Long bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + bookId));

        return reviewRepository.findByBookOrderByCreatedAtDesc(book).stream()
                .map(this::toResponseDTO)
                .toList();
    }

    @Override
    public ReviewResponseDTO addReview(Long bookId, ReviewRequestDTO dto) {
        User user = currentUserService.getCurrentUser();
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + bookId));

        if (reviewRepository.findByUserAndBook(user, book).isPresent()) {
            throw new DuplicateResourceException("You've already reviewed this book - try editing your existing review instead");
        }

        Review review = Review.builder()
                .user(user)
                .book(book)
                .rating(dto.getRating())
                .comment(dto.getComment())
                .build();

        return toResponseDTO(reviewRepository.save(review));
    }

    @Override
    public ReviewResponseDTO updateReview(Long reviewId, ReviewRequestDTO dto) {
        User user = currentUserService.getCurrentUser();
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found with id: " + reviewId));

        // Ownership check: comparing IDs, not object references - two
        // separately-loaded User objects for the same row are never ==
        // to each other in Java, but their ids will be equal.
        if (!review.getUser().getId().equals(user.getId())) {
            throw new ForbiddenException("You can only edit your own reviews");
        }

        review.setRating(dto.getRating());
        review.setComment(dto.getComment());
        return toResponseDTO(reviewRepository.save(review));
    }

    @Override
    public void deleteReview(Long reviewId) {
        User user = currentUserService.getCurrentUser();
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found with id: " + reviewId));

        if (!review.getUser().getId().equals(user.getId())) {
            throw new ForbiddenException("You can only delete your own reviews");
        }

        reviewRepository.delete(review);
    }

    private ReviewResponseDTO toResponseDTO(Review review) {
        return ReviewResponseDTO.builder()
                .id(review.getId())
                .userId(review.getUser().getId())
                .userName(review.getUser().getName())
                .rating(review.getRating())
                .comment(review.getComment())
                .createdAt(review.getCreatedAt())
                .updatedAt(review.getUpdatedAt())
                .build();
    }
}
