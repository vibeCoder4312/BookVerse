import api from "./api";

export function registerUser(payload) {
  return api.post("/auth/register", payload).then((res) => res.data);
}

export function loginUser(payload) {
  return api.post("/auth/login", payload).then((res) => res.data);
}

export function refreshAccessToken(refreshToken) {
  return api.post("/auth/refresh", { refreshToken }).then((res) => res.data);
}

export function logoutUser(refreshToken) {
  return api.post("/auth/logout", { refreshToken });
}
