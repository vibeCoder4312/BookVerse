import { Navigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";

// A wrapper component - instead of putting login-checking logic inside
// every single protected page, pages just get wrapped with this once
// in App.jsx: <ProtectedRoute><Profile /></ProtectedRoute>
function ProtectedRoute({ children }) {
  const { isAuthenticated } = useAuth();

  // <Navigate> is React Router's way of redirecting programmatically -
  // "replace" means it swaps the current history entry instead of
  // adding a new one, so the Back button doesn't bounce to the
  // protected page you were just blocked from.
  if (!isAuthenticated) {
    return <Navigate to="/login" replace />;
  }

  return children;
}

export default ProtectedRoute;
