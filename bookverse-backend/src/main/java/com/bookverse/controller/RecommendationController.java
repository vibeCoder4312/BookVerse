package com.bookverse.controller;

import com.bookverse.dto.BookResponseDTO;
import com.bookverse.service.RecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class RecommendationController {

    private final RecommendationService recommendationService;

    // This endpoint requires a valid JWT (it's not in SecurityConfig's
    // permitAll list) since recommendations only make sense FOR a specific
    // logged-in user - there's no "generic" recommendation to give an
    // anonymous visitor.
    @GetMapping("/api/recommendations")
    public ResponseEntity<List<BookResponseDTO>> getRecommendations(
            @RequestParam(defaultValue = "12") int size
    ) {
        return ResponseEntity.ok(recommendationService.getRecommendations(size));
    }
}
