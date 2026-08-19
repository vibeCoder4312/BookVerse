import axios from "axios";

const api = axios.create({
  baseURL: "http://localhost:8080/api",
});

// An axios INTERCEPTOR runs before every single request this instance
// sends. Here, we read the access token from localStorage (if the user
// is logged in) and attach it as "Authorization: Bearer <token>" -
// exactly what our JwtAuthFilter on the backend expects to find.
// This means individual components never have to remember to attach
// the token themselves - it happens automatically, everywhere.
api.interceptors.request.use((config) => {
  const token = localStorage.getItem("bookverse_access_token");
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

export default api;
