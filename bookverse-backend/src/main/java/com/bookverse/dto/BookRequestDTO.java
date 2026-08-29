package com.bookverse.dto;

import com.bookverse.entity.ContentType;
import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookRequestDTO {

    @NotBlank(message = "Title is required")
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

    // NEW: required so every book created/edited through the admin API
    // has a real price - @DecimalMin prevents accidentally saving a
    // negative or zero price through a typo.
    @NotNull(message = "Price is required")
    @DecimalMin(value = "1.00", message = "Price must be at least ₹1.00")
    private BigDecimal price;
}
