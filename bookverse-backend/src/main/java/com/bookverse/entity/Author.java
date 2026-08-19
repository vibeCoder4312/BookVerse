package com.bookverse.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "authors")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Author {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    // columnDefinition lets us override the SQL type Hibernate picks.
    // A bio can be long, so we ask for TEXT instead of the default
    // short VARCHAR, to avoid truncating an author's biography.
    private String bio;

    @Column(name = "photo_url")
    private String photoUrl;

    // mappedBy = "author" means: "the Book entity owns this relationship
    // via its 'author' field - don't create a separate join column here,
    // just look up books that point at me."
    // This is the "one" side of the one-to-many: one Author has many Books.
    @OneToMany(mappedBy = "author")
    @Builder.Default
    private List<Book> books = new ArrayList<>();
}
