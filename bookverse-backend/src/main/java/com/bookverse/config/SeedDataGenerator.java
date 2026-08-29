package com.bookverse.config;

import com.bookverse.entity.*;
import com.bookverse.repository.AuthorRepository;
import com.bookverse.repository.BookRepository;
import com.bookverse.repository.CategoryRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

/**
 * Generates BookVerse's full ~565-book catalog by COMBINING word banks
 * (adjectives + nouns, topics + templates) instead of hand-writing every
 * title. See Phase 12 for the original design rationale - unchanged here.
 *
 * PRICE FEATURE UPDATE: every generated book now gets a real BigDecimal
 * price via priceFor(contentType, index), varied per content type (study
 * textbooks cost more than manga volumes) with per-book variation so
 * books of the same type aren't all identically priced.
 */
public class SeedDataGenerator {

    private static final Random RANDOM = new Random(42); // fixed seed = reproducible runs

    private static final String[] FIRST_NAMES = {
            "Aiden", "Sophia", "Ethan", "Olivia", "Liam", "Emma", "Noah", "Ava",
            "Mason", "Isabella", "Lucas", "Mia", "Henry", "Charlotte", "Owen",
            "Amelia", "Jack", "Harper", "Leo", "Grace", "Elena", "Marcus",
            "Priya", "Daniel", "Naomi"
    };
    private static final String[] LAST_NAMES = {
            "Bennett", "Coleman", "Whitfield", "Harper", "Sinclair", "Rowan",
            "Ashford", "Vaughn", "Pierce", "Lockwood", "Kingston", "Ferris",
            "Blackwood", "Merrow", "Osei", "Callahan", "Nakamura", "Delgado"
    };

    public static void generate(AuthorRepository authorRepository,
                                CategoryRepository categoryRepository,
                                BookRepository bookRepository) {

        List<Author> authorPool = buildAuthorPool(authorRepository);

        for (CategorySpec spec : CATEGORY_SPECS) {
            Category category = getOrCreateCategory(categoryRepository, spec.name());
            List<Book> books = switch (spec.group()) {
                case FICTION -> generateFictionBooks(spec, category, authorPool);
                case NONFICTION -> generateNonfictionBooks(spec, category, authorPool);
                case STUDY -> generateStudyBooks(spec, category, authorPool);
                case SERIES -> generateSeriesBooks(spec, category, authorPool);
                case CHILDRENS -> generateChildrensBooks(spec, category, authorPool);
            };
            bookRepository.saveAll(books);
        }
    }

    // ============================================================
    // CATEGORY DEFINITIONS - matches the spec's distribution table
    // ============================================================

    private enum Group { FICTION, NONFICTION, STUDY, SERIES, CHILDRENS }

    private record CategorySpec(String name, ContentType contentType, int count, Group group) {}

