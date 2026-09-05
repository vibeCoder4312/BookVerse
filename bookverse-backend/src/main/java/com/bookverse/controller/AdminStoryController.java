package com.bookverse.controller;

import com.bookverse.dto.*;
import com.bookverse.service.AdminStoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

// Every endpoint here is admin-only - one @PreAuthorize on the class
// applies to every method inside, same pattern as the existing AdminController.
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminStoryController {

    private final AdminStoryService adminStoryService;

    // --- Story CRUD ---

    @PostMapping("/stories")
    public ResponseEntity<StoryDetailResponseDTO> createStory(@Valid @RequestBody StoryRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(adminStoryService.createStory(dto));
    }

    @PutMapping("/stories/{id}")
    public ResponseEntity<StoryDetailResponseDTO> updateStory(@PathVariable Long id, @Valid @RequestBody StoryRequestDTO dto) {
        return ResponseEntity.ok(adminStoryService.updateStory(id, dto));
    }

    @DeleteMapping("/stories/{id}")
    public ResponseEntity<Void> deleteStory(@PathVariable Long id) {
        adminStoryService.deleteStory(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/stories/{id}/publish")
    public ResponseEntity<Void> publishStory(@PathVariable Long id) {
        adminStoryService.publishStory(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/stories/{id}/unpublish")
    public ResponseEntity<Void> unpublishStory(@PathVariable Long id) {
        adminStoryService.unpublishStory(id);
        return ResponseEntity.noContent().build();
    }

    // --- Episode CRUD ---

    @PostMapping("/stories/{storyId}/episodes")
    public ResponseEntity<StoryEpisodeResponseDTO> addEpisode(
            @PathVariable Long storyId, @Valid @RequestBody StoryEpisodeRequestDTO dto
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(adminStoryService.addEpisode(storyId, dto));
    }

    @PutMapping("/episodes/{id}")
    public ResponseEntity<StoryEpisodeResponseDTO> updateEpisode(
            @PathVariable Long id, @Valid @RequestBody StoryEpisodeRequestDTO dto
    ) {
        return ResponseEntity.ok(adminStoryService.updateEpisode(id, dto));
    }

    @DeleteMapping("/episodes/{id}")
    public ResponseEntity<Void> deleteEpisode(@PathVariable Long id) {
        adminStoryService.deleteEpisode(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/episodes/{id}/publish")
    public ResponseEntity<Void> publishEpisode(@PathVariable Long id) {
        adminStoryService.publishEpisode(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/episodes/{id}/unpublish")
    public ResponseEntity<Void> unpublishEpisode(@PathVariable Long id) {
        adminStoryService.unpublishEpisode(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/stories/{storyId}/episodes/reorder")
    public ResponseEntity<Void> reorderEpisodes(
            @PathVariable Long storyId, @Valid @RequestBody ReorderEpisodesRequestDTO dto
    ) {
        adminStoryService.reorderEpisodes(storyId, dto);
        return ResponseEntity.noContent().build();
    }

    // --- Audio upload ---
    // multipart/form-data, not JSON - the field name "file" must match
    // exactly what the frontend's FormData key is set to.
    @PostMapping(value = "/episodes/{id}/audio", consumes = "multipart/form-data")
    public ResponseEntity<StoryEpisodeResponseDTO> uploadAudio(
            @PathVariable Long id, @RequestParam("file") MultipartFile file
    ) {
        return ResponseEntity.ok(adminStoryService.uploadEpisodeAudio(id, file));
    }
}
