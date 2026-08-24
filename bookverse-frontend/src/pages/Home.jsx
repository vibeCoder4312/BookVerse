import { useState, useEffect } from "react";
import { fetchBooks, fetchBooksByCategory } from "../services/bookService";
import { mapApiBook } from "../utils/mapBook";
import GenreShelf from "../components/GenreShelf";
import "./Home.css";

function Home() {
  const [sections, setSections] = useState({ featured: [], fantasy: [], adventure: [], programming: [] });
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    async function loadHomeData() {
      try {
        setLoading(true);
        setError(null);

        const [featuredPage, fantasyPage, adventurePage, programmingPage] = await Promise.all([
          fetchBooks({ page: 0, size: 6 }),
          fetchBooksByCategory("Fantasy", { size: 6 }),
          fetchBooksByCategory("Adventure", { size: 6 }),
          fetchBooksByCategory("Programming", { size: 6 }),
        ]);

        setSections({
          featured: featuredPage.content.map(mapApiBook),
          fantasy: fantasyPage.content.map(mapApiBook),
          adventure: adventurePage.content.map(mapApiBook),
          programming: programmingPage.content.map(mapApiBook),
        });
      } catch (err) {
        console.error(err);
        setError("Couldn't load books. Is the backend running on port 8080?");
      } finally {
        setLoading(false);
      }
    }

    loadHomeData();
  }, []);

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
        {error && <p className="home__status home__status--error">{error}</p>}

        {!error && (
          <>
            {/* Each shelf gets its OWN loading skeleton now, instead of
                the whole page hiding behind one "Loading books..." message -
                the layout appears instantly, content fills in as it arrives. */}
            <GenreShelf title="Featured" books={sections.featured} loading={loading} />
            <GenreShelf title="Fantasy" books={sections.fantasy} loading={loading} />
            <GenreShelf title="Adventure" books={sections.adventure} loading={loading} />
            <GenreShelf title="Programming" books={sections.programming} loading={loading} />
          </>
        )}
      </div>
    </div>
  );
}

export default Home;
