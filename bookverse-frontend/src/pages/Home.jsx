import { useState, useEffect } from "react";
import { fetchBooks, fetchBooksByCategory } from "../services/bookService";
import { fetchFavorites } from "../services/libraryService.js";
import { mapApiBook } from "../utils/mapBook";
import { useAuth } from "../context/AuthContext";
import GenreShelf from "../components/GenreShelf";
import "./Home.css";

function Home() {
  // Three pieces of state: the actual data, whether we're still loading
  // it, and whether something went wrong. This trio is the standard
  // pattern for ANY component that fetches data.
  const [sections, setSections] = useState({ featured: [], fantasy: [], adventure: [], programming: [] });
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [favoritedIds, setFavoritedIds] = useState(new Set());
  const { isAuthenticated } = useAuth();

  // useEffect(fn, [dependencies]) runs `fn` after this component renders.
  // An EMPTY dependency array [] means "run this only once, right after
  // the first render" - exactly what we want for an initial data fetch.
  // If we forgot the [] entirely, this would re-run after every single
  // render, causing an infinite loop of fetches.
  useEffect(() => {
    async function loadHomeData() {
      try {
        setLoading(true);
        setError(null);

        // Promise.all runs all four requests in parallel instead of
        // waiting for each one to finish before starting the next -
        // much faster than awaiting them one by one.
        const [featuredPage, fantasyPage, adventurePage, programmingPage] = await Promise.all([
          fetchBooks({ page: 0, size: 6 }),
          fetchBooksByCategory("Fantasy", { size: 6 }),
          fetchBooksByCategory("Adventure", { size: 6 }),
          fetchBooksByCategory("Programming", { size: 6 }),
        ]);

        // Every paginated response has the real books inside a
        // "content" array, alongside metadata like totalPages.
        setSections({
          featured: featuredPage.content.map(mapApiBook),
          fantasy: fantasyPage.content.map(mapApiBook),
          adventure: adventurePage.content.map(mapApiBook),
          programming: programmingPage.content.map(mapApiBook),
        });

        // Logged-in users: find out which books are already favorited,
        // so hearts render filled-in correctly instead of always
        // starting unfavorited.
        if (isAuthenticated) {
          try {
            const favorites = await fetchFavorites();
            const ids = new Set(favorites.map((f) => String(f.bookId ?? f.id ?? f.book?.id)));
            setFavoritedIds(ids);
          } catch (err) {
            console.error("Failed to load favorites", err);
          }
        }
      } catch (err) {
        console.error(err);
        setError("Couldn't load books. Is the backend running on port 8080?");
      } finally {
        setLoading(false);
      }
    }

    loadHomeData();
  }, [isAuthenticated]);

  return (
    <div>
      <section className="hero">
        <div className="page-container hero__inner">
          <h1>Every story, one shelf away.</h1>
          <p className="hero__sub">
            Books, manga, comics and study material - all in one place.
          </p>
          <input className="hero__search" type="text" placeholder="Search titles, authors, genres..." />
        </div>
      </section>

      <div className="page-container">
        {/* Three-way conditional: loading, error, or actual content.
            Only one of these branches ever renders at a time. */}
        {loading && <p className="home__status">Loading books...</p>}

        {error && <p className="home__status home__status--error">{error}</p>}

        {!loading && !error && (
          <>
            <GenreShelf title="Featured" books={sections.featured} favoritedIds={favoritedIds} />
            <GenreShelf title="Fantasy" books={sections.fantasy} favoritedIds={favoritedIds} />
            <GenreShelf title="Adventure" books={sections.adventure} favoritedIds={favoritedIds} />
            <GenreShelf title="Programming" books={sections.programming} favoritedIds={favoritedIds} />
          </>
        )}
      </div>
    </div>
  );
}

export default Home;