package com.bookverse.controller;

import com.bookverse.dto.ReviewRequestDTO;
import com.bookverse.dto.ReviewResponseDTO;
import com.bookverse.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

// Notice this controller handles TWO different URL patterns:
// /api/books/{bookId}/reviews (list + create, nested under a book)
// /api/reviews/{reviewId}     (update + delete, standalone by review id)
// Both are legitimate REST design - listing/creating reviews naturally
// belongs to "a book's reviews", but editing/deleting only needs the
// review's own id, not its book.
@RestController
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping("/api/books/{bookId}/reviews")
    public ResponseEntity<List<ReviewResponseDTO>> getReviews(@PathVariable Long bookId) {
        return ResponseEntity.ok(reviewService.getReviewsForBook(bookId));
    }

    @PostMapping("/api/books/{bookId}/reviews")
    public ResponseEntity<ReviewResponseDTO> addReview(
            @PathVariable Long bookId,
            @Valid @RequestBody ReviewRequestDTO dto
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(reviewService.addReview(bookId, dto));
    }

    @PutMapping("/api/reviews/{reviewId}")
    public ResponseEntity<ReviewResponseDTO> updateReview(
            @PathVariable Long reviewId,
            @Valid @RequestBody ReviewRequestDTO dto
    ) {
        return ResponseEntity.ok(reviewService.updateReview(reviewId, dto));
    }

    @DeleteMapping("/api/reviews/{reviewId}")
    public ResponseEntity<Void> deleteReview(@PathVariable Long reviewId) {
        reviewService.deleteReview(reviewId);
        return ResponseEntity.noContent().build();
    }
}
