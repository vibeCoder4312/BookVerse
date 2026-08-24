import { useState, useEffect } from "react";
import { useParams, Link, useNavigate } from "react-router-dom";
import { fetchBookById } from "../services/bookService";
import { addFavorite, removeFavorite, addOrUpdateLibraryEntry, recordHistoryView } from "../services/libraryService";
import { useAuth } from "../context/AuthContext";
import { useToast } from "../context/ToastContext";
import { mapApiBook } from "../utils/mapBook";
import "./BookDetails.css";

function BookDetails() {
  const { id } = useParams();
  const { isAuthenticated } = useAuth();
  const navigate = useNavigate();
  const { success, error: showError } = useToast();

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

        if (isAuthenticated) {
          recordHistoryView(id).catch((err) => console.error("Failed to record history", err));
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
      success(next ? "Added to favorites" : "Removed from favorites");
    } catch (err) {
      console.error(err);
      setIsFavorite(!next);
      showError("Couldn't update favorites - please try again");
    }
  }

  async function handleAddToLibrary() {
    if (!isAuthenticated) return navigate("/login");
    try {
      await addOrUpdateLibraryEntry(id, "SAVED");
      setInLibrary(true);
      success("Added to your library");
    } catch (err) {
      console.error(err);
      showError("Couldn't add to library - please try again");
    }
  }

  if (loading) {
    return (
      <div className="page-container book-details">
        <div className="book-details__cover book-details__cover--skeleton" />
        <div className="book-details__info">
          <div className="book-details__skeleton-line" style={{ width: "30%" }} />
          <div className="book-details__skeleton-line" style={{ width: "60%", height: "2rem" }} />
          <div className="book-details__skeleton-line" style={{ width: "40%" }} />
        </div>
      </div>
    );
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
          {book.hasContent && (
            <Link to={`/books/${id}/read`} className="btn btn--primary">
              📖 Read
            </Link>
          )}
          <button className="btn btn--ghost" onClick={handleAddToLibrary} disabled={inLibrary}>
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
