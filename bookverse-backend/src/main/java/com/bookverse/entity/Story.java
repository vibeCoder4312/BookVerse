package com.bookverse.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "stories")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Story {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "short_description")
    private String shortDescription; // shown on story cards, kept separate from the full description

    @Column(name = "cover_image")
    private String coverImage; // URL/path, same convention as Book.coverUrl

    // Plain strings, not FKs - see design note in Phase 2 write-up: a
    // story's author/narrator is a different concept from a book's
    // catalogued Author entity, and forcing that relationship would
    // conflate two unrelated domains.
    private String author;
    private String narrator;

    @Enumerated(EnumType.STRING)
    @Column(name = "voice_style", nullable = false)
    @Builder.Default
    private VoiceStyle voiceStyle = VoiceStyle.NEUTRAL;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StoryLanguage language;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private StoryCategory category;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private StoryStatus status = StoryStatus.DRAFT;

    @Column(name = "is_premium", nullable = false)
    @Builder.Default
    private Boolean isPremium = false;

    // Cached aggregates, recomputed by the service layer whenever
    // episodes change - avoids summing episodes on every single read
    // (a story's episode list doesn't change nearly as often as it's viewed).
    @Column(name = "total_duration_seconds")
    @Builder.Default
    private Integer totalDurationSeconds = 0;

    @Column(name = "total_episodes")
    @Builder.Default
    private Integer totalEpisodes = 0;

    @Column(name = "play_count")
    @Builder.Default
    private Long playCount = 0L; // increments on playback start - powers Trending, same idea as Book.views

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "story", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<StoryEpisode> episodes = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
