package com.bookverse.service.impl;

import com.bookverse.dto.AdminReviewResponseDTO;
import com.bookverse.dto.AdminStatsDTO;
import com.bookverse.dto.AdminUserResponseDTO;
import com.bookverse.entity.ContentType;
import com.bookverse.entity.Review;
import com.bookverse.entity.Role;
import com.bookverse.entity.User;
import com.bookverse.exception.BadRequestException;
import com.bookverse.exception.ResourceNotFoundException;
import com.bookverse.repository.*;
import com.bookverse.service.AdminService;
import com.bookverse.util.CurrentUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final ReviewRepository reviewRepository;
    private final CategoryRepository categoryRepository;
    private final CurrentUserService currentUserService;

    @Override
    public AdminStatsDTO getStats() {
        return AdminStatsDTO.builder()
                .totalUsers(userRepository.count())
                .totalBooks(bookRepository.count())
                .totalManga(bookRepository.countByContentType(ContentType.MANGA))
                .totalComics(bookRepository.countByContentType(ContentType.COMIC))
                .totalReviews(reviewRepository.count())
                .totalCategories(categoryRepository.count())
                .build();
    }

    @Override
    public Page<AdminUserResponseDTO> getUsers(String search, Pageable pageable) {
        Page<User> users = (search == null || search.isBlank())
                ? userRepository.findAll(pageable)
                : userRepository.findByNameContainingIgnoreCaseOrEmailContainingIgnoreCase(search, search, pageable);

        return users.map(user -> AdminUserResponseDTO.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .enabled(user.getEnabled())
                .createdAt(user.getCreatedAt())
                .build());
    }

    @Override
    public void setUserRole(Long userId, Role role) {
        User admin = currentUserService.getCurrentUser();
        if (admin.getId().equals(userId)) {
            // Safety guard: without this, an admin could accidentally
            // demote themselves to USER and lock themselves out of every
            // admin endpoint with no way back in except direct SQL.
            throw new BadRequestException("You cannot change your own role");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        user.setRole(role);
        userRepository.save(user);
    }

    @Override
    public void setUserEnabled(Long userId, boolean enabled) {
        User admin = currentUserService.getCurrentUser();
        if (admin.getId().equals(userId)) {
            throw new BadRequestException("You cannot disable your own account");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        user.setEnabled(enabled);
        userRepository.save(user);
    }

    @Override
    public Page<AdminReviewResponseDTO> getAllReviews(Pageable pageable) {
        return reviewRepository.findAll(pageable).map(review -> AdminReviewResponseDTO.builder()
                .id(review.getId())
                .bookId(review.getBook().getId())
                .bookTitle(review.getBook().getTitle())
                .userName(review.getUser().getName())
                .rating(review.getRating())
                .comment(review.getComment())
                .createdAt(review.getCreatedAt())
                .build());
    }

    @Override
    public void deleteAnyReview(Long reviewId) {
        // Deliberately does NOT check ownership like ReviewServiceImpl does -
        // moderation means an admin can remove ANY review, including ones
        // they didn't write. That's the entire point of this endpoint existing
        // separately from the regular DELETE /api/reviews/{id}.
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found with id: " + reviewId));
        reviewRepository.delete(review);
    }
}
