package com.bookverse.service;

import com.bookverse.dto.ReviewRequestDTO;
import com.bookverse.dto.ReviewResponseDTO;
import java.util.List;

public interface ReviewService {
    List<ReviewResponseDTO> getReviewsForBook(Long bookId);
    ReviewResponseDTO addReview(Long bookId, ReviewRequestDTO dto);
    ReviewResponseDTO updateReview(Long reviewId, ReviewRequestDTO dto);
    void deleteReview(Long reviewId);
}
