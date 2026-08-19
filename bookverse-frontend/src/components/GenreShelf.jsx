import BookCard from "./BookCard";
import "./GenreShelf.css";

// This component takes a title and an array of books, and renders one
// "shelf" row. Notice it doesn't know or care whether the books came
// from dummyBooks.js or a real API - it just needs an array of book
// objects shaped the way BookCard expects. That separation is the
// whole point of building components around props.
function GenreShelf({ title, books, favoritedIds }) {
  return (
    <section className="genre-shelf">
      <div className="genre-shelf__header">
        <h2>{title}</h2>
      </div>

      {/* CONDITIONAL RENDERING: if there are no books for this genre,
          show an empty-state message instead of a blank row. The
          `books.length === 0 ? ... : ...` is a ternary - a compact
          if/else that can live directly inside JSX. */}
      {books.length === 0 ? (
        <p className="genre-shelf__empty">No titles here yet.</p>
      ) : (
        <div className="genre-shelf__row">
          {/* LISTS: .map() transforms each book object into a <BookCard>.
              The `key` prop is required by React whenever you render a
              list - it helps React track which item is which between
              re-renders, without it React would print a console warning. */}
          {books.map((book) => (
            <BookCard
              key={book.id}
              book={book}
              favorited={favoritedIds ? favoritedIds.has(String(book.id)) : false}
            />
          ))}
        </div>
      )}
    </section>
  );
}

export default GenreShelf;