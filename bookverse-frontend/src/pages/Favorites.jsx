import { useState, useEffect } from "react";
import { fetchFavorites } from "../services/libraryService";
import { mapApiBook } from "../utils/mapBook";
import BookGrid from "../components/BookGrid";
import "./ListPage.css";

function Favorites() {
  const [books, setBooks] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchFavorites()
      .then((data) => setBooks(data.map(mapApiBook)))
      .catch((err) => console.error(err))
      .finally(() => setLoading(false));
  }, []);

  return (
    <div className="page-container list-page">
      <h1>Favorites</h1>
      <BookGrid
        books={books.map((b) => ({ ...b, favorited: true }))}
        loading={loading}
        emptyMessage="You haven't favorited any books yet."
      />
    </div>
  );
}

export default Favorites;
