import BookCard from "./BookCard";
import BookCardSkeleton from "./BookCardSkeleton";
import "./BookGrid.css";

// A simple, reusable wrapper: give it a books array and what to say
// when it's empty. Used by Explore, Library, Favorites, History, Search -
// anywhere that's "just a grid of book cards".
//
// `loading` is optional - when true, we render skeleton placeholders
// instead of checking books.length, so callers don't need their own
// separate "Loading..." text anymore.
function BookGrid({ books, loading = false, emptyMessage = "Nothing here yet.", skeletonCount = 6 }) {
  if (loading) {
    return (
      <div className="book-grid">
        {Array.from({ length: skeletonCount }).map((_, i) => (
          <BookCardSkeleton key={i} />
        ))}
      </div>
    );
  }

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
