package com.bookverse.service;

import com.bookverse.dto.BookResponseDTO;
import java.util.List;

public interface FavoriteService {
    List<BookResponseDTO> getFavorites();
    void addFavorite(Long bookId);
    void removeFavorite(Long bookId);
}
