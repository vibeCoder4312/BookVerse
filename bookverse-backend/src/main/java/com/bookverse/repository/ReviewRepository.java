package com.bookverse.repository;

import com.bookverse.entity.Book;
import com.bookverse.entity.Review;
import com.bookverse.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findByBookOrderByCreatedAtDesc(Book book);

    Optional<Review> findByUserAndBook(User user, Book book);

    // Spring Data JPA understands "BookId" here as "book.id" automatically -
    // it walks the relationship (Review -> Book -> id) without us writing
    // any SQL/JPQL, because we followed the naming convention.
    long countByBookId(Long bookId);

    // Aggregate queries like AVG() aren't derivable from a method name
    // alone, so we write the JPQL explicitly here.
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.book.id = :bookId")
    Double findAverageRatingByBookId(@Param("bookId") Long bookId);
}
