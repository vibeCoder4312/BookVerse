import { useState, useEffect } from "react";
import { useParams, Link, useNavigate } from "react-router-dom";
import { fetchBookById } from "../services/bookService";
import {
  addFavorite,
  removeFavorite,
  addOrUpdateLibraryEntry,
  recordHistoryView,
  fetchFavorites,
  fetchLibrary,
} from "../services/libraryService.js";
import { useAuth } from "../context/AuthContext";
import { mapApiBook } from "../utils/mapBook";
import "./BookDetails.css";

function BookDetails() {
  const { id } = useParams();
  const { isAuthenticated } = useAuth();
  const navigate = useNavigate();

  const [book, setBook] = useState(null);
  const [loading, setLoading] = useState(true);
  const [notFound, setNotFound] = useState(false);
  const [isFavorite, setIsFavorite] = useState(false);
  const [inLibrary, setInLibrary] = useState(false);

  useEffect(() => {
    async function loadBook() {
      try {
        setLoading(true);
        setNotFound(false);
        const data = await fetchBookById(id);
        setBook(mapApiBook(data));

        // Only logged-in users get a reading history - this call is
        // "fire and forget": we don't await UI state on it, a failure
        // here shouldn't block the user from viewing the page.
        if (isAuthenticated) {
          recordHistoryView(id).catch((err) => console.error("Failed to record history", err));

          // Check whether this book is already favorited / in library,
          // so the buttons reflect real saved state instead of always
          // starting as false.
          try {
            const [favorites, library] = await Promise.all([fetchFavorites(), fetchLibrary()]);

            const matchesBook = (item) => {
              const itemId = item.bookId ?? item.id ?? item.book?.id;
              return String(itemId) === String(id);
            };

            setIsFavorite(favorites.some(matchesBook));
            setInLibrary(library.some(matchesBook));
          } catch (err) {
            console.error("Failed to load favorite/library status", err);
          }
        }
      } catch (err) {
        if (err.response?.status === 404) {
          setNotFound(true);
        } else {
          console.error(err);
        }
      } finally {
        setLoading(false);
      }
    }
    loadBook();
  }, [id, isAuthenticated]);

  async function handleFavoriteToggle() {
    if (!isAuthenticated) return navigate("/login");
    const next = !isFavorite;
    setIsFavorite(next);
    try {
      next ? await addFavorite(id) : await removeFavorite(id);
    } catch (err) {
      if (err.response?.status === 409) {
        // Already exists server-side - keep UI as favorited rather
        // than reverting.
        setIsFavorite(true);
      } else {
        console.error(err);
        setIsFavorite(!next);
      }
    }
  }

  async function handleAddToLibrary() {
    if (!isAuthenticated) return navigate("/login");
    try {
      await addOrUpdateLibraryEntry(id, "SAVED");
      setInLibrary(true);
    } catch (err) {
      if (err.response?.status === 409) {
        setInLibrary(true);
      } else {
        console.error(err);
      }
    }
  }

  if (loading) {
    return <div className="page-container book-details__missing"><p>Loading...</p></div>;
  }

  if (notFound || !book) {
    return (
      <div className="page-container book-details__missing">
        <p>We couldn't find that book.</p>
        <Link to="/explore">Back to Explore</Link>
      </div>
    );
  }

  return (
    <div className="page-container book-details">
      <div
        className="book-details__cover"
        style={{ background: `linear-gradient(160deg, ${book.spineColor}, #14162b)` }}
      >
        <span>{book.title}</span>
      </div>

      <div className="book-details__info">
        <p className="book-details__type">{book.contentType.replace("_", " ")}</p>
        <h1>{book.title}</h1>
        <p className="book-details__author">by {book.author}</p>

        <div className="book-details__meta">
          {book.rating ? <span>★ {book.rating}</span> : <span>New - no ratings yet</span>}
          <span>{book.year}</span>
          <span>{book.genres.join(", ")}</span>
        </div>

        <div className="book-details__actions">
          <button className="btn btn--primary" onClick={handleAddToLibrary} disabled={inLibrary}>
            {inLibrary ? "In Your Library" : "Add to Library"}
          </button>
          <button className="btn btn--ghost" onClick={handleFavoriteToggle}>
            {isFavorite ? "♥ Favorited" : "♡ Favorite"}
          </button>
        </div>

        {book.description && <p className="book-details__description">{book.description}</p>}
      </div>
    </div>
  );
}

export default BookDetails;