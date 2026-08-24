import api from "./api";

export function fetchProgress(bookId) {
  return api.get(`/progress/${bookId}`).then((res) => res.data);
}

export function updateProgress(bookId, progress) {
  return api.put(`/progress/${bookId}`, null, { params: { progress } }).then((res) => res.data);
}

export function fetchContinueReading() {
  return api.get("/progress/continue-reading").then((res) => res.data);
}
