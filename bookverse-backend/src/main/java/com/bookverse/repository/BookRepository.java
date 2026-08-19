package com.bookverse.repository;

import com.bookverse.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

// Notice this interface has no implementation body for these methods,
// yet they work. Spring Data JPA either:
//  (a) reads the method NAME and builds the SQL automatically
//      (e.g. findByTitleContainingIgnoreCase), or
//  (b) runs the exact JPQL query we write ourselves with @Query.
// JPQL looks like SQL but operates on ENTITY names/fields (Book, categories)
// instead of raw table/column names - Hibernate translates it to real SQL.
public interface BookRepository extends JpaRepository<Book, Long> {

    // Page (not List!) carries pagination metadata - total pages, total
    // elements, current page number. This is what lets the frontend
    // paginate through books instead of loading all 500+ at once.
    @Query("SELECT b FROM Book b JOIN b.categories c " +
            "WHERE LOWER(c.name) = LOWER(:categoryName)")
    Page<Book> findByCategoryName(@Param("categoryName") String categoryName, Pageable pageable);

    @Query("SELECT b FROM Book b JOIN b.author a WHERE " +
            "LOWER(b.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(a.name) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Book> searchByTitleOrAuthor(@Param("keyword") String keyword, Pageable pageable);
}
