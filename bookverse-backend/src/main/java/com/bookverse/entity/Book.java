package com.bookverse.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "books")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @ManyToOne
    @JoinColumn(name = "author_id", nullable = false)
    private Author author;

    @Enumerated(EnumType.STRING)
    @Column(name = "content_type", nullable = false)
    private ContentType contentType;

    @Column(name = "cover_url")
    private String coverUrl;

    @Column(name = "publication_year")
    private Integer publicationYear;

    @Builder.Default
    private Long views = 0L;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    // Full/preview readable text for the "Read" feature (Phase 14).
    @Column(columnDefinition = "TEXT")
    private String content;

    // NEW: price of a physical copy, in rupees. Used by the "Buy Physical
    // Copy" WhatsApp order feature - previously this was guessed client-side
    // from contentType alone (every Fantasy novel showing the same price),
    // which wasn't realistic. Now every book has its own real price set
    // by the admin (or by the seed generator for demo data).
    // precision=10, scale=2 -> stores values like 1299.99 accurately;
    // BigDecimal (not double/float) is used because floating-point binary
    // types can introduce tiny rounding errors in currency math.
    @Column(precision = 10, scale = 2)
    private BigDecimal price;

    @ManyToMany
    @JoinTable(
            name = "book_categories",
            joinColumns = @JoinColumn(name = "book_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    @Builder.Default
    private Set<Category> categories = new HashSet<>();

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
