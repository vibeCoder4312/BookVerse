package com.bookverse.dto;

import com.bookverse.entity.ContentType;
import jakarta.validation.constraints.*;
import lombok.*;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookRequestDTO {

    @NotBlank(message = "Title is required")
    // @NotBlank checks the string is not null AND not just whitespace.
    // If this fails, Spring Boot automatically returns a 400 Bad Request
    // with a message - we don't have to write that check ourselves.
    private String title;

    private String description;

    @NotNull(message = "Author ID is required")
    private Long authorId;

    @NotNull(message = "Content type is required")
    private ContentType contentType;

    private String coverUrl;

    @Min(value = 1000, message = "Publication year seems invalid")
    private Integer publicationYear;

    @NotEmpty(message = "At least one category is required")
    private Set<Long> categoryIds;
}
