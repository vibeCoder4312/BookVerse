import StoryCard from "./StoryCard";
import StoryCardSkeleton from "./StoryCardSkeleton";
import "./StoryShelf.css";

function StoryShelf({ title, icon, stories, loading = false }) {
  return (
    <section className="story-shelf">
      <div className="story-shelf__header">
        <h2>{icon} {title}</h2>
      </div>

      {loading ? (
        <div className="story-shelf__row">
          {Array.from({ length: 6 }).map((_, i) => (
            <StoryCardSkeleton key={i} />
          ))}
        </div>
      ) : stories.length === 0 ? (
        <p className="story-shelf__empty">Nothing here yet - check back soon.</p>
      ) : (
        <div className="story-shelf__row">
          {stories.map((story) => (
            <StoryCard key={story.id} story={story} />
          ))}
        </div>
      )}
    </section>
  );
}

export default StoryShelf;
