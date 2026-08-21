package com.bookverse.config;

import com.bookverse.entity.*;
import com.bookverse.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;

import java.util.Set;

// @Component tells Spring "create one instance of this and manage it".
// Implementing CommandLineRunner means Spring will automatically call
// run() once, right after the application finishes starting up.
// This is a common beginner-friendly way to seed test data - later in
// Phase 12 we'll replace this with a proper 500+ record seeding strategy.
@Component
@RequiredArgsConstructor
// Lombok generates a constructor that takes all 'final' fields below
// and assigns them - this is how Spring "injects" the repositories in,
// a pattern called Dependency Injection. We never write "new UserRepository()"
// ourselves; Spring hands us the ready-to-use instance automatically.
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final AuthorRepository authorRepository;
    private final CategoryRepository categoryRepository;
    private final BookRepository bookRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        // Guard clause: only seed if the books table is empty, so we
        // don't insert duplicates every single time you restart the app.
        if (bookRepository.count() > 0) {
            System.out.println("Seed data already exists, skipping seeding.");
            return;
        }

        // --- Categories ---
        Category fantasy = categoryRepository.save(Category.builder().name("Fantasy").build());
        Category adventure = categoryRepository.save(Category.builder().name("Adventure").build());
        Category programming = categoryRepository.save(Category.builder().name("Programming").build());

        // --- Authors ---
        Author tolkien = authorRepository.save(
                Author.builder()
                        .name("J.R.R. Tolkien")
                        .bio("English writer, best known for The Hobbit and The Lord of the Rings.")
                        .build()
        );

        Author martin = authorRepository.save(
                Author.builder()
                        .name("Robert C. Martin")
                        .bio("Software engineer and author, known for Clean Code.")
                        .build()
        );

        // --- Books (this is where the relationships come together) ---
        bookRepository.save(
                Book.builder()
                        .title("The Hobbit")
                        .description("A hobbit's unexpected journey to reclaim a mountain kingdom.")
                        .author(tolkien)
                        .contentType(ContentType.NOVEL)
                        .publicationYear(1937)
                        .categories(Set.of(fantasy, adventure)) // many-to-many in action
                        .build()
        );

        bookRepository.save(
                Book.builder()
                        .title("Clean Code")
                        .description("A handbook of agile software craftsmanship.")
                        .author(martin)
                        .contentType(ContentType.STUDY_BOOK)
                        .publicationYear(2008)
                        .categories(Set.of(programming))
                        .build()
        );

        // --- Phase 12: the full 500+ book catalog ---
        // Generated programmatically (word-bank combinations), not typed
        // out by hand - see SeedDataGenerator.java for how it works.
        SeedDataGenerator.generate(authorRepository, categoryRepository, bookRepository);

        // --- A test user ---
        // Password is now properly BCrypt-hashed via passwordEncoder, so you
        // can actually log in with these credentials once Phase 7 is wired up:
        //   email: student@bookverse.com
        //   password: Password123
        userRepository.save(
                User.builder()
                        .name("Test Student")
                        .email("student@bookverse.com")
                        .password(passwordEncoder.encode("Password123"))
                        .role(Role.USER)
                        .build()
        );

        System.out.println("Seed data inserted successfully! Total books: " + bookRepository.count());
    }
}
