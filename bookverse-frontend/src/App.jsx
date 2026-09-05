import { Routes, Route } from "react-router-dom";
import Navbar from "./components/Navbar";
import Footer from "./components/Footer";
import ProtectedRoute from "./components/ProtectedRoute";
import Home from "./pages/Home";
import Explore from "./pages/Explore";
import Search from "./pages/Search";
import BookDetails from "./pages/BookDetails";
import Read from "./pages/Read";
import Register from "./pages/Register";
import Login from "./pages/Login";
import Profile from "./pages/Profile";
import Library from "./pages/Library";
import Favorites from "./pages/Favorites";
import History from "./pages/History";
import ListenStoriesHome from "./pages/ListenStoriesHome";
import StorySearch from "./pages/StorySearch";
import StoryCategoryPage from "./pages/StoryCategoryPage";
import StoryDetail from "./pages/StoryDetail";

function App() {
  return (
    <>
      <Navbar />

      <main style={{ flex: 1 }}>
        <Routes>
          <Route path="/" element={<Home />} />
          <Route path="/explore" element={<Explore />} />
          <Route path="/search" element={<Search />} />
          <Route path="/books/:id" element={<BookDetails />} />
          <Route path="/books/:id/read" element={<Read />} />
          <Route path="/register" element={<Register />} />
          <Route path="/login" element={<Login />} />
          <Route
            path="/profile"
            element={
              <ProtectedRoute>
                <Profile />
              </ProtectedRoute>
            }
          />
          <Route
            path="/library"
            element={
              <ProtectedRoute>
                <Library />
              </ProtectedRoute>
            }
          />
          <Route
            path="/favorites"
            element={
              <ProtectedRoute>
                <Favorites />
              </ProtectedRoute>
            }
          />
          <Route
            path="/history"
            element={
              <ProtectedRoute>
                <History />
              </ProtectedRoute>
            }
          />

          {/* Listen Stories - all public for now (browsing doesn't need
              login; favoriting/progress-tracking in later phases will
              individually guard those specific actions, same pattern as
              the book domain's favorite/library buttons). */}
          <Route path="/listen-stories" element={<ListenStoriesHome />} />
          <Route path="/listen-stories/search" element={<StorySearch />} />
          <Route path="/listen-stories/category/:categoryName" element={<StoryCategoryPage />} />
          <Route path="/listen-stories/:id" element={<StoryDetail />} />
        </Routes>
      </main>

      <Footer />
    </>
  );
}

export default App;
