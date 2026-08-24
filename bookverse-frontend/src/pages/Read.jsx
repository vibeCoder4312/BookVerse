import { useState, useEffect, useRef } from "react";
import { useParams, Link } from "react-router-dom";
import { fetchBookById } from "../services/bookService";
import { fetchProgress, updateProgress } from "../services/progressService";
import { useAuth } from "../context/AuthContext";
import { useToast } from "../context/ToastContext";
import "./Read.css";

function Read() {
  const { id } = useParams();
  const { isAuthenticated } = useAuth();
  const { success } = useToast();

  const [book, setBook] = useState(null);
  const [loading, setLoading] = useState(true);
  const [progress, setProgress] = useState(0);
  const contentRef = useRef(null);
  const hasShownResumeToast = useRef(false);

  useEffect(() => {
    async function load() {
      try {
        setLoading(true);
        const data = await fetchBookById(id);
        setBook(data); // raw API shape here - we need the full `content` field,

        if (isAuthenticated) {
          const progressData = await fetchProgress(id);
          setProgress(progressData.progress);

          // If they're resuming partway through, scroll to roughly that
          // point and let them know - a nice touch, not just silent.
          if (progressData.progress > 0 && progressData.progress < 100 && !hasShownResumeToast.current) {
            hasShownResumeToast.current = true;
            success(`Resuming from ${progressData.progress}%`);
          }
        }
      } catch (err) {
        console.error("Failed to load book for reading", err);
      } finally {
        setLoading(false);
      }
    }
    load();
  }, [id, isAuthenticated]);

  // Restore scroll position once, after content has actually rendered
  // (can't scroll to a percentage of a div that has zero height yet).
  useEffect(() => {
    if (!loading && progress > 0 && contentRef.current) {
      const el = contentRef.current;
      el.scrollTop = (progress / 100) * (el.scrollHeight - el.clientHeight);
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [loading]);

  async function handleMarkProgress(value) {
    if (!isAuthenticated) return;
    setProgress(value);
    try {
      await updateProgress(id, value);
      success(value === 100 ? "Marked as finished!" : `Progress saved: ${value}%`);
    } catch (err) {
      console.error("Failed to save progress", err);
    }
  }

  if (loading) {
    return <div className="page-container read-page__status"><p>Loading...</p></div>;
  }

  if (!book || !book.content) {
    return (
      <div className="page-container read-page__status">
        <p>This book doesn't have readable content available yet.</p>
        <Link to={`/books/${id}`}>Back to book details</Link>
      </div>
    );
  }

  return (
    <div className="page-container read-page">
      <div className="read-page__header">
        <Link to={`/books/${id}`} className="read-page__back">← Back to details</Link>
        <h1>{book.title}</h1>
        <p className="read-page__author">by {book.author?.name}</p>
      </div>

      {!isAuthenticated && (
        <p className="read-page__note">Log in to save your reading progress across visits.</p>
      )}

      <div className="read-page__content" ref={contentRef}>
        {/* Splitting on blank lines and rendering each as its own
            paragraph - our seeded content uses double-newlines between
            paragraphs, same as any plain-text manuscript would. */}
        {book.content.split("\n\n").map((paragraph, i) => (
          <p key={i}>{paragraph.trim()}</p>
        ))}
      </div>

      {isAuthenticated && (
        <div className="read-page__progress">
          <span className="read-page__progress-label">{progress}% read</span>
          <div className="read-page__progress-buttons">
            <button onClick={() => handleMarkProgress(25)}>25%</button>
            <button onClick={() => handleMarkProgress(50)}>50%</button>
            <button onClick={() => handleMarkProgress(75)}>75%</button>
            <button onClick={() => handleMarkProgress(100)}>Mark Finished</button>
          </div>
        </div>
      )}
    </div>
  );
}

export default Read;
