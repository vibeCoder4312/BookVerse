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
// our components (BookCard, BookDetails) already expect.
export function mapApiBook(apiBook) {
  const genres = apiBook.categories ? Array.from(apiBook.categories) : [];
  return {
    id: apiBook.id,
    title: apiBook.title,
    description: apiBook.description,
    author: apiBook.author?.name ?? "Unknown Author",
    contentType: apiBook.contentType,
    genres,
    rating: apiBook.averageRating ?? null,
    reviewCount: apiBook.reviewCount ?? 0,
    year: apiBook.publicationYear,
    spineColor: colorFor(genres[0] ?? apiBook.contentType ?? apiBook.title),
    hasContent: Boolean(apiBook.content),
    // NEW: real per-book price from the backend (a BigDecimal, arrives as
    // a JSON number). Null for any book seeded before this field existed -
    // components fall back to a content-type-based default in that case.
    price: apiBook.price ?? null,
  };
}
