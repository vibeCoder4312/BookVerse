import { useState, useEffect } from "react";
import { fetchBooks } from "../services/bookService";
import { mapApiBook } from "../utils/mapBook";
import BookGrid from "../components/BookGrid";
import Pagination from "../components/Pagination";
import "./Explore.css";

const PAGE_SIZE = 24;

function Explore() {
  const [selectedType, setSelectedType] = useState("ALL");
  const [books, setBooks] = useState([]);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  // Re-fetch whenever `page` changes - this is real server-side
  // pagination now: with 500+ books, we only ever hold ONE page's worth
  // (24 books) in memory at a time, exactly like the spec requires.
  //
  // NOTE: contentType filtering here is a known simplification - it only
  // filters WITHIN the current page's 24 results, not across the whole
  // catalog. A fully correct version would pass contentType as a backend
  // query param; left as a follow-up since this phase's focus is UI polish.
  useEffect(() => {
    async function loadBooks() {
      try {
        setLoading(true);
        setError(null);
        const data = await fetchBooks({ page, size: PAGE_SIZE });
        setBooks(data.content.map(mapApiBook));
        setTotalPages(data.totalPages);
      } catch (err) {
        console.error(err);
        setError("Couldn't load books. Is the backend running on port 8080?");
      } finally {
        setLoading(false);
      }
    }
    loadBooks();
  }, [page]);

  function handleTypeChange(event) {
    setSelectedType(event.target.value);
  }

  function handlePageChange(newPage) {
    setPage(newPage);
    window.scrollTo({ top: 0, behavior: "smooth" });
  }

  const filteredBooks =
    selectedType === "ALL" ? books : books.filter((book) => book.contentType === selectedType);

  return (
    <div className="page-container explore">
      <h1>Explore</h1>

      <div className="explore__filters">
        <label htmlFor="type-filter">Content Type</label>
        <select id="type-filter" value={selectedType} onChange={handleTypeChange}>
          <option value="ALL">All</option>
          <option value="NOVEL">Novel</option>
          <option value="BOOK">Book</option>
          <option value="MANGA">Manga</option>
          <option value="MANHWA">Manhwa</option>
          <option value="MANHUA">Manhua</option>
          <option value="COMIC">Comic</option>
          <option value="GRAPHIC_NOVEL">Graphic Novel</option>
          <option value="LIGHT_NOVEL">Light Novel</option>
          <option value="STUDY_BOOK">Study Book</option>
          <option value="CHILDRENS_BOOK">Children's Book</option>
        </select>
      </div>

      {error && <p className="explore__empty">{error}</p>}

      {!error && (
        <>
          <BookGrid
            books={filteredBooks}
            loading={loading}
            skeletonCount={PAGE_SIZE}
            emptyMessage="No books match this filter on this page - try Next page or a different filter."
          />
          <Pagination page={page} totalPages={totalPages} onPageChange={handlePageChange} />
        </>
      )}
    </div>
  );
}

export default Explore;
