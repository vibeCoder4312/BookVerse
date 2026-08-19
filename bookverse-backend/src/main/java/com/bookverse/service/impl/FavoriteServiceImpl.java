package com.bookverse.service.impl;

import com.bookverse.dto.BookResponseDTO;
import com.bookverse.entity.Book;
import com.bookverse.entity.Favorite;
import com.bookverse.entity.User;
import com.bookverse.exception.DuplicateResourceException;
import com.bookverse.exception.ResourceNotFoundException;
import com.bookverse.mapper.BookMapper;
import com.bookverse.repository.BookRepository;
import com.bookverse.repository.FavoriteRepository;
import com.bookverse.service.FavoriteService;
import com.bookverse.util.CurrentUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FavoriteServiceImpl implements FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final BookRepository bookRepository;
    private final BookMapper bookMapper;
    private final CurrentUserService currentUserService;

    @Override
    public List<BookResponseDTO> getFavorites() {
        User user = currentUserService.getCurrentUser();
        return favoriteRepository.findByUserOrderByCreatedAtDesc(user).stream()
                .map(fav -> bookMapper.toResponseDTO(fav.getBook()))
                .toList();
    }

    @Override
    public void addFavorite(Long bookId) {
        User user = currentUserService.getCurrentUser();
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + bookId));

        if (favoriteRepository.existsByUserAndBook(user, book)) {
            throw new DuplicateResourceException("This book is already in your favorites");
        }

        favoriteRepository.save(Favorite.builder().user(user).book(book).build());
    }

    @Override
    public void removeFavorite(Long bookId) {
        User user = currentUserService.getCurrentUser();
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + bookId));

        favoriteRepository.deleteByUserAndBook(user, book);
    }
}
