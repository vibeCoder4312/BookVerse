package com.bookverse.controller;

import com.bookverse.dto.ContinueReadingDTO;
import com.bookverse.dto.ReadingProgressResponseDTO;
import com.bookverse.service.ReadingProgressService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/progress")
@RequiredArgsConstructor
public class ReadingProgressController {

    private final ReadingProgressService progressService;

    @GetMapping("/{bookId}")
    public ResponseEntity<ReadingProgressResponseDTO> getProgress(@PathVariable Long bookId) {
        return ResponseEntity.ok(progressService.getProgress(bookId));
    }

    // PUT /api/progress/5?progress=42
    @PutMapping("/{bookId}")
    public ResponseEntity<ReadingProgressResponseDTO> updateProgress(
            @PathVariable Long bookId,
            @RequestParam int progress
    ) {
        return ResponseEntity.ok(progressService.updateProgress(bookId, progress));
    }

    // "Continue Reading" - every book this user started but hasn't finished
    @GetMapping("/continue-reading")
    public ResponseEntity<List<ContinueReadingDTO>> getContinueReading() {
        return ResponseEntity.ok(progressService.getContinueReading());
    }
}
