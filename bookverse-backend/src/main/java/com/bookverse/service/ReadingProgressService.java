package com.bookverse.service;

import com.bookverse.dto.ContinueReadingDTO;
import com.bookverse.dto.ReadingProgressResponseDTO;
import java.util.List;

public interface ReadingProgressService {
    ReadingProgressResponseDTO getProgress(Long bookId);
    ReadingProgressResponseDTO updateProgress(Long bookId, int progress);
    List<ContinueReadingDTO> getContinueReading();
}
