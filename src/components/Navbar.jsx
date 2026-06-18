import { useState } from 'react';
import { Link, useLocation } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';

export default function Navbar() {
  const { isLoggedIn, user } = useAuth();
  const location = useLocation();
  const [mobileOpen, setMobileOpen] = useState(false);

  const currentPath = location.pathname;
  const role = user?.role || 'user';
  const name = user?.name || '';

  const isActive = (path) => currentPath === path || currentPath.startsWith(path + '/');

  let links;
  if (role === 'super_admin' && isLoggedIn) {
    links = (
      <Link to="/admin" className={`navbar-link ${isActive('/admin') ? 'active' : ''}`} onClick={() => setMobileOpen(false)}>
        <span className="material-icons-round">admin_panel_settings</span> Dashboard
      </Link>
    );
  } else {
    links = (
      <>
        <Link to="/home" className={`navbar-link ${currentPath === '/home' ? 'active' : ''}`} onClick={() => setMobileOpen(false)}>
          <span className="material-icons-round">home</span> Beranda
        </Link>
        <Link to="/explore" className={`navbar-link ${isActive('/explore') ? 'active' : ''}`} onClick={() => setMobileOpen(false)}>
          <span className="material-icons-round">explore</span> Jelajahi
        </Link>
        {isLoggedIn && (
          <Link to="/bookmark" className={`navbar-link ${currentPath === '/bookmark' ? 'active' : ''}`} onClick={() => setMobileOpen(false)}>
            <span className="material-icons-round">bookmark</span> Tersimpan
          </Link>
        )}
      </>
    );
  }

  let actions;
  if (isLoggedIn) {
    actions = (
      <Link to="/profile" className="navbar-avatar" title={name}>
        {name ? name.charAt(0).toUpperCase() : '?'}
      </Link>
    );
  } else {
    actions = (
      <>
        <Link to="/login" className="btn btn-sm" style={{ color: 'rgba(255,255,255,0.8)' }}>Masuk</Link>
        <Link to="/register" className="btn btn-sm" style={{ background: 'rgba(255,255,255,0.15)', color: 'var(--white)', border: '1px solid rgba(255,255,255,0.2)' }}>Daftar</Link>
      </>
    );
  }

  return (
    <>
      <header className="navbar">
        <div className="navbar-inner">
          <Link to="/home" className="navbar-brand">
            <div className="navbar-brand-icon">🌿</div>
            <div>
              <span className="navbar-brand-text">TravelWaka</span>
              <span className="navbar-brand-sub">Jelajahi Jawa Tengah</span>
            </div>
          </Link>

          <nav className="navbar-links">
            {links}
          </nav>

          <div className="navbar-actions">
            {actions}
            <button className="navbar-toggle" onClick={() => setMobileOpen(!mobileOpen)} aria-label="Menu">
              <span className="material-icons-round">menu</span>
            </button>
          </div>
        </div>
      </header>

      <div className={`navbar-mobile-menu${mobileOpen ? ' open' : ''}`}>
        {links}
        {!isLoggedIn ? (
          <div style={{ padding: '12px 16px', display: 'flex', gap: '8px' }}>
            <Link to="/login" className="btn btn-outline w-full" style={{ borderColor: 'rgba(255,255,255,0.3)', color: 'var(--white)' }} onClick={() => setMobileOpen(false)}>Masuk</Link>
            <Link to="/register" className="btn btn-primary w-full" onClick={() => setMobileOpen(false)}>Daftar</Link>
          </div>
        ) : (
          <Link to="/profile" className="navbar-link" onClick={() => setMobileOpen(false)}>
            <span className="material-icons-round">person</span> Profil
          </Link>
        )}
      </div>
    </>
  );
}
