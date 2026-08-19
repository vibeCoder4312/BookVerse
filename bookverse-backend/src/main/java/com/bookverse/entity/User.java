package com.bookverse.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

// @Entity tells Hibernate "this class maps to a database table".
// @Table lets us pick the exact table name (otherwise it'd default to
// the class name, "User" - which is actually a RESERVED WORD in some
// SQL dialects, so naming it explicitly avoids that landmine).
@Entity
@Table(name = "users")
// Lombok annotations - these auto-generate code for us at compile time
// so we don't have to hand-write getters, setters, and constructors:
@Getter
@Setter
@NoArgsConstructor   // generates an empty constructor: new User()
@AllArgsConstructor  // generates a constructor with every field
@Builder             // lets us write User.builder().name("...").build()
public class User {

    @Id // marks this field as the table's primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // IDENTITY means the database itself auto-increments this value
    // (1, 2, 3, ...) every time a new row is inserted. We never set it manually.
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    // unique = true creates a UNIQUE constraint in SQL - the database
    // itself will reject a second user trying to register with the
    // same email, even if our Java code had a bug and forgot to check.
    private String email;

    @Column(nullable = false)
    private String password; // will store a BCrypt HASH, never plain text (Phase 7)

    @Enumerated(EnumType.STRING)
    // Without this, Hibernate would store the enum as a number (0, 1).
    // STRING makes it store the actual word "USER" or "ADMIN" in the
    // database column - far easier to read when you open SSMS.
    @Column(nullable = false)
    @Builder.Default
    private Role role = Role.USER; // every new user defaults to USER, not ADMIN

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    // This method runs automatically right before Hibernate INSERTs this
    // row for the first time - a clean way to auto-stamp the creation time
    // without the caller having to remember to set it.
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
