import { useState, useEffect } from "react";
import { useParams } from "react-router-dom";
import { fetchStoriesByCategory } from "../services/storyService";
import { mapApiStory } from "../utils/mapStory";
import StoryGrid from "../components/StoryGrid";
import "./ListenStoriesHome.css";

function StoryCategoryPage() {
  const { categoryName } = useParams();
  const [stories, setStories] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    async function load() {
      try {
        setLoading(true);
        setError(null);
        const data = await fetchStoriesByCategory(categoryName, { size: 24 });
        setStories(data.content.map(mapApiStory));
      } catch (err) {
        console.error(err);
        setError("Couldn't load this category. Is the backend running on port 8080?");
      } finally {
        setLoading(false);
      }
    }
    load();
  }, [categoryName]);

  return (
    <div className="page-container" style={{ paddingTop: "var(--space-6)", paddingBottom: "var(--space-8)" }}>
      <h1 style={{ marginBottom: "var(--space-5)" }}>{categoryName}</h1>

      {error && <p className="stories-home__status stories-home__status--error">{error}</p>}
      {!error && (
        <StoryGrid stories={stories} loading={loading} emptyMessage={`No stories in ${categoryName} yet.`} />
      )}
    </div>
  );
}

export default StoryCategoryPage;
