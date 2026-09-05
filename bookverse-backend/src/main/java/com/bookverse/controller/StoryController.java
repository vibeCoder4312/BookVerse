package com.bookverse.controller;

import com.bookverse.dto.StoryCardResponseDTO;
import com.bookverse.dto.StoryDetailResponseDTO;
import com.bookverse.dto.StoryEpisodeResponseDTO;
import com.bookverse.entity.StoryLanguage;
import com.bookverse.service.StoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/stories")
@RequiredArgsConstructor
public class StoryController {

    private final StoryService storyService;

    @GetMapping
    public ResponseEntity<Page<StoryCardResponseDTO>> getStories(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(storyService.getPublishedStories(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<StoryDetailResponseDTO> getStoryDetail(@PathVariable Long id) {
        return ResponseEntity.ok(storyService.getStoryDetail(id));
    }

    // GET /api/stories/category/Horror?page=0&size=20
    @GetMapping("/category/{categoryName}")
    public ResponseEntity<Page<StoryCardResponseDTO>> getStoriesByCategory(
            @PathVariable String categoryName,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(storyService.getStoriesByCategory(categoryName, pageable));
    }

    // GET /api/stories/language/HINDI?page=0&size=20
    // Spring binds the path segment straight to the enum by matching its
    // name - "ENGLISH" and "HINDI" work directly, no custom converter needed.
    @GetMapping("/language/{language}")
    public ResponseEntity<Page<StoryCardResponseDTO>> getStoriesByLanguage(
            @PathVariable StoryLanguage language,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(storyService.getStoriesByLanguage(language, pageable));
    }

    // GET /api/stories/search?keyword=haunted&page=0&size=20
    @GetMapping("/search")
    public ResponseEntity<Page<StoryCardResponseDTO>> searchStories(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(storyService.searchStories(keyword, pageable));
    }

    // GET /api/stories/free?page=0&size=20
    @GetMapping("/free")
    public ResponseEntity<Page<StoryCardResponseDTO>> getFreeStories(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(storyService.getFreeStories(pageable));
    }

    // GET /api/stories/3/episodes
    @GetMapping("/{id}/episodes")
    public ResponseEntity<List<StoryEpisodeResponseDTO>> getStoryEpisodes(@PathVariable Long id) {
        return ResponseEntity.ok(storyService.getStoryEpisodes(id));
    }
}
