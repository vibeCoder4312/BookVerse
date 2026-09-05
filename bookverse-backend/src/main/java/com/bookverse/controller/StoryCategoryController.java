package com.bookverse.controller;

import com.bookverse.dto.StoryCategoryRequestDTO;
import com.bookverse.dto.StoryCategoryResponseDTO;
import com.bookverse.service.StoryCategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/story-categories")
@RequiredArgsConstructor
public class StoryCategoryController {

    private final StoryCategoryService categoryService;

    @GetMapping
    public ResponseEntity<List<StoryCategoryResponseDTO>> getAllCategories() {
        return ResponseEntity.ok(categoryService.getAllCategories());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<StoryCategoryResponseDTO> createCategory(@Valid @RequestBody StoryCategoryRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(categoryService.createCategory(dto));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<StoryCategoryResponseDTO> updateCategory(@PathVariable Long id, @Valid @RequestBody StoryCategoryRequestDTO dto) {
        return ResponseEntity.ok(categoryService.updateCategory(id, dto));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }
}
