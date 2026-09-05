import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import {
  fetchTrendingStories,
  fetchNewStories,
  fetchFreeStories,
  fetchStoriesByCategory,
  fetchStoryCategories,
} from "../services/storyService";
import { mapApiStory } from "../utils/mapStory";
import StoryShelf from "../components/StoryShelf";
import "./ListenStoriesHome.css";

function ListenStoriesHome() {
  const navigate = useNavigate();
  const [searchValue, setSearchValue] = useState("");

  const [categories, setCategories] = useState([]);
  const [trending, setTrending] = useState([]);
  const [newStories, setNewStories] = useState([]);
  const [free, setFree] = useState([]);
  // Category showcase shelves - keyed by category name, filled in after
  // we know which categories actually exist (avoids hardcoding names
  // that might not have been created by an admin yet).
  const [categoryShelves, setCategoryShelves] = useState({});

  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    async function loadHomeData() {
      try {
        setLoading(true);
        setError(null);

        const [trendingData, newData, freeData, categoriesData] = await Promise.all([
          fetchTrendingStories(8),
          fetchNewStories(8),
          fetchFreeStories({ size: 8 }),
          fetchStoryCategories(),
        ]);

        setTrending(trendingData.map(mapApiStory));
        setNewStories(newData.map(mapApiStory));
        setFree(freeData.content.map(mapApiStory));
        setCategories(categoriesData);

        // Showcase up to 3 categories on the homepage - showing every
        // category that exists would make the page unboundedly long as
        // an admin adds more; the full set is still browsable via the
        // category chips below.
        const showcaseCategories = categoriesData.slice(0, 3);
        const shelfResults = await Promise.all(
          showcaseCategories.map((cat) => fetchStoriesByCategory(cat.name, { size: 8 }))
        );

        const shelves = {};
        showcaseCategories.forEach((cat, i) => {
          shelves[cat.name] = shelfResults[i].content.map(mapApiStory);
        });
        setCategoryShelves(shelves);
      } catch (err) {
        console.error(err);
        setError("Couldn't load stories. Is the backend running on port 8080?");
      } finally {
        setLoading(false);
      }
    }
    loadHomeData();
  }, []);

  function handleSearchSubmit(event) {
    event.preventDefault();
    const trimmed = searchValue.trim();
    if (!trimmed) return;
    navigate(`/listen-stories/search?q=${encodeURIComponent(trimmed)}`);
  }

  function scrollToShelves() {
    document.getElementById("story-shelves")?.scrollIntoView({ behavior: "smooth" });
  }

  return (
    <div>
      {/* Cinematic hero - entirely CSS-drawn (layered radial gradients +
          an "equalizer bars" motif), deliberately avoiding any external
          image asset so there's zero copyright concern here. */}
      <section className="stories-hero">
        <div className="stories-hero__glow" />
        <div className="page-container stories-hero__inner">
          <p className="stories-hero__kicker">🎧 BOOKVERSE LISTEN STORIES</p>
          <h1>Stories That Sound Alive.</h1>
          <p className="stories-hero__sub">Listen to stories you'll remember.</p>

          <form className="stories-hero__search" onSubmit={handleSearchSubmit}>
            <input
              type="search"
              placeholder="Search stories, narrators, categories..."
              value={searchValue}
              onChange={(e) => setSearchValue(e.target.value)}
            />
            <button type="submit">Search</button>
          </form>

          <button className="stories-hero__cta" onClick={scrollToShelves}>
            🎧 Start Listening
          </button>
        </div>

        <div className="stories-hero__bars" aria-hidden="true">
          {Array.from({ length: 24 }).map((_, i) => (
            <span key={i} style={{ animationDelay: `${(i % 8) * 0.12}s` }} />
          ))}
        </div>
      </section>

      {/* Category chips - browse-by-category, satisfies the spec's
          "Categories" homepage requirement without needing a shelf per
          category (25+ story categories would make for an endless page). */}
      {categories.length > 0 && (
        <div className="page-container stories-categories">
          {categories.map((cat) => (
            <button
              key={cat.id}
              className="stories-categories__chip"
              onClick={() => navigate(`/listen-stories/category/${encodeURIComponent(cat.name)}`)}
            >
              {cat.name}
            </button>
          ))}
        </div>
      )}

      <div id="story-shelves" className="page-container">
        {error && <p className="stories-home__status stories-home__status--error">{error}</p>}

        {!error && (
          <>
            <StoryShelf title="Trending Stories" icon="🔥" stories={trending} loading={loading} />
            <StoryShelf title="New Stories" icon="🆕" stories={newStories} loading={loading} />
            <StoryShelf title="Free to Listen" icon="🎁" stories={free} loading={loading} />
            {Object.entries(categoryShelves).map(([categoryName, stories]) => (
              <StoryShelf key={categoryName} title={categoryName} icon="📻" stories={stories} loading={loading} />
            ))}
          </>
        )}
      </div>
    </div>
  );
}

export default ListenStoriesHome;
