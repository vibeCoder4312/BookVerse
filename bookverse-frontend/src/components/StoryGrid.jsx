import StoryCard from "./StoryCard";
import StoryCardSkeleton from "./StoryCardSkeleton";
import "./StoryGrid.css";

function StoryGrid({ stories, loading = false, emptyMessage = "Nothing here yet.", skeletonCount = 8 }) {
  if (loading) {
    return (
      <div className="story-grid">
        {Array.from({ length: skeletonCount }).map((_, i) => (
          <StoryCardSkeleton key={i} />
        ))}
      </div>
    );
  }

  if (stories.length === 0) {
    return <p className="story-grid__empty">{emptyMessage}</p>;
  }

  return (
    <div className="story-grid">
      {stories.map((story) => (
        <StoryCard key={story.id} story={story} />
      ))}
    </div>
  );
}

export default StoryGrid;