    private static final List<CategorySpec> CATEGORY_SPECS = List.of(
            new CategorySpec("Fiction", ContentType.NOVEL, 50, Group.FICTION),
            new CategorySpec("Fantasy", ContentType.NOVEL, 30, Group.FICTION),
            new CategorySpec("Romance", ContentType.NOVEL, 30, Group.FICTION),
            new CategorySpec("Mystery", ContentType.NOVEL, 25, Group.FICTION),
            new CategorySpec("Thriller", ContentType.NOVEL, 25, Group.FICTION),
            new CategorySpec("Horror", ContentType.NOVEL, 20, Group.FICTION),
            new CategorySpec("Science Fiction", ContentType.NOVEL, 25, Group.FICTION),
            new CategorySpec("Adventure", ContentType.NOVEL, 20, Group.FICTION),
            new CategorySpec("Self Improvement", ContentType.BOOK, 25, Group.NONFICTION),
            new CategorySpec("Psychology", ContentType.BOOK, 20, Group.NONFICTION),
            new CategorySpec("Business", ContentType.BOOK, 20, Group.NONFICTION),
            new CategorySpec("Finance", ContentType.BOOK, 15, Group.NONFICTION),
            new CategorySpec("Biography", ContentType.BOOK, 15, Group.NONFICTION),
            new CategorySpec("History", ContentType.BOOK, 15, Group.NONFICTION),
            new CategorySpec("Programming", ContentType.STUDY_BOOK, 30, Group.STUDY),
            new CategorySpec("Computer Science", ContentType.STUDY_BOOK, 30, Group.STUDY),
            new CategorySpec("Engineering", ContentType.STUDY_BOOK, 20, Group.STUDY),
            new CategorySpec("Mathematics", ContentType.STUDY_BOOK, 15, Group.STUDY),
            new CategorySpec("Manga", ContentType.MANGA, 30, Group.SERIES),
            new CategorySpec("Manhwa", ContentType.MANHWA, 20, Group.SERIES),
            new CategorySpec("Manhua", ContentType.MANHUA, 15, Group.SERIES),
            new CategorySpec("Comics", ContentType.COMIC, 25, Group.SERIES),
            new CategorySpec("Graphic Novels", ContentType.GRAPHIC_NOVEL, 15, Group.SERIES),
            new CategorySpec("Light Novels", ContentType.LIGHT_NOVEL, 15, Group.SERIES),
            new CategorySpec("Children's", ContentType.CHILDRENS_BOOK, 15, Group.CHILDRENS)
    );

    // ============================================================
    // PRICE ASSIGNMENT (new)
    // ============================================================

    // Base price ranges per content type, in rupees - roughly reflects
    // real-world pricing patterns (textbooks cost more than manga volumes).
    private static final Map<ContentType, int[]> PRICE_RANGE_BY_TYPE = Map.of(
            ContentType.NOVEL, new int[]{249, 499},
            ContentType.BOOK, new int[]{299, 599},
            ContentType.STUDY_BOOK, new int[]{399, 899},
            ContentType.MANGA, new int[]{149, 299},
            ContentType.MANHWA, new int[]{149, 299},
            ContentType.MANHUA, new int[]{149, 299},
            ContentType.COMIC, new int[]{199, 399},
            ContentType.GRAPHIC_NOVEL, new int[]{299, 599},
            ContentType.LIGHT_NOVEL, new int[]{249, 449},
            ContentType.CHILDRENS_BOOK, new int[]{149, 349}
    );

    // Deterministically varies the price within that content type's range
    // based on the book's index - same seed, same run, always produces the
    // same prices, so restarting the seeder gives consistent demo data
    // rather than different prices every time.
    private static BigDecimal priceFor(ContentType contentType, int index) {
        int[] range = PRICE_RANGE_BY_TYPE.getOrDefault(contentType, new int[]{199, 399});
        int span = range[1] - range[0];
        int price = range[0] + ((index * 37) % (span + 1)); // 37 = arbitrary fixed spread multiplier
        return BigDecimal.valueOf(price).setScale(2, RoundingMode.UNNECESSARY);
    }

    // ============================================================
    // GROUP 1: FICTION - adjective + noun, flavored per genre
    // ============================================================

    private static final Map<String, String[]> FICTION_ADJECTIVES = Map.of(
            "Fiction", new String[]{"Quiet", "Distant", "Unspoken", "Ordinary", "Fragile", "Lasting", "Simple", "Wandering"},
            "Fantasy", new String[]{"Ancient", "Forgotten", "Silver", "Shattered", "Eternal", "Hidden", "Sacred", "Rising"},
            "Romance", new String[]{"Sweet", "Unexpected", "Whispered", "Tender", "Secret", "Golden", "Endless", "Gentle"},
            "Mystery", new String[]{"Silent", "Curious", "Vanishing", "Hidden", "Final", "Strange", "Missing", "Quiet"},
            "Thriller", new String[]{"Dark", "Relentless", "Final", "Deadly", "Silent", "Rising", "Broken", "Last"},
            "Horror", new String[]{"Haunted", "Cursed", "Forsaken", "Silent", "Twisted", "Dreadful", "Hollow", "Buried"},
            "Science Fiction", new String[]{"Distant", "Quantum", "Forgotten", "Silent", "Last", "Infinite", "Rogue", "Frozen"},
            "Adventure", new String[]{"Lost", "Uncharted", "Wild", "Golden", "Ancient", "Bold", "Endless", "Untamed"}
    );

