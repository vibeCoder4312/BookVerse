package com.bookverse.service.impl;

import com.bookverse.dto.LibraryEntryResponseDTO;
import com.bookverse.entity.Book;
import com.bookverse.entity.LibraryEntry;
import com.bookverse.entity.LibraryStatus;
import com.bookverse.entity.User;
import com.bookverse.exception.ResourceNotFoundException;
import com.bookverse.mapper.BookMapper;
import com.bookverse.repository.BookRepository;
import com.bookverse.repository.LibraryRepository;
import com.bookverse.service.LibraryService;
import com.bookverse.util.CurrentUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LibraryServiceImpl implements LibraryService {

    private final LibraryRepository libraryRepository;
    private final BookRepository bookRepository;
    private final BookMapper bookMapper;
    private final CurrentUserService currentUserService;

    @Override
    public List<LibraryEntryResponseDTO> getLibrary() {
        User user = currentUserService.getCurrentUser();
        return libraryRepository.findByUserOrderByAddedAtDesc(user).stream()
                .map(entry -> LibraryEntryResponseDTO.builder()
                        .book(bookMapper.toResponseDTO(entry.getBook()))
                        .status(entry.getStatus())
                        .addedAt(entry.getAddedAt())
                        .build())
                .toList();
    }

    @Override
    public void addOrUpdateEntry(Long bookId, LibraryStatus status) {
        User user = currentUserService.getCurrentUser();
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + bookId));

        // UPSERT pattern: if this user already has this book in their
        // library, just update its status (e.g. Saved -> Currently Reading)
        // instead of creating a second row for the same book.
        LibraryEntry entry = libraryRepository.findByUserAndBook(user, book)
                .orElseGet(() -> LibraryEntry.builder().user(user).book(book).build());

        entry.setStatus(status);
        libraryRepository.save(entry);
    }

    @Override
    public void removeEntry(Long bookId) {
        User user = currentUserService.getCurrentUser();
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + bookId));

        libraryRepository.deleteByUserAndBook(user, book);
    }
}
