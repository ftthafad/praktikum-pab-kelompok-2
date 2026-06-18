import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import * as API from '../services/api';
import { useAuth } from '../contexts/AuthContext';
import { useToast } from '../contexts/ToastContext';

export default function RegisterPage() {
  const navigate = useNavigate();
  const { login } = useAuth();
  const toast = useToast();

  const [name, setName] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [error, setError] = useState('');
  const [submitting, setSubmitting] = useState(false);

  const handleRegister = async (e) => {
    e.preventDefault();
    setError('');

    if (password !== confirmPassword) {
      setError('Password dan konfirmasi tidak sama');
      return;
    }
    if (password.length < 8) {
      setError('Password minimal 8 karakter');
      return;
    }

    setSubmitting(true);
    try {
      const res = await API.register(name, email, password, confirmPassword);
      login(res.data);
      toast.success('Registrasi berhasil! Selamat datang!');
      navigate('/home');
    } catch (err) {
      const msg = err.message || 'Gagal mendaftar. Coba lagi.';
      setError(msg);
      setSubmitting(false);
    }
  };

  return (
    <div className="auth-page">
      <div className="auth-card">
        <div className="auth-logo">
          <div className="auth-logo-icon">🌿</div>
          <h1 className="auth-title">Buat Akun</h1>
          <p className="auth-subtitle">Daftar dan mulai jelajahi wisata Jawa Tengah</p>
        </div>

        <form onSubmit={handleRegister}>
          <div className="form-group">
            <label className="form-label" htmlFor="reg-name">Nama Lengkap</label>
            <input type="text" className="form-input" id="reg-name" placeholder="Masukkan nama lengkap" required autoComplete="name"
              value={name} onChange={(e) => setName(e.target.value)} />
          </div>

          <div className="form-group">
            <label className="form-label" htmlFor="reg-email">Email</label>
            <input type="email" className="form-input" id="reg-email" placeholder="nama@email.com" required autoComplete="email"
              value={email} onChange={(e) => setEmail(e.target.value)} />
          </div>

          <div className="form-group">
            <label className="form-label" htmlFor="reg-password">Password</label>
            <input type="password" className="form-input" id="reg-password" placeholder="Minimal 8 karakter" required minLength="8" autoComplete="new-password"
              value={password} onChange={(e) => setPassword(e.target.value)} />
          </div>

          <div className="form-group">
            <label className="form-label" htmlFor="reg-password-confirm">Konfirmasi Password</label>
            <input type="password" className="form-input" id="reg-password-confirm" placeholder="Ulangi password" required autoComplete="new-password"
              value={confirmPassword} onChange={(e) => setConfirmPassword(e.target.value)} />
          </div>

          {error && (
            <div className="form-error" style={{ marginBottom: '12px', display: 'block' }}>{error}</div>
          )}

          <button type="submit" className="btn btn-primary btn-lg w-full" disabled={submitting}>
            {submitting ? (
              <><div className="spinner" style={{ width: '20px', height: '20px', borderWidth: '2px' }}></div> Memproses...</>
            ) : (
              <><span className="material-icons-round">person_add</span> Daftar</>
            )}
          </button>
        </form>

        <div className="auth-footer">
          Sudah punya akun? <a href="#" onClick={(e) => { e.preventDefault(); navigate('/login'); }}>Masuk</a>
        </div>
      </div>
    </div>
  );
}