    private static final Map<String, String[]> FICTION_NOUNS = Map.of(
            "Fiction", new String[]{"Life", "Truth", "Season", "Journey", "House", "Letter", "Silence", "Promise"},
            "Fantasy", new String[]{"Throne", "Kingdom", "Blade", "Prophecy", "Crown", "Realm", "Empire", "Oracle"},
            "Romance", new String[]{"Heart", "Promise", "Summer", "Kiss", "Letter", "Wedding", "Melody", "Embrace"},
            "Mystery", new String[]{"Clue", "Case", "Witness", "Secret", "Manor", "Detective", "Alibi", "Puzzle"},
            "Thriller", new String[]{"Hunt", "Target", "Countdown", "Conspiracy", "Pursuit", "Deadline", "Verdict", "Threat"},
            "Horror", new String[]{"House", "Woods", "Asylum", "Grave", "Shadow", "Night", "Ritual", "Whisper"},
            "Science Fiction", new String[]{"Galaxy", "Colony", "Signal", "Horizon", "Machine", "Star", "Orbit", "Frontier"},
            "Adventure", new String[]{"Expedition", "Island", "Trail", "Voyage", "Map", "Quest", "Frontier", "Peak"}
    );

    private static List<Book> generateFictionBooks(CategorySpec spec, Category category, List<Author> authorPool) {
        String[] adjectives = FICTION_ADJECTIVES.get(spec.name());
        String[] nouns = FICTION_NOUNS.get(spec.name());
        List<Book> books = new ArrayList<>();

        for (int i = 0; i < spec.count(); i++) {
            String adjective = adjectives[i % adjectives.length];
            String noun = nouns[(i / adjectives.length) % nouns.length];
            String title = "The " + adjective + " " + noun;

            books.add(Book.builder()
                    .title(title)
                    .description("A gripping " + spec.name().toLowerCase() + " story that keeps readers turning pages.")
                    .author(pickAuthor(authorPool, title))
                    .contentType(spec.contentType())
                    .publicationYear(1950 + ((i * 7 + spec.name().hashCode()) % 75))
                    .categories(new HashSet<>(Set.of(category)))
                    .price(priceFor(spec.contentType(), i))
                    .build());
        }
        return books;
    }

    // ============================================================
    // GROUP 2: NONFICTION - template + topic
    // ============================================================

    private static final String[] NONFICTION_TEMPLATES = {
            "The Art of %s", "Mastering %s", "%s: A New Perspective",
            "Understanding %s", "A Practical Guide to %s", "The Power of %s"
    };

    private static final Map<String, String[]> NONFICTION_TOPICS = Map.of(
            "Self Improvement", new String[]{"Discipline", "Focus", "Habits", "Confidence", "Productivity", "Mindfulness", "Resilience", "Purpose"},
            "Psychology", new String[]{"Motivation", "Memory", "Perception", "Emotion", "Behavior", "Bias", "Attachment", "Cognition"},
            "Business", new String[]{"Leadership", "Strategy", "Negotiation", "Branding", "Innovation", "Management", "Growth", "Marketing"},
            "Finance", new String[]{"Investing", "Budgeting", "Wealth", "Markets", "Savings", "Retirement", "Trading", "Credit"},
            "History", new String[]{"Empire", "Revolution", "War", "Civilization", "Kingdom", "Republic", "Dynasty", "Frontier"}
    );

