package com.bookverse.service;

import com.bookverse.dto.BookRequestDTO;
import com.bookverse.dto.BookResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

// We define an interface first, then implement it in service/impl.
// Why bother with an interface for just one implementation? It's a
// beginner-friendly habit that pays off later: the Controller only
// depends on this interface, not the concrete class - so if we ever
// need a second implementation (e.g. a cached version), nothing in
// the Controller has to change.
public interface BookService {
    Page<BookResponseDTO> getAllBooks(Pageable pageable);
    BookResponseDTO getBookById(Long id);
    BookResponseDTO createBook(BookRequestDTO dto);
    BookResponseDTO updateBook(Long id, BookRequestDTO dto);
    void deleteBook(Long id);
    Page<BookResponseDTO> searchBooks(String keyword, Pageable pageable);
    Page<BookResponseDTO> getBooksByCategory(String categoryName, Pageable pageable);
}
