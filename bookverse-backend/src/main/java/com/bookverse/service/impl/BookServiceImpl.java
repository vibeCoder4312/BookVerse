package com.bookverse.service.impl;

import com.bookverse.dto.BookRequestDTO;
import com.bookverse.dto.BookResponseDTO;
import com.bookverse.entity.Author;
import com.bookverse.entity.Book;
import com.bookverse.entity.Category;
import com.bookverse.exception.ResourceNotFoundException;
import com.bookverse.mapper.BookMapper;
import com.bookverse.repository.AuthorRepository;
import com.bookverse.repository.BookRepository;
import com.bookverse.repository.CategoryRepository;
import com.bookverse.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

// @Service marks this as a Spring-managed bean holding business logic -
// the same Dependency Injection idea we saw with DataSeeder in Phase 3.
@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final CategoryRepository categoryRepository;
    private final BookMapper bookMapper;

    @Override
    public Page<BookResponseDTO> getAllBooks(Pageable pageable) {
        // .map() here converts a Page<Book> into a Page<BookResponseDTO>
        // by running our mapper on every element, while keeping all the
        // pagination metadata (total pages etc.) intact.
        return bookRepository.findAll(pageable).map(bookMapper::toResponseDTO);
    }

    @Override
    public BookResponseDTO getBookById(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + id));
        return bookMapper.toResponseDTO(book);
    }

    @Override
    public BookResponseDTO createBook(BookRequestDTO dto) {
        Author author = authorRepository.findById(dto.getAuthorId())
                .orElseThrow(() -> new ResourceNotFoundException("Author not found with id: " + dto.getAuthorId()));

        Set<Category> categories = resolveCategories(dto.getCategoryIds());

        Book book = Book.builder()
                .title(dto.getTitle())
                .description(dto.getDescription())
                .author(author)
                .contentType(dto.getContentType())
                .coverUrl(dto.getCoverUrl())
                .publicationYear(dto.getPublicationYear())
                .categories(categories)
                .build();

        Book saved = bookRepository.save(book);
        return bookMapper.toResponseDTO(saved);
    }

    @Override
    public BookResponseDTO updateBook(Long id, BookRequestDTO dto) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + id));

        Author author = authorRepository.findById(dto.getAuthorId())
                .orElseThrow(() -> new ResourceNotFoundException("Author not found with id: " + dto.getAuthorId()));

        book.setTitle(dto.getTitle());
        book.setDescription(dto.getDescription());
        book.setAuthor(author);
        book.setContentType(dto.getContentType());
        book.setCoverUrl(dto.getCoverUrl());
        book.setPublicationYear(dto.getPublicationYear());
        book.setCategories(resolveCategories(dto.getCategoryIds()));

        Book updated = bookRepository.save(book);
        return bookMapper.toResponseDTO(updated);
    }

    @Override
    public void deleteBook(Long id) {
        if (!bookRepository.existsById(id)) {
            throw new ResourceNotFoundException("Book not found with id: " + id);
        }
        bookRepository.deleteById(id);
    }

    @Override
    public Page<BookResponseDTO> searchBooks(String keyword, Pageable pageable) {
        return bookRepository.searchByTitleOrAuthor(keyword, pageable).map(bookMapper::toResponseDTO);
    }

    @Override
    public Page<BookResponseDTO> getBooksByCategory(String categoryName, Pageable pageable) {
        return bookRepository.findByCategoryName(categoryName, pageable).map(bookMapper::toResponseDTO);
    }

    // Small private helper - not part of the interface, just used
    // internally to avoid repeating this logic in both create and update.
    private Set<Category> resolveCategories(Set<Long> categoryIds) {
        return categoryIds.stream()
                .map(catId -> categoryRepository.findById(catId)
                        .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + catId)))
                .collect(Collectors.toSet());
    }
}