    private static List<Book> generateNonfictionBooks(CategorySpec spec, Category category, List<Author> authorPool) {
        List<Book> books = new ArrayList<>();

        // Biography is a special case - it needs a person's name, not a topic
        if (spec.name().equals("Biography")) {
            String[] professions = {"Scientist", "Explorer", "Artist", "Leader", "Inventor", "Pioneer", "Athlete", "Writer"};
            for (int i = 0; i < spec.count(); i++) {
                String name = FIRST_NAMES[i % FIRST_NAMES.length] + " " + LAST_NAMES[(i / FIRST_NAMES.length) % LAST_NAMES.length];
                String profession = professions[i % professions.length];
                String title = name + ": A " + profession + "'s Story";

                books.add(Book.builder()
                        .title(title)
                        .description("A compelling biography chronicling an extraordinary life.")
                        .author(pickAuthor(authorPool, title))
                        .contentType(spec.contentType())
                        .publicationYear(1980 + ((i * 5) % 45))
                        .categories(new HashSet<>(Set.of(category)))
                        .price(priceFor(spec.contentType(), i))
                        .build());
            }
            return books;
        }

        String[] topics = NONFICTION_TOPICS.get(spec.name());
        for (int i = 0; i < spec.count(); i++) {
            String template = NONFICTION_TEMPLATES[i % NONFICTION_TEMPLATES.length];
            String topic = topics[(i / NONFICTION_TEMPLATES.length) % topics.length];
            String title = String.format(template, topic);

            books.add(Book.builder()
                    .title(title)
                    .description("A practical, insight-driven look at " + topic.toLowerCase() + ".")
                    .author(pickAuthor(authorPool, title))
                    .contentType(spec.contentType())
                    .publicationYear(1980 + ((i * 6 + spec.name().hashCode()) % 45))
                    .categories(new HashSet<>(Set.of(category)))
                    .price(priceFor(spec.contentType(), i))
                    .build());
        }
        return books;
    }

    // ============================================================
    // GROUP 3: STUDY/TECH - template + real subject-matter term
    // ============================================================

    private static final String[] STUDY_TEMPLATES = {
            "Introduction to %s", "Advanced %s", "%s in Practice",
            "A Beginner's Guide to %s", "%s: Concepts and Applications", "Mastering %s"
    };

    private static final Map<String, String[]> STUDY_TOPICS = Map.of(
            "Programming", new String[]{"Java", "Python", "JavaScript", "C++", "Go", "Rust", "Kotlin", "Ruby"},
            "Computer Science", new String[]{"Data Structures", "Algorithms", "Operating Systems", "Computer Networks", "Databases", "Compilers", "Distributed Systems", "Software Engineering"},
            "Engineering", new String[]{"Mechanical Design", "Circuit Theory", "Thermodynamics", "Structural Analysis", "Control Systems", "Fluid Mechanics", "Materials Science", "Robotics"},
            "Mathematics", new String[]{"Calculus", "Linear Algebra", "Probability", "Statistics", "Discrete Math", "Number Theory", "Differential Equations", "Topology"}
    );

    private static List<Book> generateStudyBooks(CategorySpec spec, Category category, List<Author> authorPool) {
        String[] topics = STUDY_TOPICS.get(spec.name());
        List<Book> books = new ArrayList<>();

        for (int i = 0; i < spec.count(); i++) {
            String template = STUDY_TEMPLATES[i % STUDY_TEMPLATES.length];
            String topic = topics[(i / STUDY_TEMPLATES.length) % topics.length];
            String title = String.format(template, topic);

            books.add(Book.builder()
                    .title(title)
                    .description("A clear, example-driven guide to " + topic + ".")
                    .author(pickAuthor(authorPool, title))
                    .contentType(spec.contentType())
                    .publicationYear(1995 + ((i * 4 + spec.name().hashCode()) % 30))
                    .categories(new HashSet<>(Set.of(category)))
                    .price(priceFor(spec.contentType(), i))
                    .build());
        }
        return books;
    }

