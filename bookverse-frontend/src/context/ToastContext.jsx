import { createContext, useContext, useState, useCallback } from "react";
import "./Toast.css";

const ToastContext = createContext(null);

let idCounter = 0;

export function ToastProvider({ children }) {
  const [toasts, setToasts] = useState([]);

  // useCallback here means this function keeps the same identity across
  // re-renders - not critical for correctness at our scale, but it's a
  // common pattern for functions passed down through context, so any
  // component receiving it doesn't re-render unnecessarily.
  const showToast = useCallback((message, type = "info", duration = 3000) => {
    const id = idCounter++;
    setToasts((prev) => [...prev, { id, message, type }]);

    // Auto-dismiss after `duration` ms - setTimeout runs once, later,
    // then removes just this one toast by id.
    setTimeout(() => {
      setToasts((prev) => prev.filter((t) => t.id !== id));
    }, duration);
  }, []);

  const value = {
    showToast,
    success: (msg) => showToast(msg, "success"),
    error: (msg) => showToast(msg, "error"),
  };

  return (
    <ToastContext.Provider value={value}>
      {children}
      <div className="toast-container">
        {toasts.map((toast) => (
          <div key={toast.id} className={`toast toast--${toast.type}`}>
            {toast.message}
          </div>
        ))}
      </div>
    </ToastContext.Provider>
  );
}

export function useToast() {
  const context = useContext(ToastContext);
  if (!context) {
    throw new Error("useToast must be used within a ToastProvider");
  }
  return context;
}
