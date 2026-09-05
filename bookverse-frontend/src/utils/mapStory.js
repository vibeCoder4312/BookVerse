// A distinct palette from the book domain's spine colors, so story cards
// feel like their own visual family within the same overall design system -
// slightly warmer/moodier, evoking audio/podcast cover art rather than
// book spines.
const STORY_PALETTE = ["#6f42c1", "#c0392b", "#1a5276", "#8e44ad", "#b8860b", "#2c3e50"];

function colorFor(seed) {
  let hash = 0;
  for (let i = 0; i < seed.length; i++) {
    hash = seed.charCodeAt(i) + ((hash << 5) - hash);
  }
  return STORY_PALETTE[Math.abs(hash) % STORY_PALETTE.length];
}

// Converts seconds into a short display label - "32 min" under an hour,
// "1h 12m" once it crosses that boundary. Handles null gracefully (a
// brand new story with no episodes yet has no duration to show).
export function formatDuration(totalSeconds) {
  if (!totalSeconds || totalSeconds <= 0) return null;
  const totalMinutes = Math.round(totalSeconds / 60);
  if (totalMinutes < 60) return `${totalMinutes} min`;
  const hours = Math.floor(totalMinutes / 60);
  const minutes = totalMinutes % 60;
  return minutes > 0 ? `${hours}h ${minutes}m` : `${hours}h`;
}

const LANGUAGE_FLAG = { ENGLISH: "🇬🇧", HINDI: "🇮🇳" };

export function mapApiStory(apiStory) {
  return {
    id: apiStory.id,
    title: apiStory.title,
    shortDescription: apiStory.shortDescription,
    description: apiStory.description,
    coverImage: apiStory.coverImage,
    author: apiStory.author,
    narrator: apiStory.narrator,
    language: apiStory.language,
    languageFlag: LANGUAGE_FLAG[apiStory.language] ?? "",
    categoryName: apiStory.category?.name ?? "Story",
    isPremium: Boolean(apiStory.isPremium),
    durationLabel: formatDuration(apiStory.totalDurationSeconds),
    episodeCount: apiStory.totalEpisodes ?? 0,
    playCount: apiStory.playCount ?? 0,
    coverColor: colorFor(apiStory.category?.name ?? apiStory.title),
    // Only present on the detail-response shape, undefined on cards -
    // components should treat undefined the same as "no episodes loaded".
    episodes: apiStory.episodes,
  };
}
