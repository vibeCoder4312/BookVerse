import { useState, useEffect } from "react";
import { useSearchParams } from "react-router-dom";
import { searchBooks } from "../services/bookService";
import { mapApiBook } from "../utils/mapBook";
import BookGrid from "../components/BookGrid";
import Pagination from "../components/Pagination";
import "./Explore.css";

const PAGE_SIZE = 24;

function Search() {
  // useSearchParams reads/writes the URL's query string (?q=...) - this
  // means the search itself is bookmarkable/shareable/back-button-friendly,
  // since the search term lives in the URL, not just in component state.
  const [searchParams] = useSearchParams();
  const keyword = searchParams.get("q") || "";

  const [books, setBooks] = useState([]);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  // Re-run whenever the keyword OR page changes. Resetting to page 0
  // when the keyword changes (see handleSearchChange effect below) avoids
  // landing on "page 5" of a brand new, much shorter result set.
  useEffect(() => {
    if (!keyword.trim()) {
      setBooks([]);
      setTotalPages(0);
      setLoading(false);
      return;
    }

    async function runSearch() {
      try {
        setLoading(true);
        setError(null);
        const data = await searchBooks(keyword, { page, size: PAGE_SIZE });
        setBooks(data.content.map(mapApiBook));
        setTotalPages(data.totalPages);
      } catch (err) {
        console.error(err);
        setError("Search failed. Is the backend running on port 8080?");
      } finally {
        setLoading(false);
      }
    }
    runSearch();
  }, [keyword, page]);

  // Whenever the search term itself changes (new navbar search), jump
  // back to page 0 - otherwise a stale page number could show "no results"
  // even when the new search has plenty.
  useEffect(() => {
    setPage(0);
  }, [keyword]);

  return (
    <div className="page-container explore">
      <h1>{keyword ? `Search results for "${keyword}"` : "Search"}</h1>

      {!keyword.trim() && <p className="explore__empty">Type something in the search bar above to get started.</p>}

      {error && <p className="explore__empty">{error}</p>}

      {keyword.trim() && !error && (
        <>
          <BookGrid
            books={books}
            loading={loading}
            skeletonCount={8}
            emptyMessage={`No results found for "${keyword}".`}
          />
          <Pagination page={page} totalPages={totalPages} onPageChange={setPage} />
        </>
      )}
    </div>
  );
}

export default Search;
