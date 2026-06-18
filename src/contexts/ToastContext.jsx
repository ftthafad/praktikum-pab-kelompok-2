import { createContext, useContext, useState, useCallback } from 'react';

const ToastContext = createContext(null);

export function ToastProvider({ children }) {
  const [toasts, setToasts] = useState([]);

  const removeToast = useCallback((id) => {
    setToasts(prev => prev.map(t => t.id === id ? { ...t, exiting: true } : t));
    setTimeout(() => {
      setToasts(prev => prev.filter(t => t.id !== id));
    }, 250);
  }, []);

  const show = useCallback((message, type = 'info', duration = 3500) => {
    const id = Date.now() + Math.random();
    setToasts(prev => [...prev, { id, message, type, exiting: false }]);
    setTimeout(() => removeToast(id), duration);
  }, [removeToast]);

  const success = useCallback((msg) => show(msg, 'success'), [show]);
  const error = useCallback((msg) => show(msg, 'error'), [show]);
  const info = useCallback((msg) => show(msg, 'info'), [show]);

  const iconMap = { success: 'check_circle', error: 'error', info: 'info' };

  return (
    <ToastContext.Provider value={{ show, success, error, info }}>
      {children}
      <div id="toast-container">
        {toasts.map(toast => (
          <div key={toast.id} className={`toast toast-${toast.type}${toast.exiting ? ' exiting' : ''}`}>
            <span className="material-icons-round">{iconMap[toast.type] || 'info'}</span>
            <span className="toast-text">{toast.message}</span>
            <span
              className="material-icons-round toast-close"
              onClick={() => removeToast(toast.id)}
            >
              close
            </span>
          </div>
        ))}
      </div>
    </ToastContext.Provider>
  );
}

export function useToast() {
  const ctx = useContext(ToastContext);
  if (!ctx) throw new Error('useToast must be used within ToastProvider');
  return ctx;
}
