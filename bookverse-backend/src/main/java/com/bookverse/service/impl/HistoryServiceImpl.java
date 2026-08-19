package com.bookverse.service.impl;

import com.bookverse.dto.ReadingHistoryResponseDTO;
import com.bookverse.entity.Book;
import com.bookverse.entity.ReadingHistory;
import com.bookverse.entity.User;
import com.bookverse.exception.ResourceNotFoundException;
import com.bookverse.mapper.BookMapper;
import com.bookverse.repository.BookRepository;
import com.bookverse.repository.ReadingHistoryRepository;
import com.bookverse.service.HistoryService;
import com.bookverse.util.CurrentUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HistoryServiceImpl implements HistoryService {

    private final ReadingHistoryRepository historyRepository;
    private final BookRepository bookRepository;
    private final BookMapper bookMapper;
    private final CurrentUserService currentUserService;

    @Override
    public List<ReadingHistoryResponseDTO> getHistory() {
        User user = currentUserService.getCurrentUser();
        return historyRepository.findByUserOrderByOpenedAtDesc(user).stream()
                .map(h -> ReadingHistoryResponseDTO.builder()
                        .book(bookMapper.toResponseDTO(h.getBook()))
                        .openedAt(h.getOpenedAt())
                        .build())
                .toList();
    }

    @Override
    public void recordView(Long bookId) {
        User user = currentUserService.getCurrentUser();
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + bookId));

        // Same upsert idea as LibraryService: re-viewing a book updates
        // its timestamp (bumping it to the top of history) rather than
        // creating endless duplicate rows for the same book.
        ReadingHistory entry = historyRepository.findByUserAndBook(user, book)
                .orElseGet(() -> ReadingHistory.builder().user(user).book(book).build());

        historyRepository.save(entry); // @PreUpdate/@PrePersist stamps openedAt either way
    }
}
