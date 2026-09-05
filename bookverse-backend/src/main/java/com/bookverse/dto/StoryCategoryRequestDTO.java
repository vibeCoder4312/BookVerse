package com.bookverse.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StoryCategoryRequestDTO {
    @NotBlank(message = "Category name is required")
    private String name;
}
