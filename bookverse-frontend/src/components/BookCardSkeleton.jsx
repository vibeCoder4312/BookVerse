import "./BookCardSkeleton.css";

// A placeholder shaped exactly like a real BookCard, shown WHILE data
// is loading. This is far less jarring than a blank screen or a plain
// "Loading..." text - the user sees the page's actual layout immediately,
// just with pulsing gray boxes where content will appear.
function BookCardSkeleton() {
  return (
    <div className="book-card-skeleton">
      <div className="book-card-skeleton__cover" />
      <div className="book-card-skeleton__line book-card-skeleton__line--title" />
      <div className="book-card-skeleton__line book-card-skeleton__line--author" />
    </div>
  );
}

export default BookCardSkeleton;
