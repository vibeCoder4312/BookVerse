package com.bookverse.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "reading_progress", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "book_id"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReadingProgress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    // 0-100, a simple percentage - enforced by validation on the DTO,
    // not here (same pattern as Review.rating in Phase 9).
    @Column(nullable = false)
    private Integer progress;

    @Column(name = "last_read_at")
    private LocalDateTime lastReadAt;

    @PrePersist
    @PreUpdate
    protected void onSave() {
        this.lastReadAt = LocalDateTime.now();
    }
}
