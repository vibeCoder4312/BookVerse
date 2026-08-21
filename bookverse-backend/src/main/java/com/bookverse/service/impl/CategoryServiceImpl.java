package com.bookverse.service.impl;

import com.bookverse.dto.CategoryRequestDTO;
import com.bookverse.dto.CategoryResponseDTO;
import com.bookverse.entity.Category;
import com.bookverse.exception.DuplicateResourceException;
import com.bookverse.exception.ResourceNotFoundException;
import com.bookverse.repository.CategoryRepository;
import com.bookverse.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    public List<CategoryResponseDTO> getAllCategories() {
        return categoryRepository.findAll().stream().map(this::toResponseDTO).toList();
    }

    @Override
    public CategoryResponseDTO createCategory(CategoryRequestDTO dto) {
        if (categoryRepository.findByName(dto.getName()).isPresent()) {
            throw new DuplicateResourceException("A category named '" + dto.getName() + "' already exists");
        }
        Category category = Category.builder().name(dto.getName()).build();
        return toResponseDTO(categoryRepository.save(category));
    }

    @Override
    public CategoryResponseDTO updateCategory(Long id, CategoryRequestDTO dto) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));
        category.setName(dto.getName());
        return toResponseDTO(categoryRepository.save(category));
    }

    @Override
    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));
        // Same foreign-key caveat as AuthorServiceImpl.deleteAuthor - deleting
        // a category still linked to books will fail at the database level.
        categoryRepository.delete(category);
    }

    private CategoryResponseDTO toResponseDTO(Category category) {
        return CategoryResponseDTO.builder()
                .id(category.getId())
                .name(category.getName())
                .bookCount(category.getBooks().size())
                .build();
    }
}
