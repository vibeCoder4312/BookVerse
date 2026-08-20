package com.bookverse.service;

import com.bookverse.dto.BookResponseDTO;
import java.util.List;

public interface RecommendationService {
    List<BookResponseDTO> getRecommendations(int size);
}
