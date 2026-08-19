package com.bookverse.dto;

import lombok.*;

// We deliberately do NOT include the author's full book list here.
// If we did, and BookResponseDTO includes this AuthorSummaryDTO, and
// this included the author's books again... we'd get infinite JSON.
// This DTO only carries what a book card/detail page actually needs
// to show about the author.
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthorSummaryDTO {
    private Long id;
    private String name;
}
