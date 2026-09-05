package com.bookverse.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

// Deliberately SEPARATE from StoryProgress. This is a coarse, story-level
// "you played this recently" log (upserted on each play), used for the
// Listening History page and its "Clear History" button. StoryProgress
// (episode-level resume position) is untouched by clearing history - a
// user shouldn't lose their exact place in a story just because they
// tidied up their history list.
@Entity
@Table(name = "story_history", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "story_id"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StoryHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "story_id", nullable = false)
    private Story story;

    @Column(name = "played_at")
    private LocalDateTime playedAt;

    @PrePersist
    @PreUpdate
    protected void onSave() {
        this.playedAt = LocalDateTime.now();
    }
}
