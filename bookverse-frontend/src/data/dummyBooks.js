// This is plain, static JavaScript data - just an array of objects.
// No React or API involved here. We use this in Phase 5 so pages have
// something to render while we're still learning React basics.
// In Phase 6, we'll DELETE this file's usage and replace it with real
// data fetched from our Spring Boot API - the components won't need
// to change much, since they'll receive the same *shape* of data.

export const dummyBooks = [
  { id: 1, title: "The Hobbit", author: "J.R.R. Tolkien", contentType: "NOVEL", genres: ["Fantasy", "Adventure"], rating: 4.8, year: 1937, spineColor: "#7c6fd6" },
  { id: 1, title: "The Hobbit", author: "J.R.R. Tolkien", contentType: "NOVEL", genres: ["Fantasy", "Adventure"], rating: 4.8, year: 1937, spineColor: "#7c6fd6" },
  { id: 2, title: "Clean Code", author: "Robert C. Martin", contentType: "STUDY_BOOK", genres: ["Programming"], rating: 4.6, year: 2008, spineColor: "#3e8ed0" },
  { id: 3, title: "Dune", author: "Frank Herbert", contentType: "NOVEL", genres: ["Science Fiction"], rating: 4.7, year: 1965, spineColor: "#d67a3e" },
  { id: 4, title: "One Piece Vol. 1", author: "Eiichiro Oda", contentType: "MANGA", genres: ["Adventure", "Manga"], rating: 4.9, year: 1997, spineColor: "#e8637a" },
  { id: 5, title: "Atomic Habits", author: "James Clear", contentType: "BOOK", genres: ["Self Improvement"], rating: 4.5, year: 2018, spineColor: "#6fcf97" },
  { id: 6, title: "Solo Leveling Vol. 1", author: "Chugong", contentType: "MANHWA", genres: ["Fantasy", "Manga"], rating: 4.7, year: 2016, spineColor: "#9b59b6" },
  { id: 7, title: "Thinking, Fast and Slow", author: "Daniel Kahneman", contentType: "BOOK", genres: ["Psychology"], rating: 4.4, year: 2011, spineColor: "#f2a93b" },
  { id: 8, title: "Introduction to Algorithms", author: "Thomas H. Cormen", contentType: "STUDY_BOOK", genres: ["Computer Science"], rating: 4.6, year: 2009, spineColor: "#3e8ed0" },
  { id: 9, title: "The Silent Patient", author: "Alex Michaelides", contentType: "NOVEL", genres: ["Thriller", "Mystery"], rating: 4.3, year: 2019, spineColor: "#4a4a6a" },
  { id: 10, title: "Berserk Vol. 1", author: "Kentaro Miura", contentType: "MANGA", genres: ["Horror", "Manga"], rating: 4.9, year: 1990, spineColor: "#8b3a3a" },
  { id: 11, title: "Sapiens", author: "Yuval Noah Harari", contentType: "BOOK", genres: ["History"], rating: 4.6, year: 2011, spineColor: "#d6b93e" },
  { id: 12, title: "The Way of Kings", author: "Brandon Sanderson", contentType: "NOVEL", genres: ["Fantasy"], rating: 4.8, year: 2010, spineColor: "#7c6fd6" },
];

// A small helper - components can import this instead of re-writing
// this filter logic themselves. Takes a genre name, returns matching books.
export function getBooksByGenre(genreName) {
  return dummyBooks.filter((book) => book.genres.includes(genreName));
}
