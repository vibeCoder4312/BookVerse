import { Link } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import "./Profile.css";

function Profile() {
  const { user } = useAuth();

  return (
    <div className="page-container profile-page">
      <h1>My Profile</h1>
      <div className="profile-card">
        <p><strong>Name:</strong> {user.name}</p>
        <p><strong>Email:</strong> {user.email}</p>
        <p><strong>Role:</strong> {user.role}</p>
      </div>

      <div className="profile-links">
        <Link to="/library">My Library</Link>
        <Link to="/favorites">Favorites</Link>
        <Link to="/history">Reading History</Link>
      </div>
    </div>
  );
}

export default Profile;
