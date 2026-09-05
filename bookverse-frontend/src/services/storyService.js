import api from "./api";

export function fetchStories(params = {}) {
  return api.get("/stories", { params }).then((res) => res.data);
}

export function fetchTrendingStories(size = 10) {
  return api.get("/stories/trending", { params: { size } }).then((res) => res.data);
}

export function fetchNewStories(size = 10) {
  return api.get("/stories/new", { params: { size } }).then((res) => res.data);
}

export function fetchFreeStories(params = {}) {
  return api.get("/stories/free", { params }).then((res) => res.data);
}

export function fetchStoriesByCategory(categoryName, params = {}) {
  return api.get(`/stories/category/${encodeURIComponent(categoryName)}`, { params }).then((res) => res.data);
}

export function fetchStoriesByLanguage(language, params = {}) {
  return api.get(`/stories/language/${language}`, { params }).then((res) => res.data);
}

export function searchStories(keyword, params = {}) {
  return api.get("/stories/search", { params: { keyword, ...params } }).then((res) => res.data);
}

export function fetchStoryDetail(id) {
  return api.get(`/stories/${id}`).then((res) => res.data);
}

export function fetchStoryEpisodes(id) {
  return api.get(`/stories/${id}/episodes`).then((res) => res.data);
}

export function fetchSimilarStories(id, size = 6) {
  return api.get(`/stories/${id}/similar`, { params: { size } }).then((res) => res.data);
}

export function fetchStoryCategories() {
  return api.get("/story-categories").then((res) => res.data);
}
