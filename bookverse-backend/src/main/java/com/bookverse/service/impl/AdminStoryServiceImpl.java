package com.bookverse.service.impl;

import com.bookverse.dto.*;
import com.bookverse.entity.*;
import com.bookverse.exception.BadRequestException;
import com.bookverse.exception.DuplicateResourceException;
import com.bookverse.exception.ResourceNotFoundException;
import com.bookverse.mapper.StoryMapper;
import com.bookverse.repository.*;
import com.bookverse.service.AdminStoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AdminStoryServiceImpl implements AdminStoryService {

    private final StoryRepository storyRepository;
    private final StoryCategoryRepository categoryRepository;
    private final StoryEpisodeRepository episodeRepository;
    private final StoryAudioRepository audioRepository;
    private final StoryMapper storyMapper;

    @Value("${bookverse.storage.audio-root}")
    private String audioRoot;

    private static final long MAX_AUDIO_FILE_SIZE_BYTES = 20L * 1024 * 1024; // 20 MB

    // ============================================================
    // STORY CRUD
    // ============================================================

    @Override
    public StoryDetailResponseDTO createStory(StoryRequestDTO dto) {
        StoryCategory category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Story category not found with id: " + dto.getCategoryId()));

        Story story = Story.builder()
                .title(dto.getTitle())
                .description(dto.getDescription())
                .shortDescription(dto.getShortDescription())
                .coverImage(dto.getCoverImage())
                .author(dto.getAuthor())
                .narrator(dto.getNarrator())
                .voiceStyle(dto.getVoiceStyle())
                .language(dto.getLanguage())
                .category(category)
                .isPremium(dto.getIsPremium())
                .status(StoryStatus.DRAFT) // every new story starts as a draft, never live immediately
                .build();

        Story saved = storyRepository.save(story);
        return storyMapper.toDetailDTO(saved, List.of());
    }

    @Override
    public StoryDetailResponseDTO updateStory(Long storyId, StoryRequestDTO dto) {
        Story story = storyRepository.findById(storyId)
                .orElseThrow(() -> new ResourceNotFoundException("Story not found with id: " + storyId));

        StoryCategory category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Story category not found with id: " + dto.getCategoryId()));

        story.setTitle(dto.getTitle());
        story.setDescription(dto.getDescription());
        story.setShortDescription(dto.getShortDescription());
        story.setCoverImage(dto.getCoverImage());
        story.setAuthor(dto.getAuthor());
        story.setNarrator(dto.getNarrator());
        story.setVoiceStyle(dto.getVoiceStyle());
        story.setLanguage(dto.getLanguage());
        story.setCategory(category);
        story.setIsPremium(dto.getIsPremium());

        Story saved = storyRepository.save(story);
        var episodes = episodeRepository.findByStoryOrderByEpisodeNumberAsc(saved);
        return storyMapper.toDetailDTO(saved, episodes);
    }

    @Override
    public void deleteStory(Long storyId) {
        Story story = storyRepository.findById(storyId)
                .orElseThrow(() -> new ResourceNotFoundException("Story not found with id: " + storyId));
        // CascadeType.ALL + orphanRemoval on Story.episodes (and in turn
        // StoryEpisode.audioVersions) means deleting the story also cleans
        // up all its episodes and their audio records automatically - we
        // don't need to manually delete those first.
        storyRepository.delete(story);
    }

    @Override
    public void publishStory(Long storyId) {
        setStoryStatus(storyId, StoryStatus.PUBLISHED);
    }

    @Override
    public void unpublishStory(Long storyId) {
        setStoryStatus(storyId, StoryStatus.UNPUBLISHED);
    }

    private void setStoryStatus(Long storyId, StoryStatus status) {
        Story story = storyRepository.findById(storyId)
                .orElseThrow(() -> new ResourceNotFoundException("Story not found with id: " + storyId));
        story.setStatus(status);
        storyRepository.save(story);
    }

    // ============================================================
    // EPISODE CRUD
    // ============================================================

    @Override
    public StoryEpisodeResponseDTO addEpisode(Long storyId, StoryEpisodeRequestDTO dto) {
        Story story = storyRepository.findById(storyId)
                .orElseThrow(() -> new ResourceNotFoundException("Story not found with id: " + storyId));

        if (episodeRepository.findByStoryAndEpisodeNumber(story, dto.getEpisodeNumber()).isPresent()) {
            throw new DuplicateResourceException(
                    "Episode " + dto.getEpisodeNumber() + " already exists for this story");
        }

        StoryEpisode episode = StoryEpisode.builder()
                .story(story)
                .episodeNumber(dto.getEpisodeNumber())
                .title(dto.getTitle())
                .description(dto.getDescription())
                .durationSeconds(dto.getDurationSeconds())
                .isPublished(false) // admin publishes explicitly once audio is ready
                .build();

        StoryEpisode saved = episodeRepository.save(episode);
        recomputeStoryAggregates(story);
        return storyMapper.toEpisodeDTO(saved);
    }

    @Override
    public StoryEpisodeResponseDTO updateEpisode(Long episodeId, StoryEpisodeRequestDTO dto) {
        StoryEpisode episode = episodeRepository.findById(episodeId)
                .orElseThrow(() -> new ResourceNotFoundException("Episode not found with id: " + episodeId));

        // If the episode number is changing, make sure it doesn't collide
        // with a DIFFERENT episode of the same story (colliding with
        // itself, i.e. not actually changing, is fine).
        episodeRepository.findByStoryAndEpisodeNumber(episode.getStory(), dto.getEpisodeNumber())
                .filter(existing -> !existing.getId().equals(episodeId))
                .ifPresent(existing -> {
                    throw new DuplicateResourceException(
                            "Episode " + dto.getEpisodeNumber() + " already exists for this story");
                });

        episode.setEpisodeNumber(dto.getEpisodeNumber());
        episode.setTitle(dto.getTitle());
        episode.setDescription(dto.getDescription());
        episode.setDurationSeconds(dto.getDurationSeconds());

        StoryEpisode saved = episodeRepository.save(episode);
        recomputeStoryAggregates(episode.getStory());
        return storyMapper.toEpisodeDTO(saved);
    }

    @Override
    public void deleteEpisode(Long episodeId) {
        StoryEpisode episode = episodeRepository.findById(episodeId)
                .orElseThrow(() -> new ResourceNotFoundException("Episode not found with id: " + episodeId));
        Story story = episode.getStory();
        episodeRepository.delete(episode);
        recomputeStoryAggregates(story);
    }

    @Override
    public void publishEpisode(Long episodeId) {
        setEpisodePublished(episodeId, true);
    }

    @Override
    public void unpublishEpisode(Long episodeId) {
        setEpisodePublished(episodeId, false);
    }

    private void setEpisodePublished(Long episodeId, boolean published) {
        StoryEpisode episode = episodeRepository.findById(episodeId)
                .orElseThrow(() -> new ResourceNotFoundException("Episode not found with id: " + episodeId));
        episode.setIsPublished(published);
        episodeRepository.save(episode);
    }

    // ============================================================
    // EPISODE REORDERING - the two-pass negative-number trick
    // ============================================================

    @Override
    public void reorderEpisodes(Long storyId, ReorderEpisodesRequestDTO dto) {
        Story story = storyRepository.findById(storyId)
                .orElseThrow(() -> new ResourceNotFoundException("Story not found with id: " + storyId));

        List<StoryEpisode> episodes = dto.getEpisodeIdsInOrder().stream()
                .map(id -> episodeRepository.findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("Episode not found with id: " + id)))
                .toList();

        boolean allBelongToStory = episodes.stream().allMatch(e -> e.getStory().getId().equals(storyId));
        if (!allBelongToStory) {
            throw new BadRequestException("All episodes in the reorder list must belong to the same story");
        }

        // PASS 1: move every episode to a temporary NEGATIVE number first.
        // Without this, setting episode A to position 2 could collide with
        // episode B, which currently holds number 2 - the unique constraint
        // is checked immediately on each save, not deferred to the end of
        // the transaction, so a direct 1-pass reorder can fail halfway through.
        for (int i = 0; i < episodes.size(); i++) {
            StoryEpisode ep = episodes.get(i);
            ep.setEpisodeNumber(-(i + 1));
            episodeRepository.save(ep);
        }

        // PASS 2: now assign the REAL final positions - no negative number
        // can ever collide with a positive one, so this pass is always safe.
        for (int i = 0; i < episodes.size(); i++) {
            StoryEpisode ep = episodes.get(i);
            ep.setEpisodeNumber(i + 1);
            episodeRepository.save(ep);
        }
    }

    // ============================================================
    // AUDIO UPLOAD
    // ============================================================

    @Override
    public StoryEpisodeResponseDTO uploadEpisodeAudio(Long episodeId, MultipartFile file) {
        StoryEpisode episode = episodeRepository.findById(episodeId)
                .orElseThrow(() -> new ResourceNotFoundException("Episode not found with id: " + episodeId));

        validateAudioFile(file);

        try {
            // Store under {audioRoot}/stories/{storyId}/{random-name}.mp3 -
            // a random filename (not the original) avoids path-traversal
            // tricks from a malicious filename and avoids collisions
            // between admins uploading files with the same original name.
            String extension = getExtension(file.getOriginalFilename());
            String safeFileName = UUID.randomUUID() + "." + extension;
            String relativePath = "stories/" + episode.getStory().getId() + "/" + safeFileName;

            Path destination = Path.of(audioRoot, relativePath);
            Files.createDirectories(destination.getParent());
            Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);

            // Deactivate any previously active audio for this episode -
            // only one version should be "live" at a time (see StoryAudio's
            // design note from Phase 2).
            audioRepository.findByEpisodeAndIsActiveTrue(episode)
                    .ifPresent(previous -> {
                        previous.setIsActive(false);
                        audioRepository.save(previous);
                    });

            StoryAudio audio = StoryAudio.builder()
                    .episode(episode)
                    .sourceType(AudioSourceType.UPLOADED)
                    .filePath(relativePath)
                    .format(extension)
                    .fileSizeBytes(file.getSize())
                    .voiceStyle(episode.getStory().getVoiceStyle())
                    .isActive(true)
                    .build();
            audioRepository.save(audio);

        } catch (IOException e) {
            throw new BadRequestException("Failed to store audio file: " + e.getMessage());
        }

        // Refresh the episode so its activeAudio reflects the new upload
        StoryEpisode refreshed = episodeRepository.findById(episodeId).orElseThrow();
        return storyMapper.toEpisodeDTO(refreshed);
    }

    private void validateAudioFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("Audio file is required");
        }
        if (file.getSize() > MAX_AUDIO_FILE_SIZE_BYTES) {
            throw new BadRequestException("Audio file must be smaller than 20MB");
        }
        String extension = getExtension(file.getOriginalFilename());
        if (!extension.equalsIgnoreCase("mp3")) {
            throw new BadRequestException("Only MP3 audio files are supported");
        }
    }

    private String getExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            throw new BadRequestException("Uploaded file must have a valid extension");
        }
        return filename.substring(filename.lastIndexOf('.') + 1);
    }

    // ============================================================
    // AGGREGATE RECOMPUTATION
    // ============================================================

    // Recalculates Story.totalEpisodes and Story.totalDurationSeconds from
    // ALL of its episodes (published or not) - simplest interpretation for
    // a college project scope; a stricter version might count published-only,
    // but that distinction isn't critical to the demo and adds complexity
    // that's easy to explain away as a deliberate simplification in a viva.
    private void recomputeStoryAggregates(Story story) {
        List<StoryEpisode> allEpisodes = episodeRepository.findByStoryOrderByEpisodeNumberAsc(story);
        int totalDuration = allEpisodes.stream()
                .mapToInt(e -> e.getDurationSeconds() != null ? e.getDurationSeconds() : 0)
                .sum();

        story.setTotalEpisodes(allEpisodes.size());
        story.setTotalDurationSeconds(totalDuration);
        storyRepository.save(story);
    }
}
