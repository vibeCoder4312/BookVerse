import { useState, useEffect } from "react";
import { useSearchParams } from "react-router-dom";
import { searchStories } from "../services/storyService";
import { mapApiStory } from "../utils/mapStory";
import StoryGrid from "../components/StoryGrid";
import "./ListenStoriesHome.css";

function StorySearch() {
  const [searchParams] = useSearchParams();
  const keyword = searchParams.get("q") || "";

  const [stories, setStories] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    if (!keyword.trim()) {
      setStories([]);
      setLoading(false);
      return;
    }

    async function runSearch() {
      try {
        setLoading(true);
        setError(null);
        const data = await searchStories(keyword, { size: 24 });
        setStories(data.content.map(mapApiStory));
      } catch (err) {
        console.error(err);
        setError("Search failed. Is the backend running on port 8080?");
      } finally {
        setLoading(false);
      }
    }
    runSearch();
  }, [keyword]);

  return (
    <div className="page-container" style={{ paddingTop: "var(--space-6)", paddingBottom: "var(--space-8)" }}>
      <h1 style={{ marginBottom: "var(--space-5)" }}>
        {keyword ? `Results for "${keyword}"` : "Search Stories"}
      </h1>

      {!keyword.trim() && <p className="stories-home__status">Type something in the search bar to get started.</p>}
      {error && <p className="stories-home__status stories-home__status--error">{error}</p>}

      {keyword.trim() && !error && (
        <StoryGrid stories={stories} loading={loading} emptyMessage={`No stories found for "${keyword}".`} />
      )}
    </div>
  );
}

export default StorySearch;
