package com.bookverse.service;

import com.bookverse.dto.StoryCategoryRequestDTO;
import com.bookverse.dto.StoryCategoryResponseDTO;
import java.util.List;

public interface StoryCategoryService {
    List<StoryCategoryResponseDTO> getAllCategories();
    StoryCategoryResponseDTO createCategory(StoryCategoryRequestDTO dto);
    StoryCategoryResponseDTO updateCategory(Long id, StoryCategoryRequestDTO dto);
    void deleteCategory(Long id);
}
