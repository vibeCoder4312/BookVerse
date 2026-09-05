package com.bookverse.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

// Why this is its own table instead of just an audioUrl column on
// StoryEpisode: an episode can have MULTIPLE audio versions over its
// lifetime (an admin uploads an MP3, then later a TTS engine generates
// an alternate narration, or a replacement file is uploaded) - but only
// ONE should be considered "live" at a time. Modeling this as its own
// entity keeps that history instead of silently overwriting a URL string.
@Entity
@Table(name = "story_audio")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StoryAudio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "episode_id", nullable = false)
    private StoryEpisode episode;

    @Enumerated(EnumType.STRING)
    @Column(name = "source_type", nullable = false)
    private AudioSourceType sourceType;

    // A relative path under the app's local audio storage root, e.g.
    // "stories/haunted-village/episode-1.mp3" - NOT an absolute filesystem
    // path, so the storage root can move without breaking stored data.
    @Column(name = "file_path", nullable = false)
    private String filePath;

    @Column(nullable = false)
    @Builder.Default
    private String format = "mp3";

    @Column(name = "file_size_bytes")
    private Long fileSizeBytes;

    // Which voice preset was used - mainly meaningful for TTS_GENERATED
    // rows; for UPLOADED rows this just records what the admin intended.
    @Enumerated(EnumType.STRING)
    @Column(name = "voice_style")
    private VoiceStyle voiceStyle;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
