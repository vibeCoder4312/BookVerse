package com.bookverse.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StoryCategoryResponseDTO {
    private Long id;
    private String name;
}
