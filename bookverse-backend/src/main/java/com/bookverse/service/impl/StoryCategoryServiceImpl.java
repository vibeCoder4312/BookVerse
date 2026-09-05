package com.bookverse.service.impl;

import com.bookverse.dto.StoryCategoryRequestDTO;
import com.bookverse.dto.StoryCategoryResponseDTO;
import com.bookverse.entity.StoryCategory;
import com.bookverse.exception.DuplicateResourceException;
import com.bookverse.exception.ResourceNotFoundException;
import com.bookverse.repository.StoryCategoryRepository;
import com.bookverse.service.StoryCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StoryCategoryServiceImpl implements StoryCategoryService {

    private final StoryCategoryRepository categoryRepository;

    @Override
    public List<StoryCategoryResponseDTO> getAllCategories() {
        return categoryRepository.findAll().stream().map(this::toDTO).toList();
    }

    @Override
    public StoryCategoryResponseDTO createCategory(StoryCategoryRequestDTO dto) {
        if (categoryRepository.findByName(dto.getName()).isPresent()) {
            throw new DuplicateResourceException("A story category named '" + dto.getName() + "' already exists");
        }
        StoryCategory category = StoryCategory.builder().name(dto.getName()).build();
        return toDTO(categoryRepository.save(category));
    }

    @Override
    public StoryCategoryResponseDTO updateCategory(Long id, StoryCategoryRequestDTO dto) {
        StoryCategory category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Story category not found with id: " + id));
        category.setName(dto.getName());
        return toDTO(categoryRepository.save(category));
    }

    @Override
    public void deleteCategory(Long id) {
        StoryCategory category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Story category not found with id: " + id));
        // Same foreign-key caveat as the book Category/Author deletes:
        // this will fail if any Story still references this category,
        // since Story.category is non-nullable.
        categoryRepository.delete(category);
    }

    private StoryCategoryResponseDTO toDTO(StoryCategory category) {
        return StoryCategoryResponseDTO.builder()
                .id(category.getId())
                .name(category.getName())
                .build();
    }
}
