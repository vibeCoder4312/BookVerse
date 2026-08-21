package com.bookverse.service;

import com.bookverse.dto.AdminReviewResponseDTO;
import com.bookverse.dto.AdminStatsDTO;
import com.bookverse.dto.AdminUserResponseDTO;
import com.bookverse.entity.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AdminService {
    AdminStatsDTO getStats();
    Page<AdminUserResponseDTO> getUsers(String search, Pageable pageable);
    void setUserRole(Long userId, Role role);
    void setUserEnabled(Long userId, boolean enabled);
    Page<AdminReviewResponseDTO> getAllReviews(Pageable pageable);
    void deleteAnyReview(Long reviewId);
}
