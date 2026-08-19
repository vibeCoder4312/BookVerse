package com.bookverse.service;

import com.bookverse.dto.LibraryEntryResponseDTO;
import com.bookverse.entity.LibraryStatus;
import java.util.List;

public interface LibraryService {
    List<LibraryEntryResponseDTO> getLibrary();
    void addOrUpdateEntry(Long bookId, LibraryStatus status);
    void removeEntry(Long bookId);
}
