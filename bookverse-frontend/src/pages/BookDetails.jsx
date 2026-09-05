import { useState, useEffect } from "react";
import { useParams, Link, useNavigate } from "react-router-dom";
import { fetchBookById } from "../services/bookService";
import { addFavorite, removeFavorite, addOrUpdateLibraryEntry, recordHistoryView } from "../services/libraryService";
import { useAuth } from "../context/AuthContext";
import { useToast } from "../context/ToastContext";
import { mapApiBook } from "../utils/mapBook";
import { getDemoPrice, buildWhatsAppOrderLink } from "../utils/buyOrder";
import "./BookDetails.css";

function BookDetails() {
  const { id } = useParams();
  const { isAuthenticated } = useAuth();
  const navigate = useNavigate();
  const { success, error: showError } = useToast();

  const [book, setBook] = useState(null);
  const [loading, setLoading] = useState(true);
  const [notFound, setNotFound] = useState(false);
  const [isFavorite, setIsFavorite] = useState(false);
  const [inLibrary, setInLibrary] = useState(false);

  // --- Buy Physical Copy (WhatsApp order) state ---
  const [showOrderForm, setShowOrderForm] = useState(false);
  const [orderForm, setOrderForm] = useState({ name: "", address: "", contact: "", quantity: 1 });
  const [orderErrors, setOrderErrors] = useState({});

  useEffect(() => {
    async function loadBook() {
      try {
        setLoading(true);
        setNotFound(false);
        const data = await fetchBookById(id);
        setBook(mapApiBook(data));

        if (isAuthenticated) {
          recordHistoryView(id).catch((err) => console.error("Failed to record history", err));
        }
      } catch (err) {
        if (err.response?.status === 404) {
          setNotFound(true);
        } else {
          console.error(err);
        }
      } finally {
        setLoading(false);
      }
    }
    loadBook();
  }, [id, isAuthenticated]);

  async function handleFavoriteToggle() {
    if (!isAuthenticated) return navigate("/login");
    const next = !isFavorite;
    setIsFavorite(next);
    try {
      next ? await addFavorite(id) : await removeFavorite(id);
      success(next ? "Added to favorites" : "Removed from favorites");
    } catch (err) {
      console.error(err);
      setIsFavorite(!next);
      showError("Couldn't update favorites - please try again");
    }
  }

  async function handleAddToLibrary() {
    if (!isAuthenticated) return navigate("/login");
    try {
      await addOrUpdateLibraryEntry(id, "SAVED");
      setInLibrary(true);
      success("Added to your library");
    } catch (err) {
      console.error(err);
      showError("Couldn't add to library - please try again");
    }
  }

  function handleOrderFieldChange(event) {
    const { name, value } = event.target;
    setOrderForm((prev) => ({ ...prev, [name]: value }));
  }

  // This does NOT hit our backend at all - there's no order database, no
  // payment processing. It only validates the form, then opens a WhatsApp
  // deep link with the details pre-filled as a chat message.
  function handleOrderSubmit(event) {
    event.preventDefault();

    const errors = {};
    if (!orderForm.name.trim()) errors.name = "Name is required";
    if (!orderForm.address.trim()) errors.address = "Delivery address is required";
    if (!orderForm.contact.trim()) errors.contact = "Contact number is required";
    else if (!/^\+?[0-9]{7,15}$/.test(orderForm.contact.trim())) errors.contact = "Enter a valid phone number";
    if (!orderForm.quantity || orderForm.quantity < 1) errors.quantity = "Quantity must be at least 1";

    setOrderErrors(errors);
    if (Object.keys(errors).length > 0) return;

    // Use the book's REAL price from the database. Only if a book somehow
    // has no price set (e.g. seeded before this field existed and never
    // re-seeded) do we fall back to the old content-type-based guess.
    const price = book.price ?? getDemoPrice(book.contentType);

    const link = buildWhatsAppOrderLink({
      bookTitle: book.title,
      price,
      quantity: Number(orderForm.quantity),
      name: orderForm.name.trim(),
      address: orderForm.address.trim(),
      contact: orderForm.contact.trim(),
    });

    window.open(link, "_blank", "noopener,noreferrer");
    success("Opening WhatsApp with your order details...");
    setShowOrderForm(false);
  }

  if (loading) {
    return (
      <div className="page-container book-details">
        <div className="book-details__cover book-details__cover--skeleton" />
        <div className="book-details__info">
          <div className="book-details__skeleton-line" style={{ width: "30%" }} />
          <div className="book-details__skeleton-line" style={{ width: "60%", height: "2rem" }} />
          <div className="book-details__skeleton-line" style={{ width: "40%" }} />
        </div>
      </div>
    );
  }

  if (notFound || !book) {
    return (
      <div className="page-container book-details__missing">
        <p>We couldn't find that book.</p>
        <Link to="/explore">Back to Explore</Link>
      </div>
    );
  }

  const displayPrice = book.price ?? getDemoPrice(book.contentType);

  return (
    <div className="page-container book-details">
      <div
        className="book-details__cover"
        style={{ background: `linear-gradient(160deg, ${book.spineColor}, #14162b)` }}
      >
        <span>{book.title}</span>
      </div>

      <div className="book-details__info">
        <p className="book-details__type">{book.contentType.replace("_", " ")}</p>
        <h1>{book.title}</h1>
        <p className="book-details__author">by {book.author}</p>

        <div className="book-details__meta">
          {book.rating ? <span>★ {book.rating}</span> : <span>New - no ratings yet</span>}
          <span>{book.year}</span>
          <span>{book.genres.join(", ")}</span>
        </div>

        <div className="book-details__actions">
          {book.hasContent && (
            <Link to={`/books/${id}/read`} className="btn btn--primary">
              📖 Read
            </Link>
          )}
          <button className="btn btn--ghost" onClick={handleAddToLibrary} disabled={inLibrary}>
            {inLibrary ? "In Your Library" : "Add to Library"}
          </button>
          <button className="btn btn--ghost" onClick={handleFavoriteToggle}>
            {isFavorite ? "♥ Favorited" : "♡ Favorite"}
          </button>
          <button className="btn btn--buy" onClick={() => setShowOrderForm((v) => !v)}>
            🛒 Buy Physical Copy - ₹{displayPrice}
          </button>
        </div>

        {showOrderForm && (
          <form className="buy-order-form" onSubmit={handleOrderSubmit}>
            <p className="buy-order-form__note">
              This sends your order details as a WhatsApp message to confirm availability and delivery -
              no online payment happens here.
            </p>

            <label htmlFor="order-name">Full Name</label>
            <input id="order-name" name="name" type="text" value={orderForm.name} onChange={handleOrderFieldChange} />
            {orderErrors.name && <span className="buy-order-form__error">{orderErrors.name}</span>}

            <label htmlFor="order-address">Delivery Address</label>
            <textarea id="order-address" name="address" rows={2} value={orderForm.address} onChange={handleOrderFieldChange} />
            {orderErrors.address && <span className="buy-order-form__error">{orderErrors.address}</span>}

            <label htmlFor="order-contact">Contact Number</label>
            <input id="order-contact" name="contact" type="tel" placeholder="+91 98765 43210" value={orderForm.contact} onChange={handleOrderFieldChange} />
            {orderErrors.contact && <span className="buy-order-form__error">{orderErrors.contact}</span>}

            <label htmlFor="order-quantity">Quantity</label>
            <input id="order-quantity" name="quantity" type="number" min="1" value={orderForm.quantity} onChange={handleOrderFieldChange} />
            {orderErrors.quantity && <span className="buy-order-form__error">{orderErrors.quantity}</span>}

            <p className="buy-order-form__total">
              Total: ₹{displayPrice} × {orderForm.quantity || 1} = ₹{displayPrice * (Number(orderForm.quantity) || 1)}
            </p>

            <button type="submit" className="btn btn--buy">Send Order via WhatsApp</button>
          </form>
        )}

        {book.description && <p className="book-details__description">{book.description}</p>}
      </div>
    </div>
  );
}

export default BookDetails;
