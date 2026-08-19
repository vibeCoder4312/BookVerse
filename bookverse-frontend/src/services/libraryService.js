import api from "./api";

// --- Favorites ---
export function fetchFavorites() {
  return api.get("/favorites").then((res) => res.data);
}
export function addFavorite(bookId) {
  return api.post(`/favorites/${bookId}`);
}
export function removeFavorite(bookId) {
  return api.delete(`/favorites/${bookId}`);
}

// --- Library ---
export function fetchLibrary() {
  return api.get("/library").then((res) => res.data);
}
export function addOrUpdateLibraryEntry(bookId, status = "SAVED") {
  return api.post(`/library/${bookId}`, null, { params: { status } });
}
export function removeLibraryEntry(bookId) {
  return api.delete(`/library/${bookId}`);
}

// --- Reading History ---
export function fetchHistory() {
  return api.get("/history").then((res) => res.data);
}
export function recordHistoryView(bookId) {
  return api.post(`/history/${bookId}`);
}

