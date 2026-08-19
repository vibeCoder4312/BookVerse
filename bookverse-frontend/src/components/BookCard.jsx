import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import { addFavorite, removeFavorite } from "../services/libraryService.js";
import "./BookCard.css";

// `favorited` is an OPTIONAL prop - pages that already know a book's
// favorite status (like the Favorites page itself) can pass it in.
// Pages that don't know (Home, Explore) just leave it undefined, and
// the heart starts unfilled - clicking it still works correctly either way.
function BookCard({ book, favorited = false }) {
  const { isAuthenticated } = useAuth();
  const navigate = useNavigate();

  const [isFavorite, setIsFavorite] = useState(favorited);
  const [saving, setSaving] = useState(false);

  async function handleFavoriteClick(event) {
    event.preventDefault();
    event.stopPropagation();

    // Guard: favoriting requires login. Rather than fail silently or
    // throw a confusing 403 error, we send the user straight to login.
    if (!isAuthenticated) {
      navigate("/login");
      return;
    }

    if (saving) return; // avoid double-clicks firing two requests at once
    setSaving(true);

    // OPTIMISTIC UPDATE: we flip the UI immediately, then confirm with
    // the server. If the request fails, we roll the UI back - this
    // makes the button feel instant instead of waiting on network latency.
    const nextValue = !isFavorite;
    setIsFavorite(nextValue);

    try {
      if (nextValue) {
        await addFavorite(book.id);
      } else {
        await removeFavorite(book.id);
      }
    } catch (err) {
      if (err.response?.status === 409) {
        // Already exists server-side - keep it favorited instead of reverting
      } else {
        console.error("Failed to update favorite", err);
        setIsFavorite(!nextValue); // roll back on failure
      }
    } finally {
      setSaving(false);
    }
  }

  return (
    <Link to={`/books/${book.id}`} className="book-card">
      <div className="book-card__spine" style={{ background: book.spineColor }} />

      <div className="book-card__cover" style={{ background: `linear-gradient(160deg, ${book.spineColor}, #14162b)` }}>
        <span className="book-card__cover-title">{book.title}</span>

        <button
          className={`book-card__fav ${isFavorite ? "is-active" : ""}`}
          onClick={handleFavoriteClick}
          aria-label={isFavorite ? "Remove from favorites" : "Add to favorites"}
        >
          {isFavorite ? "♥" : "♡"}
        </button>
      </div>

      <div className="book-card__body">
        <p className="book-card__type">{book.contentType.replace("_", " ")}</p>
        <h3 className="book-card__title">{book.title}</h3>
        <p className="book-card__author">{book.author}</p>
        <div className="book-card__meta">
          {book.rating ? (
            <span className="book-card__rating">★ {book.rating}</span>
          ) : (
            <span className="book-card__rating">New</span>
          )}
          <span className="book-card__year">{book.year}</span>
        </div>
      </div>
    </Link>
  );
}

export default BookCard;
