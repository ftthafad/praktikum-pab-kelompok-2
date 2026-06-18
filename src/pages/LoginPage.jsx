import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import * as API from '../services/api';
import { useAuth } from '../contexts/AuthContext';
import { useToast } from '../contexts/ToastContext';
import googleLogo from '../assets/logo-google.png';

const GOOGLE_CLIENT_ID = import.meta.env.VITE_GOOGLE_CLIENT_ID;

export default function LoginPage() {
  const navigate = useNavigate();
  const { login } = useAuth();
  const toast = useToast();

  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [submitting, setSubmitting] = useState(false);
  const [googleLoading, setGoogleLoading] = useState(false);

  const handleLogin = async (e) => {
    e.preventDefault();
    setError('');

    if (!email.trim()) { setError('Email tidak boleh kosong'); return; }
    if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)) { setError('Format email tidak valid'); return; }
    if (!password) { setError('Password tidak boleh kosong'); return; }
    if (password.length < 8) { setError('Email atau password tidak valid'); return; }

    setSubmitting(true);
    try {
      const res = await API.login(email, password);
      login(res.data);
      toast.success('Login berhasil!');

      const role = res.data.user?.role || 'user';
      navigate(role === 'super_admin' ? '/admin' : '/home');
    } catch (err) {
      setError(err.message || 'Email atau password tidak valid');
      setSubmitting(false);
    }
  };

  const handleGoogleLogin = async () => {
    setGoogleLoading(true);
    try {
      if (typeof google === 'undefined' || !google.accounts) {
        toast.error('Google Sign-In tidak tersedia. Coba muat ulang halaman.');
        setGoogleLoading(false);
        return;
      }

      const idToken = await new Promise((resolve, reject) => {
        google.accounts.id.initialize({
          client_id: GOOGLE_CLIENT_ID,
          callback: (response) => {
            if (response.credential) {
              resolve(response.credential);
            } else {
              reject(new Error('Kredensial tidak diterima'));
            }
          },
        });
        google.accounts.id.prompt((notification) => {
          if (notification.isNotDisplayed() || notification.isSkippedMoment()) {
            reject(new Error('Google Sign-In dibatalkan atau tidak tersedia'));
          }
        });
      });

      const res = await API.loginWithGoogle(idToken);
      login(res.data);
      toast.success('Login berhasil!');

      const role = res.data.user?.role || 'user';
      navigate(role === 'super_admin' ? '/admin' : '/home');
    } catch (err) {
      toast.error(err.message || 'Gagal masuk dengan Google');
      setGoogleLoading(false);
    }
  };

  return (
    <div className="auth-page">
      <div className="auth-card">
        <div className="auth-logo">
          <div className="auth-logo-icon">🌿</div>
          <h1 className="auth-title">TravelWaka</h1>
          <p className="auth-subtitle">Jelajahi Wisata Jawa Tengah</p>
        </div>

        <h2 style={{ fontSize: '1.375rem', fontWeight: 700, color: 'var(--primary)', textAlign: 'center', marginBottom: '24px' }}>Masuk</h2>

        {error && (
          <div className="form-error" style={{ marginBottom: '12px', display: 'block', background: 'rgba(220,38,38,0.08)', padding: '12px', borderRadius: 'var(--radius-sm)' }}>
            {error}
          </div>
        )}

        <form onSubmit={handleLogin}>
          <div className="form-group">
            <label className="form-label" htmlFor="login-email">Email</label>
            <input
              type="email"
              className="form-input"
              id="login-email"
              placeholder="nama@email.com"
              required
              autoComplete="email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
            />
          </div>

          <div className="form-group">
            <label className="form-label" htmlFor="login-password">Password</label>
            <input
              type="password"
              className="form-input"
              id="login-password"
              placeholder="Masukkan password"
              required
              autoComplete="current-password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
            />
          </div>

          <button type="submit" className="btn btn-primary btn-lg w-full" disabled={submitting}>
            {submitting ? (
              <><div className="spinner" style={{ width: '20px', height: '20px', borderWidth: '2px' }}></div> Memproses...</>
            ) : 'Masuk'}
          </button>
        </form>

        <div className="auth-divider">atau</div>

        <button className="btn-google" onClick={handleGoogleLogin} disabled={googleLoading}>
          {googleLoading ? (
            <><div className="spinner" style={{ width: '18px', height: '18px', borderWidth: '2px' }}></div> Menghubungkan...</>
          ) : (
            <><img src={googleLogo} alt="Google" className="btn-google-icon" /><span>Masuk dengan Google</span></>
          )}
        </button>

        <div style={{ marginTop: '16px' }}>
          <button className="btn btn-outline w-full" onClick={() => navigate('/home')} style={{ height: '48px', fontWeight: 600 }}>
            Lewati, Jelajahi Dulu
          </button>
        </div>

        <div className="auth-footer">
          Belum punya akun? <a href="#" onClick={(e) => { e.preventDefault(); navigate('/register'); }}>Daftar</a>
        </div>
      </div>
    </div>
  );
}
