// A small palette matching our design tokens - used to give each book
// a consistent "spine" color based on its main genre, since the backend
// doesn't store a color (that's just a frontend visual detail).
const PALETTE = ["#7c6fd6", "#3e8ed0", "#d67a3e", "#e8637a", "#6fcf97", "#9b59b6", "#f2a93b"];

function colorFor(seed) {
  let hash = 0;
  for (let i = 0; i < seed.length; i++) {
    hash = seed.charCodeAt(i) + ((hash << 5) - hash);
  }
  return PALETTE[Math.abs(hash) % PALETTE.length];
}

// Converts a BookResponseDTO (from Spring Boot) into the flatter shape
// our components (BookCard, BookDetails) already expect. This is the
// ONLY place that needs to change if the backend's response shape
// ever changes - every component stays untouched.
export function mapApiBook(apiBook) {
  const genres = apiBook.categories ? Array.from(apiBook.categories) : [];
  return {
    id: apiBook.id,
    title: apiBook.title,
    description: apiBook.description,
    author: apiBook.author?.name ?? "Unknown Author",
    contentType: apiBook.contentType,
    genres,
    rating: null, // reviews/ratings arrive in Phase 9
    year: apiBook.publicationYear,
    spineColor: colorFor(genres[0] ?? apiBook.contentType ?? apiBook.title),
  };
}
