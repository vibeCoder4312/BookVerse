package com.bookverse.dto;

import com.bookverse.entity.LibraryStatus;
import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LibraryEntryResponseDTO {
    private BookResponseDTO book;
    private LibraryStatus status;
    private LocalDateTime addedAt;
}
