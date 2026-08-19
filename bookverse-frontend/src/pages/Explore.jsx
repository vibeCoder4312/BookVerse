import { useState, useEffect } from "react";
import { fetchBooks } from "../services/bookService";
import { mapApiBook } from "../utils/mapBook";
import BookGrid from "../components/BookGrid";
import "./Explore.css";

function Explore() {
  const [selectedType, setSelectedType] = useState("ALL");
  const [books, setBooks] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  // We fetch once on mount (empty dependency array) and filter by type
  // client-side, same approach as Phase 5 - just with real data now.
  // A backend content-type filter param can replace this later if the
  // book count grows large enough that client-side filtering gets slow.
  useEffect(() => {
    async function loadBooks() {
      try {
        setLoading(true);
        setError(null);
        const page = await fetchBooks({ page: 0, size: 50 });
        setBooks(page.content.map(mapApiBook));
      } catch (err) {
        console.error(err);
        setError("Couldn't load books. Is the backend running on port 8080?");
      } finally {
        setLoading(false);
      }
    }
    loadBooks();
  }, []);

  const filteredBooks =
    selectedType === "ALL" ? books : books.filter((book) => book.contentType === selectedType);

  return (
    <div className="page-container explore">
      <h1>Explore</h1>

      <div className="explore__filters">
        <label htmlFor="type-filter">Content Type</label>
        <select
          id="type-filter"
          value={selectedType}
          onChange={(event) => setSelectedType(event.target.value)}
        >
          <option value="ALL">All</option>
          <option value="NOVEL">Novel</option>
          <option value="BOOK">Book</option>
          <option value="MANGA">Manga</option>
          <option value="MANHWA">Manhwa</option>
          <option value="STUDY_BOOK">Study Book</option>
        </select>
      </div>

      {loading && <p className="explore__empty">Loading books...</p>}
      {error && <p className="explore__empty">{error}</p>}

      {!loading && !error && (
        <BookGrid books={filteredBooks} emptyMessage="No books match this filter." />
      )}
    </div>
  );
}

export default Explore;
