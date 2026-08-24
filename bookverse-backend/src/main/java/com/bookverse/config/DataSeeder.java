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

        // --- Phase 14: two demo books with actual readable content ---
        // These titles and text are ORIGINAL, written for this project -
        // not real published works, and not claimed to be public domain
        // reproductions. This keeps the reading feature demonstrable
        // without any copyright ambiguity.
        bookRepository.save(
                Book.builder()
                        .title("A Quiet Morning in Maple Hollow")
                        .description("A gentle short story about a small village waking up to an unusual visitor.")
                        .author(tolkien) // reusing an existing author for simplicity
                        .contentType(ContentType.NOVEL)
                        .publicationYear(2024)
                        .categories(Set.of(fantasy))
                        .content("""
                                The mist over Maple Hollow lifted slowly that morning, the way it always \
                                did when the seasons were about to turn. Elenor Ashby stood at her window, \
                                a cup of chamomile tea warming her hands, and watched the fog curl away from \
                                the rooftops one by one, as though the village were waking from a long sleep.

                                It was the baker, old Tomlin, who saw it first - a fox sitting calmly in the \
                                middle of the town square, its fur an impossible shade of silver, watching the \
                                shopfronts with an attention that felt far too deliberate for an ordinary animal. \
                                He nearly dropped his tray of morning loaves.

                                By the time the church bells rang for the hour, half the Hollow had gathered at \
                                a respectful distance, murmuring theories. A sign of good fortune, said some. \
                                A warning, said others, remembering their grandmothers' stories about silver foxes \
                                and the debts owed to the old woods.

                                Elenor, who had never much cared for superstition, found herself walking toward \
                                it anyway, teacup still in hand. The fox did not flee as she approached. It tilted \
                                its head, considering her the way one considers an old friend arriving later than \
                                expected.

                                "You're a long way from the tree line," she said quietly.

                                The fox blinked once, slow and deliberate, and for a moment - just a moment - \
                                Elenor could have sworn it understood her perfectly. Then it rose, stretched as \
                                any ordinary fox might, and trotted off toward the edge of town, pausing once to \
                                glance back over its shoulder.

                                She followed. Of course she followed. It was, after all, the first interesting \
                                thing to happen in Maple Hollow in years, and Elenor had always been the sort of \
                                person who couldn't leave an open door unexplored - not even one shaped like a \
                                silver fox slipping quietly back into the woods at the edge of morning.

                                What she found beyond the tree line, and what it would cost her to learn it, is \
                                a story for the next chapter.
                                """)
                        .build()
        );

        bookRepository.save(
                Book.builder()
                        .title("Notes from the Lighthouse")
                        .description("A quiet, atmospheric short story told through a lighthouse keeper's journal entries.")
                        .author(martin) // reusing an existing author for simplicity
                        .contentType(ContentType.NOVEL)
                        .publicationYear(2023)
                        .categories(Set.of(fantasy))
                        .content("""
                                Entry the first.

                                The supply boat left an hour ago, and already the silence has a texture to it - \
                                thick, briny, punctuated only by the slow turning of the lamp above and the gulls \
                                arguing over something on the rocks below. I am told the last keeper left in a \
                                hurry. No one will tell me why, only that the lamp must never go dark, not even \
                                for a night, and that I should keep a light burning in the keeper's room as well, \
                                just in case.

                                Just in case of what, nobody says.

                                Entry the second.

                                Three days in, and I've settled into the rhythm of it: wind the mechanism at dusk, \
                                check the oil at midnight, watch the horizon until the sky turns the color of a \
                                healing bruise. The isolation suits me more than I expected. There is a kind of \
                                peace in being the only fixed point in a great deal of moving water.

                                Last night I thought I saw a second light out past the reef, low against the \
                                waterline, where no light should be. It was gone by the time I fetched the glass. \
                                Likely a fishing boat running without proper lanterns. Likely nothing at all.

                                Entry the ninth.

                                It was not nothing.

                                I have seen it four nights running now, always in the same place, always vanishing \
                                the moment I try to look at it directly - as if it knows the exact edge of my \
                                vision and stays just outside it. Tonight I did not reach for the glass. Tonight I \
                                simply watched it back, and for the first time, it did not disappear.

                                It held steady. And then, slowly, it began to blink - once, twice, in a pattern \
                                too deliberate to be a trick of the water.

                                I have started keeping this journal somewhere the next keeper will find it easily. \
                                I suspect I understand now why the last one left in such a hurry. I am not \
                                frightened, exactly. I am simply no longer certain which of us is keeping watch \
                                over the other.
                                """)
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
