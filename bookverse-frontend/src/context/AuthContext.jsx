import { createContext, useContext, useState, useEffect } from "react";
import { loginUser, registerUser, logoutUser } from "../services/authService";

// createContext() makes a "channel" that any component, anywhere in the
// tree, can tune into with useContext() - without props being passed
// down manually through every level in between.
const AuthContext = createContext(null);

const TOKEN_KEY = "bookverse_access_token";
const REFRESH_KEY = "bookverse_refresh_token";
const USER_KEY = "bookverse_user";

// This component WRAPS the whole app (we'll do that in main.jsx). Any
// component inside it can call useAuth() to read the current user or
// call login()/logout().
export function AuthProvider({ children }) {
  const [user, setUser] = useState(null);
  const [initializing, setInitializing] = useState(true);

  // On first load (e.g. user refreshes the page), check if we already
  // have a logged-in session saved in localStorage, and restore it -
  // otherwise every page refresh would silently log the user out.
  useEffect(() => {
    const savedUser = localStorage.getItem(USER_KEY);
    if (savedUser) {
      setUser(JSON.parse(savedUser));
    }
    setInitializing(false);
  }, []);

  function persistSession(authResponse) {
    const loggedInUser = {
      id: authResponse.userId,
      name: authResponse.name,
      email: authResponse.email,
      role: authResponse.role,
    };
    localStorage.setItem(TOKEN_KEY, authResponse.accessToken);
    localStorage.setItem(REFRESH_KEY, authResponse.refreshToken);
    localStorage.setItem(USER_KEY, JSON.stringify(loggedInUser));
    setUser(loggedInUser);
  }

  async function login(email, password) {
    const data = await loginUser({ email, password });
    persistSession(data);
  }

  async function register(name, email, password, confirmPassword) {
    const data = await registerUser({ name, email, password, confirmPassword });
    persistSession(data); // registering also logs the user straight in
  }

  async function logout() {
    const refreshToken = localStorage.getItem(REFRESH_KEY);
    try {
      if (refreshToken) await logoutUser(refreshToken);
    } catch (err) {
      // Even if the server call fails (e.g. token already expired),
      // we still want to clear the LOCAL session below - the user
      // should always be able to log out on their own device.
      console.warn("Logout request failed, clearing local session anyway.", err);
    }
    localStorage.removeItem(TOKEN_KEY);
    localStorage.removeItem(REFRESH_KEY);
    localStorage.removeItem(USER_KEY);
    setUser(null);
  }

  const value = { user, login, register, logout, isAuthenticated: !!user };

  // Don't render children until we've checked localStorage - otherwise
  // the UI might flash "logged out" for a split second even for a
  // returning logged-in user.
  if (initializing) return null;

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

// A small custom hook - components call useAuth() instead of writing
// useContext(AuthContext) everywhere, and get a helpful error if
// someone forgets to wrap the app in <AuthProvider>.
export function useAuth() {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error("useAuth must be used within an AuthProvider");
  }
  return context;
}
