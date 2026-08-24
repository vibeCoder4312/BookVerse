import { useState, useEffect } from "react";
import { fetchHistory } from "../services/libraryService";
import { mapApiBook } from "../utils/mapBook";
import BookGrid from "../components/BookGrid";
import "./ListPage.css";

function History() {
  const [books, setBooks] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchHistory()
      .then((data) => setBooks(data.map((entry) => mapApiBook(entry.book))))
      .catch((err) => console.error(err))
      .finally(() => setLoading(false));
  }, []);

  return (
    <div className="page-container list-page">
      <h1>Reading History</h1>
      <BookGrid books={books} loading={loading} emptyMessage="You haven't opened any books yet." />
    </div>
  );
}

export default History;
