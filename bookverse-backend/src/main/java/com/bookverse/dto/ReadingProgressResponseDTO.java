package com.bookverse.dto;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReadingProgressResponseDTO {
    private Long bookId;
    private Integer progress; // 0 if the user has never opened this book
    private LocalDateTime lastReadAt; // null if never opened
}
