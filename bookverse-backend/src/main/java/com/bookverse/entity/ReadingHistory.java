package com.bookverse.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "reading_history", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "book_id"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReadingHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    // Unlike createdAt elsewhere, this ISN'T set with updatable = false -
    // every time the user re-opens a book we already logged, we want to
    // UPDATE this timestamp (moving it to the top of "recently viewed"),
    // not insert a second row for the same book.
    @Column(name = "opened_at")
    private LocalDateTime openedAt;

    @PrePersist
    @PreUpdate
    protected void onSave() {
        this.openedAt = LocalDateTime.now();
    }
}
