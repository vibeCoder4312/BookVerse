package com.bookverse.controller;

import com.bookverse.dto.BookRequestDTO;
import com.bookverse.dto.BookResponseDTO;
import com.bookverse.service.BookService;
import com.bookverse.service.DiscoveryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;
    private final DiscoveryService discoveryService;

    // Example call: GET /api/books?page=0&size=20&sortBy=title&direction=asc
    // 'page' and 'size' control pagination. 'sortBy' picks which field to
    // order by, 'direction' picks asc/desc. All have sensible defaults so
    // the simplest possible call is just GET /api/books.
    @GetMapping
    public ResponseEntity<Page<BookResponseDTO>> getAllBooks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction
    ) {
        Sort sort = direction.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return ResponseEntity.ok(bookService.getAllBooks(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookResponseDTO> getBookById(@PathVariable Long id) {
        return ResponseEntity.ok(bookService.getBookById(id));
    }

    // @Valid triggers all the @NotBlank/@NotNull/etc checks we wrote on
    // BookRequestDTO. If any fail, Spring throws MethodArgumentNotValidException
    // BEFORE this method body even runs - our GlobalExceptionHandler catches it.
    // @PreAuthorize runs BEFORE the method body - if the currently logged-in
    // user doesn't have ROLE_ADMIN, Spring Security throws an
    // AccessDeniedException immediately (which our GlobalExceptionHandler
    // turns into a clean 403), and this method's code never even runs.
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<BookResponseDTO> createBook(@Valid @RequestBody BookRequestDTO dto) {
        BookResponseDTO created = bookService.createBook(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<BookResponseDTO> updateBook(
            @PathVariable Long id,
            @Valid @RequestBody BookRequestDTO dto
    ) {
        return ResponseEntity.ok(bookService.updateBook(id, dto));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);
        return ResponseEntity.noContent().build(); // 204 No Content
    }

    // GET /api/books/search?keyword=hobbit&page=0&size=20
    @GetMapping("/search")
    public ResponseEntity<Page<BookResponseDTO>> searchBooks(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(bookService.searchBooks(keyword, pageable));
    }

    // GET /api/books/category/fantasy?page=0&size=20
    @GetMapping("/category/{categoryName}")
    public ResponseEntity<Page<BookResponseDTO>> getBooksByCategory(
            @PathVariable String categoryName,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(bookService.getBooksByCategory(categoryName, pageable));
    }

    // GET /api/books/trending?size=10
    @GetMapping("/trending")
    public ResponseEntity<List<BookResponseDTO>> getTrending(@RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(discoveryService.getTrending(size));
    }

    // GET /api/books/popular?size=10
    @GetMapping("/popular")
    public ResponseEntity<List<BookResponseDTO>> getPopular(@RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(discoveryService.getPopular(size));
    }

    // GET /api/books/recent?size=10
    // Technically just GET /api/books?sortBy=createdAt&direction=desc
    // underneath - this dedicated endpoint exists purely so the frontend
    // (and anyone testing the API) doesn't need to remember those params.
    @GetMapping("/recent")
    public ResponseEntity<List<BookResponseDTO>> getRecentlyAdded(@RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(0, size, Sort.by("createdAt").descending());
        return ResponseEntity.ok(bookService.getAllBooks(pageable).getContent());
    }

    // GET /api/books/3/similar?size=6
    @GetMapping("/{id}/similar")
    public ResponseEntity<List<BookResponseDTO>> getSimilarBooks(
            @PathVariable Long id,
            @RequestParam(defaultValue = "6") int size
    ) {
        return ResponseEntity.ok(discoveryService.getSimilarBooks(id, size));
    }
}
