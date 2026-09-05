package com.bookverse.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

// Deliberately SEPARATE from the existing Category table (used for
// books), even though names like "Horror" or "Romance" overlap. Reason:
// the book Category table feeds the Explore page's book filters - mixing
// in story-only categories like "Kids & Family" or "Motivation" would
// make them incorrectly show up as book filter options too, since
// nothing currently distinguishes "this category is for books" from
// "this category is for stories".
@Entity
@Table(name = "story_categories")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StoryCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name; // e.g. "Horror", "Kids & Family", "Motivation"

    @OneToMany(mappedBy = "category")
    @Builder.Default
    private List<Story> stories = new ArrayList<>();
}
