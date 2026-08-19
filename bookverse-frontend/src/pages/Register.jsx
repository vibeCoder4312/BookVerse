import { useState } from "react";
import { useNavigate, Link } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import "./AuthForm.css";

function Register() {
  const { register } = useAuth();
  const navigate = useNavigate(); // lets us redirect after a successful submit

  // One state object holding all form fields, instead of 4 separate
  // useState calls - a common pattern once a form grows past 2-3 fields.
  const [form, setForm] = useState({ name: "", email: "", password: "", confirmPassword: "" });
  const [error, setError] = useState(null);
  const [submitting, setSubmitting] = useState(false);

  // A single reusable change handler for every input - it reads WHICH
  // field changed from event.target.name, so we don't need one handler
  // per field.
  function handleChange(event) {
    const { name, value } = event.target;
    setForm((prev) => ({ ...prev, [name]: value }));
  }

  async function handleSubmit(event) {
    event.preventDefault(); // stops the browser's default full-page-reload form submit
    setError(null);
    setSubmitting(true);

    try {
      await register(form.name, form.email, form.password, form.confirmPassword);
      navigate("/"); // send them to the homepage, now logged in
    } catch (err) {
      // Our backend's GlobalExceptionHandler returns { message: "..." }
      // for most errors, or { fieldErrors: {...} } for validation failures.
      const data = err.response?.data;
      if (data?.fieldErrors) {
        setError(Object.values(data.fieldErrors).join(", "));
      } else {
        setError(data?.message || "Something went wrong. Please try again.");
      }
    } finally {
      setSubmitting(false);
    }
  }

  return (
    <div className="page-container auth-page">
      <form className="auth-form" onSubmit={handleSubmit}>
        <h1>Create your account</h1>

        {error && <p className="auth-form__error">{error}</p>}

        <label htmlFor="name">Name</label>
        <input id="name" name="name" type="text" value={form.name} onChange={handleChange} required />

        <label htmlFor="email">Email</label>
        <input id="email" name="email" type="email" value={form.email} onChange={handleChange} required />

        <label htmlFor="password">Password</label>
        <input id="password" name="password" type="password" value={form.password} onChange={handleChange} required />

        <label htmlFor="confirmPassword">Confirm Password</label>
        <input id="confirmPassword" name="confirmPassword" type="password" value={form.confirmPassword} onChange={handleChange} required />

        <button className="btn btn--primary" type="submit" disabled={submitting}>
          {submitting ? "Creating account..." : "Register"}
        </button>

        <p className="auth-form__switch">
          Already have an account? <Link to="/login">Log in</Link>
        </p>
      </form>
    </div>
  );
}

export default Register;
