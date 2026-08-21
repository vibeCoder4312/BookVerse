package com.bookverse.service.impl;

import com.bookverse.dto.AuthorRequestDTO;
import com.bookverse.dto.AuthorResponseDTO;
import com.bookverse.entity.Author;
import com.bookverse.exception.ResourceNotFoundException;
import com.bookverse.repository.AuthorRepository;
import com.bookverse.service.AuthorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthorServiceImpl implements AuthorService {

    private final AuthorRepository authorRepository;

    @Override
    public List<AuthorResponseDTO> getAllAuthors() {
        return authorRepository.findAll().stream().map(this::toResponseDTO).toList();
    }

    @Override
    public AuthorResponseDTO getAuthorById(Long id) {
        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Author not found with id: " + id));
        return toResponseDTO(author);
    }

    @Override
    public AuthorResponseDTO createAuthor(AuthorRequestDTO dto) {
        Author author = Author.builder()
                .name(dto.getName())
                .bio(dto.getBio())
                .photoUrl(dto.getPhotoUrl())
                .build();
        return toResponseDTO(authorRepository.save(author));
    }

    @Override
    public AuthorResponseDTO updateAuthor(Long id, AuthorRequestDTO dto) {
        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Author not found with id: " + id));
        author.setName(dto.getName());
        author.setBio(dto.getBio());
        author.setPhotoUrl(dto.getPhotoUrl());
        return toResponseDTO(authorRepository.save(author));
    }

    @Override
    public void deleteAuthor(Long id) {
        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Author not found with id: " + id));
        // NOTE: if this author still has books pointing at them, the
        // database's foreign key constraint will reject this delete -
        // Hibernate will throw, and our generic 500 handler will catch it.
        // A more polished version would check author.getBooks().isEmpty()
        // first and return a clean 400 - left as a improvement for Phase 15 (testing/polish).
        authorRepository.delete(author);
    }

    private AuthorResponseDTO toResponseDTO(Author author) {
        return AuthorResponseDTO.builder()
                .id(author.getId())
                .name(author.getName())
                .bio(author.getBio())
                .photoUrl(author.getPhotoUrl())
                .bookCount(author.getBooks().size())
                .build();
    }
}
