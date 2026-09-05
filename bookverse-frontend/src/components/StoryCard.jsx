import { Link } from "react-router-dom";
import "./StoryCard.css";

// Play/favorite interactivity comes in later phases (8 and 9) - for now
// this card is a rich, informative link to the story's future detail page.
// Note the /listen-stories/:id route it links to doesn't exist until
// Phase 7 - that's expected at this point in the build, not a bug.
function StoryCard({ story }) {
  return (
    <Link to={`/listen-stories/${story.id}`} className="story-card">
      <div
        className="story-card__cover"
        style={{ background: `linear-gradient(155deg, ${story.coverColor}, #14162b)` }}
      >
        {story.isPremium && <span className="story-card__badge story-card__badge--premium">PREMIUM</span>}
        {!story.isPremium && <span className="story-card__badge story-card__badge--free">FREE</span>}

        <button className="story-card__play" aria-label={`Play ${story.title}`} onClick={(e) => e.preventDefault()}>
          ▶
        </button>

        <span className="story-card__cover-title">{story.title}</span>
      </div>

      <div className="story-card__body">
        <p className="story-card__category">
          {story.languageFlag} {story.categoryName}
        </p>
        <h3 className="story-card__title">{story.title}</h3>
        {story.shortDescription && <p className="story-card__desc">{story.shortDescription}</p>}

        <div className="story-card__meta">
          {story.narrator && <span className="story-card__narrator">🎙 {story.narrator}</span>}
          {story.durationLabel && <span className="story-card__duration">⏱ {story.durationLabel}</span>}
        </div>
      </div>
    </Link>
  );
}

export default StoryCard;
