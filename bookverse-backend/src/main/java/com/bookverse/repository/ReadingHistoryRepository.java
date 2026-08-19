package com.bookverse.repository;

import com.bookverse.entity.Book;
import com.bookverse.entity.ReadingHistory;
import com.bookverse.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ReadingHistoryRepository extends JpaRepository<ReadingHistory, Long> {
    List<ReadingHistory> findByUserOrderByOpenedAtDesc(User user);
    Optional<ReadingHistory> findByUserAndBook(User user, Book book);
}
