import BookCard from "./BookCard";
import "./BookGrid.css";

// A simple, reusable wrapper: give it a books array and what to say
// when it's empty. Used by Explore, Library, Favorites, History, and
// eventually Search - anywhere that's "just a grid of book cards".
function BookGrid({ books, emptyMessage = "Nothing here yet." }) {
  if (books.length === 0) {
    return <p className="book-grid__empty">{emptyMessage}</p>;
  }

  return (
    <div className="book-grid">
      {books.map((book) => (
        <BookCard key={book.id} book={book} favorited={book.favorited ?? false} />
      ))}
    </div>
  );
}

export default BookGrid;
