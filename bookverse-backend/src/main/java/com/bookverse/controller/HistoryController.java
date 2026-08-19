package com.bookverse.controller;

import com.bookverse.dto.ReadingHistoryResponseDTO;
import com.bookverse.service.HistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/history")
@RequiredArgsConstructor
public class HistoryController {

    private final HistoryService historyService;

    @GetMapping
    public ResponseEntity<List<ReadingHistoryResponseDTO>> getHistory() {
        return ResponseEntity.ok(historyService.getHistory());
    }

    @PostMapping("/{bookId}")
    public ResponseEntity<Void> recordView(@PathVariable Long bookId) {
        historyService.recordView(bookId);
        return ResponseEntity.status(201).build();
    }
}
