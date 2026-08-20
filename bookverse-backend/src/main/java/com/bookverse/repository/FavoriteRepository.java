package com.bookverse.repository;

import com.bookverse.entity.Book;
import com.bookverse.entity.Favorite;
import com.bookverse.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    List<Favorite> findByUserOrderByCreatedAtDesc(User user);
    Optional<Favorite> findByUserAndBook(User user, Book book);
    boolean existsByUserAndBook(User user, Book book);
    void deleteByUserAndBook(User user, Book book);

    // Spring Data JPA walks the relationship (Favorite -> Book -> id)
    // from the method name alone, same trick we used for reviews in Phase 9.
    long countByBookId(Long bookId);
}
