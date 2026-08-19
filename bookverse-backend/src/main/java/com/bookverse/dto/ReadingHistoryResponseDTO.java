package com.bookverse.dto;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReadingHistoryResponseDTO {
    private BookResponseDTO book;
    private LocalDateTime openedAt;
}
