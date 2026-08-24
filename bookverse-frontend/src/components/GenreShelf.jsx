import BookCard from "./BookCard";
import BookCardSkeleton from "./BookCardSkeleton";
import "./GenreShelf.css";

// This component takes a title and an array of books, and renders one
// "shelf" row. Notice it doesn't know or care whether the books came
// from dummyBooks.js or a real API - it just needs an array of book
// objects shaped the way BookCard expects. That separation is the
// whole point of building components around props.
function GenreShelf({ title, books, loading = false }) {
  return (
    <section className="genre-shelf">
      <div className="genre-shelf__header">
        <h2>{title}</h2>
      </div>

      {loading ? (
        <div className="genre-shelf__row">
          {Array.from({ length: 6 }).map((_, i) => (
            <BookCardSkeleton key={i} />
          ))}
        </div>
      ) : books.length === 0 ? (
        /* CONDITIONAL RENDERING: if there are no books for this genre,
           show an empty-state message instead of a blank row. */
        <p className="genre-shelf__empty">No titles here yet.</p>
      ) : (
        <div className="genre-shelf__row">
          {books.map((book) => (
            <BookCard key={book.id} book={book} />
          ))}
        </div>
      )}
    </section>
  );
}

export default GenreShelf;
