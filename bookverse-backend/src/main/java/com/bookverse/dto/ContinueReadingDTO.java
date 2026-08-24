package com.bookverse.dto;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContinueReadingDTO {
    private BookResponseDTO book;
    private Integer progress;
    private LocalDateTime lastReadAt;
}
