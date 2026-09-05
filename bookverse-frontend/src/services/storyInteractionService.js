import api from "./api";

export function fetchStoryFavorites() {
  return api.get("/story-favorites").then((res) => res.data);
}
export function addStoryFavorite(storyId) {
  return api.post(`/story-favorites/${storyId}`);
}
export function removeStoryFavorite(storyId) {
  return api.delete(`/story-favorites/${storyId}`);
}
