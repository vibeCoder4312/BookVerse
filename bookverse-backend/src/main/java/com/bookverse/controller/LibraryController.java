package com.bookverse.controller;

import com.bookverse.dto.LibraryEntryResponseDTO;
import com.bookverse.entity.LibraryStatus;
import com.bookverse.service.LibraryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/library")
@RequiredArgsConstructor
public class LibraryController {

    private final LibraryService libraryService;

    @GetMapping
    public ResponseEntity<List<LibraryEntryResponseDTO>> getLibrary() {
        return ResponseEntity.ok(libraryService.getLibrary());
    }

    // Example: POST /api/library/5?status=CURRENTLY_READING
    // If status is omitted, it defaults to SAVED - matching a simple
    // "Add to Library" button that doesn't ask the user to pick a status upfront.
    @PostMapping("/{bookId}")
    public ResponseEntity<Void> addOrUpdateEntry(
            @PathVariable Long bookId,
            @RequestParam(defaultValue = "SAVED") LibraryStatus status
    ) {
        libraryService.addOrUpdateEntry(bookId, status);
        return ResponseEntity.status(201).build();
    }

    @DeleteMapping("/{bookId}")
    public ResponseEntity<Void> removeEntry(@PathVariable Long bookId) {
        libraryService.removeEntry(bookId);
        return ResponseEntity.noContent().build();
    }
}
