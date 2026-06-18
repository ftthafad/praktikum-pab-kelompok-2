import { createContext, useContext, useState, useCallback } from 'react';

const ModalContext = createContext(null);

export function ModalProvider({ children }) {
  const [modal, setModal] = useState(null);

  const hide = useCallback(() => {
    setModal(null);
  }, []);

  const show = useCallback(({ icon, iconClass = 'modal-icon-info', title, body, confirmText = 'OK', cancelText = 'Batal', onConfirm, onCancel, dangerConfirm = false }) => {
    setModal({ type: 'confirm', icon, iconClass, title, body, confirmText, cancelText, onConfirm, onCancel, dangerConfirm });
  }, []);

  const prompt = useCallback(({ title, body, placeholder = '', confirmText = 'Kirim', onConfirm }) => {
    setModal({ type: 'prompt', title, body, placeholder, confirmText, onConfirm });
  }, []);

  return (
    <ModalContext.Provider value={{ show, hide, prompt }}>
      {children}
      {modal && <ModalOverlay modal={modal} hide={hide} />}
    </ModalContext.Provider>
  );
}

function ModalOverlay({ modal, hide }) {
  const handleOverlayClick = (e) => {
    if (e.target === e.currentTarget) {
      hide();
      if (modal.onCancel) modal.onCancel();
    }
  };

  if (modal.type === 'prompt') {
    return <PromptModal modal={modal} hide={hide} onOverlayClick={handleOverlayClick} />;
  }

  return (
    <div className="modal-overlay" onClick={handleOverlayClick}>
      <div className="modal">
        {modal.icon && (
          <div className={`modal-icon ${modal.iconClass}`}>
            <span className="material-icons-round">{modal.icon}</span>
          </div>
        )}
        <h3 className="modal-title">{modal.title}</h3>
        <p className="modal-body">{modal.body}</p>
        <div className="modal-actions">
          {modal.cancelText && (
            <button className="btn btn-ghost" onClick={() => { hide(); if (modal.onCancel) modal.onCancel(); }}>
              {modal.cancelText}
            </button>
          )}
          <button
            className={`btn ${modal.dangerConfirm ? 'btn-danger' : 'btn-primary'}`}
            onClick={() => { hide(); if (modal.onConfirm) modal.onConfirm(); }}
          >
            {modal.confirmText}
          </button>
        </div>
      </div>
    </div>
  );
}

function PromptModal({ modal, hide, onOverlayClick }) {
  const [inputValue, setInputValue] = useState('');

  return (
    <div className="modal-overlay" onClick={onOverlayClick}>
      <div className="modal" style={{ textAlign: 'left' }}>
        <h3 className="modal-title">{modal.title}</h3>
        <p className="modal-body" style={{ marginBottom: '12px' }}>{modal.body}</p>
        <div className="form-group" style={{ marginBottom: '16px' }}>
          <textarea
            className="form-input form-textarea"
            placeholder={modal.placeholder}
            rows="3"
            value={inputValue}
            onChange={(e) => setInputValue(e.target.value)}
          />
        </div>
        <div className="modal-actions" style={{ justifyContent: 'flex-end' }}>
          <button className="btn btn-ghost" onClick={hide}>Batal</button>
          <button className="btn btn-primary" onClick={() => { const val = inputValue.trim(); hide(); if (modal.onConfirm) modal.onConfirm(val); }}>
            {modal.confirmText}
          </button>
        </div>
      </div>
    </div>
  );
}

export function useModal() {
  const ctx = useContext(ModalContext);
  if (!ctx) throw new Error('useModal must be used within ModalProvider');
  return ctx;
}
