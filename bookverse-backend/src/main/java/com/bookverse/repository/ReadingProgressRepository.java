package com.bookverse.repository;

import com.bookverse.entity.Book;
import com.bookverse.entity.ReadingProgress;
import com.bookverse.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ReadingProgressRepository extends JpaRepository<ReadingProgress, Long> {
    Optional<ReadingProgress> findByUserAndBook(User user, Book book);

    // Powers a future "Continue Reading" section - anything started
    // (progress > 0) but not finished (progress < 100).
    List<ReadingProgress> findByUserAndProgressBetweenOrderByLastReadAtDesc(User user, int min, int max);
}
