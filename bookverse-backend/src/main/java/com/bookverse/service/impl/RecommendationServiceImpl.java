package com.bookverse.service.impl;

import com.bookverse.dto.BookResponseDTO;
import com.bookverse.entity.Book;
import com.bookverse.entity.Category;
import com.bookverse.entity.User;
import com.bookverse.mapper.BookMapper;
import com.bookverse.repository.BookRepository;
import com.bookverse.repository.FavoriteRepository;
import com.bookverse.repository.LibraryRepository;
import com.bookverse.service.DiscoveryService;
import com.bookverse.service.RecommendationService;
import com.bookverse.util.CurrentUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecommendationServiceImpl implements RecommendationService {

    private final FavoriteRepository favoriteRepository;
    private final LibraryRepository libraryRepository;
    private final BookRepository bookRepository;
    private final BookMapper bookMapper;
    private final CurrentUserService currentUserService;
    private final DiscoveryService discoveryService; // used as a fallback

    @Override
    public List<BookResponseDTO> getRecommendations(int size) {
        User user = currentUserService.getCurrentUser();

        // Step 1: gather every book this user has already shown interest
        // in - both favorited AND library entries count as "signal".
        Set<Book> interactedBooks = new HashSet<>();
        favoriteRepository.findByUserOrderByCreatedAtDesc(user)
                .forEach(fav -> interactedBooks.add(fav.getBook()));
        libraryRepository.findByUserOrderByAddedAtDesc(user)
                .forEach(entry -> interactedBooks.add(entry.getBook()));

        // Step 2: extract every category from those books. If someone
        // favorited a Fantasy/Adventure book, both categories count.
        Set<String> categoryNames = interactedBooks.stream()
                .flatMap(book -> book.getCategories().stream())
                .map(Category::getName)
                .collect(Collectors.toSet());

        // FALLBACK: a brand new user with no favorites/library yet has
        // no categories to work from - showing them nothing would be a
        // worse experience than showing them what's currently trending.
        if (categoryNames.isEmpty()) {
            return discoveryService.getTrending(size);
        }

        Set<Long> excludeIds = interactedBooks.stream().map(Book::getId).collect(Collectors.toSet());
        // An empty "NOT IN ()" clause is invalid SQL - this can't actually
        // happen here since categoryNames non-empty implies interactedBooks
        // non-empty too, but guarding defensively costs nothing.
        if (excludeIds.isEmpty()) {
            excludeIds.add(-1L);
        }

        List<Book> recommended = bookRepository.findRecommendedByCategories(
                categoryNames, excludeIds, PageRequest.of(0, size)
        );

        // Second fallback: their categories matched, but there's simply
        // nothing NEW left to recommend in them (e.g. they've favorited
        // every Fantasy book that exists) - trending is still better than
        // an empty screen.
        if (recommended.isEmpty()) {
            return discoveryService.getTrending(size);
        }

        return recommended.stream().map(bookMapper::toResponseDTO).toList();
    }
}
