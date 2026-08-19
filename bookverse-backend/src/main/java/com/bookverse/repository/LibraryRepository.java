package com.bookverse.repository;

import com.bookverse.entity.Book;
import com.bookverse.entity.LibraryEntry;
import com.bookverse.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface LibraryRepository extends JpaRepository<LibraryEntry, Long> {
    List<LibraryEntry> findByUserOrderByAddedAtDesc(User user);
    Optional<LibraryEntry> findByUserAndBook(User user, Book book);
    void deleteByUserAndBook(User user, Book book);
}
