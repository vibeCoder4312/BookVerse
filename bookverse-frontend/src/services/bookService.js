import api from "./api";

// Grouping all book-related API calls in one file. Pages import THESE
// functions instead of calling axios directly - if an endpoint URL ever
// changes, we fix it in exactly one place.

export function fetchBooks(params = {}) {
  // axios turns the params object into a query string automatically,
  // e.g. { page: 0, size: 6 } becomes ?page=0&size=6
  return api.get("/books", { params }).then((res) => res.data);
}

export function fetchBookById(id) {
  return api.get(`/books/${id}`).then((res) => res.data);
}

export function fetchBooksByCategory(categoryName, params = {}) {
  return api.get(`/books/category/${categoryName}`, { params }).then((res) => res.data);
}

export function searchBooks(keyword, params = {}) {
  return api.get("/books/search", { params: { keyword, ...params } }).then((res) => res.data);
}
