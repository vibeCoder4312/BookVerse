package com.bookverse.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "story_episodes", uniqueConstraints = @UniqueConstraint(columnNames = {"story_id", "episode_number"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StoryEpisode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "story_id", nullable = false)
    private Story story;

    // UNIQUE(story_id, episode_number) above stops two episodes of the
    // same story accidentally getting the same number - which would
    // break "Next Episode" navigation in a confusing, silent way.
    @Column(name = "episode_number", nullable = false)
    private Integer episodeNumber;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "duration_seconds")
    private Integer durationSeconds;

    @Column(name = "is_published", nullable = false)
    @Builder.Default
    private Boolean isPublished = false;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "episode", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<StoryAudio> audioVersions = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    // Convenience method - the "currently playable" audio for this
    // episode, not necessarily the most recently added one (an admin
    // might upload a replacement MP3 without it becoming active yet).
    @Transient
    public StoryAudio getActiveAudio() {
        return audioVersions.stream().filter(StoryAudio::getIsActive).findFirst().orElse(null);
    }
}
