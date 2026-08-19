package com.bookverse.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "categories")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name; // e.g. "Fantasy", "Programming", "Manga"

    // mappedBy = "categories" points back at the field named "categories"
    // inside Book. This side doesn't own the join table - Book does
    // (we'll see @JoinTable on the Book entity next).
    @ManyToMany(mappedBy = "categories")
    @Builder.Default
    private List<Book> books = new ArrayList<>();
}
