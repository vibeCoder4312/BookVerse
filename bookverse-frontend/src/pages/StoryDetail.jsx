import { useState, useEffect } from "react";
import { useParams, Link, useNavigate } from "react-router-dom";
import { fetchStoryDetail, fetchSimilarStories } from "../services/storyService";
import { addStoryFavorite, removeStoryFavorite } from "../services/storyInteractionService";
import { useAuth } from "../context/AuthContext";
import { useToast } from "../context/ToastContext";
import { mapApiStory } from "../utils/mapStory";
import StoryShelf from "../components/StoryShelf";
import "./StoryDetail.css";

function StoryDetail() {
  const { id } = useParams();
  const { isAuthenticated } = useAuth();
  const navigate = useNavigate();
  const { success, error: showError } = useToast();

  const [story, setStory] = useState(null);
  const [loading, setLoading] = useState(true);
  const [notFound, setNotFound] = useState(false);
  const [isFavorite, setIsFavorite] = useState(false);
  const [similar, setSimilar] = useState([]);
  const [similarLoading, setSimilarLoading] = useState(true);
  // Which episode's native <audio> element is currently expanded/playing -
  // a deliberately simple mechanism for this phase. Phase 8 replaces this
  // entirely with a persistent custom player (seek, speed, sleep timer,
  // mini-player) - this inline player exists only so audio genuinely
  // plays end-to-end right now, not as the final experience.
  const [playingEpisodeId, setPlayingEpisodeId] = useState(null);

  useEffect(() => {
    async function load() {
      try {
        setLoading(true);
        setNotFound(false);
        const data = await fetchStoryDetail(id);
        setStory(mapApiStory(data));
      } catch (err) {
        if (err.response?.status === 404) {
          setNotFound(true);
        } else {
          console.error(err);
        }
      } finally {
        setLoading(false);
      }
    }
    load();
  }, [id]);

  useEffect(() => {
    async function loadSimilar() {
      try {
        setSimilarLoading(true);
        const data = await fetchSimilarStories(id, 6);
        setSimilar(data.map(mapApiStory));
      } catch (err) {
        console.error("Failed to load similar stories", err);
      } finally {
        setSimilarLoading(false);
      }
    }
    if (!notFound) loadSimilar();
  }, [id, notFound]);

  async function handleFavoriteToggle() {
    if (!isAuthenticated) return navigate("/login");
    const next = !isFavorite;
    setIsFavorite(next);
    try {
      next ? await addStoryFavorite(id) : await removeStoryFavorite(id);
      success(next ? "Added to favorites" : "Removed from favorites");
    } catch (err) {
      console.error(err);
      setIsFavorite(!next);
      showError("Couldn't update favorites - please try again");
    }
  }

  function togglePlay(episodeId) {
    setPlayingEpisodeId((current) => (current === episodeId ? null : episodeId));
  }

  if (loading) {
    return (
      <div className="page-container story-detail__status">
        <p>Loading...</p>
      </div>
    );
  }

  if (notFound || !story) {
    return (
      <div className="page-container story-detail__status">
        <p>We couldn't find that story.</p>
        <Link to="/listen-stories">Back to Listen Stories</Link>
      </div>
    );
  }

  const episodes = story.episodes ?? [];

  return (
    <div className="page-container story-detail">
      <div className="story-detail__layout">
        <div
          className="story-detail__cover"
          style={{ background: `linear-gradient(160deg, ${story.coverColor}, #14162b)` }}
        >
          <span>{story.title}</span>
        </div>

        <div className="story-detail__info">
          <p className="story-detail__type">
            {story.languageFlag} {story.categoryName} {story.isPremium ? "· PREMIUM" : "· FREE"}
          </p>
          <h1>{story.title}</h1>
          <p className="story-detail__author">by {story.author}</p>
          {story.narrator && <p className="story-detail__narrator">🎙 Narrated by {story.narrator}</p>}

          <div className="story-detail__meta">
            {story.durationLabel && <span>⏱ {story.durationLabel}</span>}
            <span>{story.episodeCount} episode{story.episodeCount === 1 ? "" : "s"}</span>
          </div>

          <div className="story-detail__actions">
            {episodes.length > 0 && (
              <button className="btn btn--primary" onClick={() => togglePlay(episodes[0].id)}>
                ▶ Start Listening
              </button>
            )}
            <button className="btn btn--ghost" onClick={handleFavoriteToggle}>
              {isFavorite ? "♥ Favorited" : "♡ Add to Favorites"}
            </button>
          </div>

          {story.description && <p className="story-detail__description">{story.description}</p>}
        </div>
      </div>

      <section className="story-detail__episodes">
        <h2>Episodes</h2>
        {episodes.length === 0 ? (
          <p className="story-detail__empty">No episodes published yet - check back soon.</p>
        ) : (
          <ul className="episode-list">
            {episodes.map((ep) => (
              <li key={ep.id} className="episode-list__item">
                <button
                  className="episode-list__play-btn"
                  onClick={() => togglePlay(ep.id)}
                  disabled={!ep.audioUrl}
                  title={!ep.audioUrl ? "Audio not uploaded yet" : undefined}
                >
                  {playingEpisodeId === ep.id ? "⏸" : "▶"}
                </button>
                <div className="episode-list__info">
                  <span className="episode-list__number">
                    {String(ep.episodeNumber).padStart(2, "0")}
                  </span>
                  <span className="episode-list__title">{ep.title}</span>
                </div>
                {ep.durationSeconds && (
                  <span className="episode-list__duration">
                    {Math.round(ep.durationSeconds / 60)} min
                  </span>
                )}

                {playingEpisodeId === ep.id && ep.audioUrl && (
                  <audio
                    className="episode-list__audio"
                    src={`http://localhost:8080${ep.audioUrl}`}
                    controls
                    autoPlay
                  />
                )}
              </li>
            ))}
          </ul>
        )}
      </section>

      <div className="story-detail__similar">
        <StoryShelf title="You may also like" icon="📻" stories={similar} loading={similarLoading} />
      </div>
    </div>
  );
}

export default StoryDetail;
