import { useState, useEffect } from "react";
import { fetchLibrary } from "../services/libraryService.js";
import { mapApiBook } from "../utils/mapBook";
import BookGrid from "../components/BookGrid";
import "./ListPage.css";

function Library() {
  const [entries, setEntries] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchLibrary()
      .then(setEntries)
      .catch((err) => console.error(err))
      .finally(() => setLoading(false));
  }, []);

  // Group the flat list of entries into three buckets by status.
  // Each entry looks like { book: {...}, status: "SAVED", addedAt: "..." }
  const currentlyReading = entries.filter((e) => e.status === "CURRENTLY_READING").map((e) => mapApiBook(e.book));
  const saved = entries.filter((e) => e.status === "SAVED").map((e) => mapApiBook(e.book));
  const completed = entries.filter((e) => e.status === "COMPLETED").map((e) => mapApiBook(e.book));

  return (
    <div className="page-container list-page">
      <h1>My Library</h1>

      {loading ? (
        <p className="list-page__status">Loading...</p>
      ) : (
        <>
          <div className="list-page__section">
            <h2>Currently Reading</h2>
            <BookGrid books={currentlyReading} emptyMessage="Nothing in progress right now." />
          </div>

          <div className="list-page__section">
            <h2>Saved</h2>
            <BookGrid books={saved} emptyMessage="You haven't saved any books yet." />
          </div>

          <div className="list-page__section">
            <h2>Completed</h2>
            <BookGrid books={completed} emptyMessage="No completed books yet." />
          </div>
        </>
      )}
    </div>
  );
}

export default Library;
