package com.bookverse.controller;

import com.bookverse.dto.StoryEpisodeResponseDTO;
import com.bookverse.service.StoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

// A separate, flat /api/episodes/{id} route (not nested under a story id)
// per the spec - useful for deep-linking directly to an episode, e.g. from
// a "Continue Listening" card that already knows the episode id but not
// necessarily which page of the story's episode list it's on.
@RestController
@RequiredArgsConstructor
public class EpisodeController {

    private final StoryService storyService;

    @GetMapping("/api/episodes/{id}")
    public ResponseEntity<StoryEpisodeResponseDTO> getEpisode(@PathVariable Long id) {
        return ResponseEntity.ok(storyService.getEpisodeById(id));
    }
}
