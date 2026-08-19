import { Link, useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import "./Navbar.css";

function Navbar() {
  const { user, isAuthenticated, logout } = useAuth();
  const navigate = useNavigate();

  async function handleLogout() {
    await logout();
    navigate("/");
  }

  return (
    <header className="navbar">
      <div className="navbar__inner page-container">
        <Link to="/" className="navbar__logo">
          Book<span>Verse</span>
        </Link>

        <nav className="navbar__links">
          <Link to="/">Home</Link>
          <Link to="/explore">Explore</Link>
          <Link to="/study">Study</Link>
          <Link to="/manga">Manga & Comics</Link>
        </nav>

        <div className="navbar__actions">
          <input className="navbar__search" type="text" placeholder="Search titles, authors..." />

          {/* CONDITIONAL RENDERING based on auth state - this is the
              whole reason we built AuthContext: this component doesn't
              fetch or manage login state itself, it just reacts to it. */}
          {isAuthenticated ? (
            <div className="navbar__user">
              <Link to="/library" className="navbar__quicklink">Library</Link>
              <Link to="/favorites" className="navbar__quicklink">Favorites</Link>
              <Link to="/profile" className="navbar__username">{user.name}</Link>
              <button className="navbar__logout" onClick={handleLogout}>Logout</button>
            </div>
          ) : (
            <Link to="/login" className="navbar__login">Login</Link>
          )}
        </div>
      </div>
    </header>
  );
}

export default Navbar;
