import "./StoryCardSkeleton.css";

function StoryCardSkeleton() {
  return (
    <div className="story-card-skeleton">
      <div className="story-card-skeleton__cover" />
      <div className="story-card-skeleton__line story-card-skeleton__line--title" />
      <div className="story-card-skeleton__line story-card-skeleton__line--desc" />
    </div>
  );
}

export default StoryCardSkeleton;
