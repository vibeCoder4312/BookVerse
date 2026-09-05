package com.bookverse.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

// Tracks resume position at EPISODE granularity, not story granularity -
// "Continue Listening" for a multi-episode story needs to know not just
// which story, but which episode and exactly where in it.
@Entity
@Table(name = "story_progress", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "episode_id"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StoryProgress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "story_id", nullable = false)
    private Story story;

    @ManyToOne
    @JoinColumn(name = "episode_id", nullable = false)
    private StoryEpisode episode;

    @Column(name = "position_seconds", nullable = false)
    @Builder.Default
    private Integer positionSeconds = 0;

    @Column(name = "duration_seconds")
    private Integer durationSeconds; // cached from the episode at save time, for quick % calculations

    @Column(nullable = false)
    @Builder.Default
    private Boolean completed = false;

    @Column(name = "last_listened_at")
    private LocalDateTime lastListenedAt;

    @PrePersist
    @PreUpdate
    protected void onSave() {
        this.lastListenedAt = LocalDateTime.now();
    }
}
