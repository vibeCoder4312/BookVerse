package com.bookverse.service;

import com.bookverse.dto.ReadingHistoryResponseDTO;
import java.util.List;

public interface HistoryService {
    List<ReadingHistoryResponseDTO> getHistory();
    void recordView(Long bookId);
}
