package com.bookverse.repository;

import com.bookverse.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Set;

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

    // "Similar books" for a details page: anything sharing at least one
    // category with this book, excluding the book itself. DISTINCT matters
    // here - a book sharing TWO categories with the original would
    // otherwise show up twice in the join results.
    @Query("SELECT DISTINCT b FROM Book b JOIN b.categories c " +
            "WHERE c.name IN :categoryNames AND b.id <> :excludeBookId")
    List<Book> findSimilarByCategories(
            @Param("categoryNames") Set<String> categoryNames,
            @Param("excludeBookId") Long excludeBookId,
            Pageable pageable
    );

    // Recommendations: same idea, but excludes a whole SET of book ids
    // (everything the user already favorited/added to their library),
    // not just one book.
    @Query("SELECT DISTINCT b FROM Book b JOIN b.categories c " +
            "WHERE c.name IN :categoryNames AND b.id NOT IN :excludeIds")
    List<Book> findRecommendedByCategories(
            @Param("categoryNames") Set<String> categoryNames,
            @Param("excludeIds") Set<Long> excludeIds,
            Pageable pageable
    );
}
