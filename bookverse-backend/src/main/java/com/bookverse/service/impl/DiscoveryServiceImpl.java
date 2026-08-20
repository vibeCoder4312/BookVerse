package com.bookverse.service.impl;

import com.bookverse.dto.BookResponseDTO;
import com.bookverse.entity.Book;
import com.bookverse.entity.Category;
import com.bookverse.exception.ResourceNotFoundException;
import com.bookverse.mapper.BookMapper;
import com.bookverse.repository.BookRepository;
import com.bookverse.repository.FavoriteRepository;
import com.bookverse.repository.ReviewRepository;
import com.bookverse.service.DiscoveryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DiscoveryServiceImpl implements DiscoveryService {

    private final BookRepository bookRepository;
    private final ReviewRepository reviewRepository;
    private final FavoriteRepository favoriteRepository;
    private final BookMapper bookMapper;

    @Override
    public List<BookResponseDTO> getTrending(int size) {
        // NOTE: findAll() loads every book into memory to score it. Fine
        // for a college project's ~500 books - at real-world scale this
        // score would instead be pre-computed and stored on a schedule
        // (e.g. a nightly job), not calculated live on every request.
        List<Book> allBooks = bookRepository.findAll();

        return allBooks.stream()
                .sorted(Comparator.comparingDouble(this::trendingScore).reversed())
                .limit(size)
                .map(bookMapper::toResponseDTO)
                .toList();
    }

    @Override
    public List<BookResponseDTO> getPopular(int size) {
        // Simpler than Trending on purpose: pure all-time view count,
        // no weighting - "what's been looked at the most, ever."
        return bookRepository
                .findAll(PageRequest.of(0, size, Sort.by("views").descending()))
                .map(bookMapper::toResponseDTO)
                .getContent();
    }

    @Override
    public List<BookResponseDTO> getSimilarBooks(Long bookId, int size) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + bookId));

        Set<String> categoryNames = book.getCategories().stream()
                .map(Category::getName)
                .collect(Collectors.toSet());

        if (categoryNames.isEmpty()) {
            return List.of(); // a book with no categories has nothing to be "similar" via
        }

        return bookRepository
                .findSimilarByCategories(categoryNames, bookId, PageRequest.of(0, size))
                .stream()
                .map(bookMapper::toResponseDTO)
                .toList();
    }

    // THE TRENDING FORMULA - a simple weighted sum, entirely rule-based
    // (no machine learning, per the project spec). Each signal is weighted
    // by how strong an indicator of genuine interest it is: a favorite
    // (deliberate action) counts for more than a passive view.
    private double trendingScore(Book book) {
        Double avgRating = reviewRepository.findAverageRatingByBookId(book.getId());
        long reviewCount = reviewRepository.countByBookId(book.getId());
        long favoriteCount = favoriteRepository.countByBookId(book.getId());

        double viewsComponent = book.getViews();
        double ratingComponent = (avgRating != null ? avgRating : 0) * 10;
        double reviewComponent = reviewCount * 5;
        double favoriteComponent = favoriteCount * 8;

        // Recency bonus: books added in the last 30 days get an extra
        // boost that fades linearly to 0 - without this, a great NEW
        // book with few views yet would never surface above old
        // high-view books, and "trending" would never show anything new.
        long daysSinceAdded = ChronoUnit.DAYS.between(book.getCreatedAt(), LocalDateTime.now());
        double recencyBonus = daysSinceAdded < 30 ? (30 - daysSinceAdded) * 0.5 : 0;

        return viewsComponent + ratingComponent + reviewComponent + favoriteComponent + recencyBonus;
    }
}
