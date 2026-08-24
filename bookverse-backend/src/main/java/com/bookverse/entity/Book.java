package com.bookverse.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
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

    // @ManyToOne = the "many" side: many Books can share the same Author.
    // @JoinColumn tells Hibernate to add an "author_id" foreign key
    // column directly on the books table, pointing at authors.id.
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
    // Nullable - most books in our catalog are metadata-only (Phase 12's
    // generated placeholder data); only a couple of demo books have this
    // populated with actual sample text.
    @Column(columnDefinition = "TEXT")
    private String content;

    // This is the OWNING side of the many-to-many with Category.
    // @JoinTable explicitly defines the join table "book_categories":
    //   - joinColumns: the column pointing back to THIS entity (Book)
    //   - inverseJoinColumns: the column pointing to the OTHER entity (Category)
    // Hibernate creates and manages this join table for us automatically.
    @ManyToMany
    @JoinTable(
            name = "book_categories",
            joinColumns = @JoinColumn(name = "book_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    @Builder.Default
    private Set<Category> categories = new HashSet<>();
    // Using a Set (not List) here prevents accidentally adding the
    // same category to a book twice.

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
