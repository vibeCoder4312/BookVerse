package com.bookverse.service.impl;

import com.bookverse.dto.ContinueReadingDTO;
import com.bookverse.dto.ReadingProgressResponseDTO;
import com.bookverse.entity.Book;
import com.bookverse.entity.ReadingProgress;
import com.bookverse.entity.User;
import com.bookverse.exception.BadRequestException;
import com.bookverse.exception.ResourceNotFoundException;
import com.bookverse.mapper.BookMapper;
import com.bookverse.repository.BookRepository;
import com.bookverse.repository.ReadingProgressRepository;
import com.bookverse.service.ReadingProgressService;
import com.bookverse.util.CurrentUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReadingProgressServiceImpl implements ReadingProgressService {

    private final ReadingProgressRepository progressRepository;
    private final BookRepository bookRepository;
    private final BookMapper bookMapper;
    private final CurrentUserService currentUserService;

    @Override
    public ReadingProgressResponseDTO getProgress(Long bookId) {
        User user = currentUserService.getCurrentUser();
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + bookId));

        return progressRepository.findByUserAndBook(user, book)
                .map(p -> ReadingProgressResponseDTO.builder()
                        .bookId(bookId)
                        .progress(p.getProgress())
                        .lastReadAt(p.getLastReadAt())
                        .build())
                // No progress saved yet is a perfectly normal state (a book
                // the user has never opened) - we return 0, not a 404.
                .orElse(ReadingProgressResponseDTO.builder().bookId(bookId).progress(0).lastReadAt(null).build());
    }

    @Override
    public ReadingProgressResponseDTO updateProgress(Long bookId, int progress) {
        if (progress < 0 || progress > 100) {
            throw new BadRequestException("Progress must be between 0 and 100");
        }

        User user = currentUserService.getCurrentUser();
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + bookId));

        ReadingProgress entry = progressRepository.findByUserAndBook(user, book)
                .orElseGet(() -> ReadingProgress.builder().user(user).book(book).build());

        entry.setProgress(progress);
        ReadingProgress saved = progressRepository.save(entry);

        return ReadingProgressResponseDTO.builder()
                .bookId(bookId)
                .progress(saved.getProgress())
                .lastReadAt(saved.getLastReadAt())
                .build();
    }

    @Override
    public List<ContinueReadingDTO> getContinueReading() {
        User user = currentUserService.getCurrentUser();
        // Started (>0) but not finished (<100) - a book at exactly 0 was
        // never really begun, and one at 100 belongs in "Completed", not here.
        return progressRepository.findByUserAndProgressBetweenOrderByLastReadAtDesc(user, 1, 99).stream()
                .map(p -> ContinueReadingDTO.builder()
                        .book(bookMapper.toResponseDTO(p.getBook()))
                        .progress(p.getProgress())
                        .lastReadAt(p.getLastReadAt())
                        .build())
                .toList();
    }
}
