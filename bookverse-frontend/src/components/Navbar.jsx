import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import "./Navbar.css";

function Navbar() {
  const { user, isAuthenticated, logout } = useAuth();
  const navigate = useNavigate();

  const [searchValue, setSearchValue] = useState("");
  const [mobileMenuOpen, setMobileMenuOpen] = useState(false);

  async function handleLogout() {
    await logout();
    setMobileMenuOpen(false);
    navigate("/");
  }

  function handleSearchSubmit(event) {
    event.preventDefault();
    const trimmed = searchValue.trim();
    if (!trimmed) return;
    navigate(`/search?q=${encodeURIComponent(trimmed)}`);
    setMobileMenuOpen(false);
  }

  function closeMobileMenu() {
    setMobileMenuOpen(false);
  }

  return (
    <header className="navbar">
      <div className="navbar__inner page-container">
        <Link to="/" className="navbar__logo" onClick={closeMobileMenu}>
          Book<span>Verse</span>
        </Link>

        <nav className="navbar__links">
          <Link to="/">Home</Link>
          <Link to="/explore">Explore</Link>
          <Link to="/study">Study</Link>
          <Link to="/manga">Manga & Comics</Link>
          <Link to="/listen-stories">🎧 Listen Stories</Link>
        </nav>

        <div className="navbar__actions">
          <form className="navbar__search-form" onSubmit={handleSearchSubmit}>
            <input
              className="navbar__search"
              type="search"
              placeholder="Search titles, authors..."
              value={searchValue}
              onChange={(e) => setSearchValue(e.target.value)}
            />
          </form>

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

          <button
            className="navbar__hamburger"
            onClick={() => setMobileMenuOpen((open) => !open)}
            aria-label="Toggle menu"
            aria-expanded={mobileMenuOpen}
          >
            <span />
            <span />
            <span />
          </button>
        </div>
      </div>

      {mobileMenuOpen && (
        <div className="navbar__mobile-menu">
          <form className="navbar__search-form navbar__search-form--mobile" onSubmit={handleSearchSubmit}>
            <input
              className="navbar__search"
              type="search"
              placeholder="Search titles, authors..."
              value={searchValue}
              onChange={(e) => setSearchValue(e.target.value)}
            />
          </form>
          <Link to="/" onClick={closeMobileMenu}>Home</Link>
          <Link to="/explore" onClick={closeMobileMenu}>Explore</Link>
          <Link to="/study" onClick={closeMobileMenu}>Study</Link>
          <Link to="/manga" onClick={closeMobileMenu}>Manga & Comics</Link>
          <Link to="/listen-stories" onClick={closeMobileMenu}>🎧 Listen Stories</Link>
          {isAuthenticated ? (
            <>
              <Link to="/library" onClick={closeMobileMenu}>My Library</Link>
              <Link to="/favorites" onClick={closeMobileMenu}>Favorites</Link>
              <Link to="/profile" onClick={closeMobileMenu}>{user.name}'s Profile</Link>
              <button className="navbar__mobile-logout" onClick={handleLogout}>Logout</button>
            </>
          ) : (
            <Link to="/login" onClick={closeMobileMenu}>Login</Link>
          )}
        </div>
      )}
    </header>
  );
}

export default Navbar;
