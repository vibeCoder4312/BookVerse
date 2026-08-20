package com.bookverse.service;

import com.bookverse.dto.BookResponseDTO;
import java.util.List;

public interface DiscoveryService {
    List<BookResponseDTO> getTrending(int size);
    List<BookResponseDTO> getPopular(int size);
    List<BookResponseDTO> getSimilarBooks(Long bookId, int size);
}