    // ============================================================
    // GROUP 4: SERIES (Manga/Manhwa/Manhua/Comics/Graphic Novels/
    // Light Novels) - original adjective + noun, with a volume number
    // ============================================================

    private static final String[] SERIES_ADJECTIVES = {
            "Silent", "Crimson", "Eternal", "Broken", "Shattered", "Lost",
            "Sacred", "Forgotten", "Rising", "Twin", "Hollow", "Iron"
    };
    private static final String[] SERIES_NOUNS = {
            "Blade", "Kingdom", "Throne", "Shadow", "Phoenix", "Reaper",
            "Guardian", "Legacy", "Empire", "Oracle", "Fang", "Ash"
    };

    private static List<Book> generateSeriesBooks(CategorySpec spec, Category category, List<Author> authorPool) {
        List<Book> books = new ArrayList<>();

        for (int i = 0; i < spec.count(); i++) {
            String adjective = SERIES_ADJECTIVES[i % SERIES_ADJECTIVES.length];
            String noun = SERIES_NOUNS[(i / SERIES_ADJECTIVES.length) % SERIES_NOUNS.length];
            int volume = (i % 3) + 1; // Vol. 1-3, cycling
            String title = adjective + " " + noun + ", Vol. " + volume;

            books.add(Book.builder()
                    .title(title)
                    .description("An action-packed series praised for its art and storytelling.")
                    .author(pickAuthor(authorPool, title))
                    .contentType(spec.contentType())
                    .publicationYear(1990 + ((i * 3 + spec.name().hashCode()) % 35))
                    .categories(new HashSet<>(Set.of(category)))
                    .price(priceFor(spec.contentType(), i))
                    .build());
        }
        return books;
    }

    // ============================================================
    // GROUP 5: CHILDREN'S - animal + activity
    // ============================================================

    private static final String[] CHILD_ANIMALS = {"Bunny", "Fox", "Bear", "Owl", "Turtle", "Duckling", "Squirrel", "Puppy"};
    private static final String[] CHILD_ACTIVITIES = {
            "Big Adventure", "First Day", "Magic Forest", "Great Journey",
            "Secret Garden", "Starry Night", "Little Journey", "Sunny Day"
    };

    private static List<Book> generateChildrensBooks(CategorySpec spec, Category category, List<Author> authorPool) {
        List<Book> books = new ArrayList<>();

        for (int i = 0; i < spec.count(); i++) {
            String animal = CHILD_ANIMALS[i % CHILD_ANIMALS.length];
            String activity = CHILD_ACTIVITIES[(i / CHILD_ANIMALS.length) % CHILD_ACTIVITIES.length];
            String title = animal + "'s " + activity;

            books.add(Book.builder()
                    .title(title)
                    .description("A warm, gentle story perfect for young readers.")
                    .author(pickAuthor(authorPool, title))
                    .contentType(spec.contentType())
                    .publicationYear(1995 + (i * 2) % 30)
                    .categories(new HashSet<>(Set.of(category)))
                    .price(priceFor(spec.contentType(), i))
                    .build());
        }
        return books;
    }

    // ============================================================
    // SHARED HELPERS
    // ============================================================

    private static List<Author> buildAuthorPool(AuthorRepository authorRepository) {
        List<Author> pool = new ArrayList<>();
        for (String first : FIRST_NAMES) {
            for (String last : LAST_NAMES) {
                if (pool.size() >= 60) break; // 60 authors is plenty to share across 565 books
                pool.add(Author.builder().name(first + " " + last).build());
            }
        }
        return authorRepository.saveAll(pool);
    }

    private static Author pickAuthor(List<Author> authorPool, String title) {
        int index = Math.abs(title.hashCode()) % authorPool.size();
        return authorPool.get(index);
    }

    private static Category getOrCreateCategory(CategoryRepository categoryRepository, String name) {
        return categoryRepository.findByName(name)
                .orElseGet(() -> categoryRepository.save(Category.builder().name(name).build()));
    }
}
