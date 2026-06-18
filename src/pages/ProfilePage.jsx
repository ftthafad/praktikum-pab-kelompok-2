import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import * as API from '../services/api';
import { useAuth } from '../contexts/AuthContext';
import { useToast } from '../contexts/ToastContext';
import { useModal } from '../contexts/ModalContext';

export default function ProfilePage() {
  const navigate = useNavigate();
  const auth = useAuth();
  const toast = useToast();
  const modal = useModal();
  const { isLoggedIn, token, user } = auth;

  const [currentUser, setCurrentUser] = useState(null);

  useEffect(() => {
    if (!isLoggedIn) return;
    async function loadProfile() {
      try {
        const meRes = await API.me(token);
        if (meRes.status && meRes.data) {
          setCurrentUser(meRes.data);
        }
      } catch (e) { /* ignore */ }
    }
    loadProfile();
  }, [isLoggedIn, token]);

  if (!isLoggedIn) {
    return (
      <div className="page-wrapper page-enter">
        <div className="profile-guest">
          <div className="profile-guest-icon">
            <span className="material-icons-round">person</span>
          </div>
          <h3>Masuk ke TravelWaka</h3>
          <p>Masuk atau buat akun untuk mengakses profil lengkap Anda, mengelola wisata, mengirim pengajuan pengelola, dan melihat notifikasi terbaru.</p>
          <button className="btn btn-primary btn-lg w-full" onClick={() => navigate('/login')} style={{ maxWidth: '400px' }}>
            Masuk / Daftar Akun
          </button>
        </div>
      </div>
    );
  }

  const name = user?.name || '';
  const email = user?.email || '';
  const role = user?.role || 'user';
  const initial = name ? name.charAt(0).toUpperCase() : '?';

  const roleLabel = {
    'pengelola': 'Pengelola Wisata',
    'super_admin': 'Super Admin',
    'user': 'Wisatawan',
  }[role] || 'Wisatawan';

  const bookmarkCount = currentUser?.bookmarks_count || 0;
  const reviewCount = currentUser?.reviews_count || 0;
  const wisataCount = currentUser?.wisata_count || 0;

  const handleLogout = () => {
    modal.show({
      icon: 'logout',
      iconClass: 'modal-icon-danger',
      title: 'Keluar dari Akun?',
      body: 'Kamu akan keluar dari akun ini. Yakin ingin melanjutkan?',
      confirmText: 'Keluar',
      cancelText: 'Batal',
      dangerConfirm: true,
      onConfirm: async () => {
        try { await API.logout(token); } catch (e) { /* ignore */ }
        auth.logout();
        toast.info('Kamu telah keluar');
        navigate('/login');
      }
    });
  };

  const showAboutDialog = () => {
    modal.show({
      icon: 'info',
      iconClass: 'modal-icon-info',
      title: 'Tentang TravelWaka',
      body: (
        <>
          Aplikasi ini dibuat untuk memudahkan wisatawan menjelajahi destinasi terbaik di Jawa Tengah.
          <br /><br />
          <span style={{ fontWeight: 300, fontSize: '12px', color: '#9ca3af' }}>
            Versi 1.0.0 (Beta) &copy; 2026 Kelompok 2. All Rights Reserved.
          </span>
        </>
      ),
      confirmText: 'Tutup',
      cancelText: null,
    });
  };

  return (
    <div className="page-wrapper page-enter">
      {/* Header */}
      <div className="profile-header">
        {currentUser?.avatar ? (
          <img className="profile-avatar-img" src={currentUser.avatar} alt="Avatar"
            onError={(e) => { e.target.outerHTML = `<div class="profile-avatar">${initial}</div>`; }} />
        ) : (
          <div className="profile-avatar">{initial}</div>
        )}
        <div className="profile-name">{name}</div>
        <div className="profile-email">{email}</div>
        <span className="profile-role-badge">{roleLabel}</span>
      </div>

      {/* Stats */}
      {currentUser && (
        <div className="profile-stats-row">
          <div className="profile-stat-card">
            <div className="profile-stat-icon-wrap"><span className="material-icons-round">bookmark</span></div>
            <div className="profile-stat-count">{bookmarkCount}</div>
            <div className="profile-stat-label">Bookmark</div>
          </div>
          <div className="profile-stat-card">
            <div className="profile-stat-icon-wrap"><span className="material-icons-round">star</span></div>
            <div className="profile-stat-count">{reviewCount}</div>
            <div className="profile-stat-label">Ulasan</div>
          </div>
          {role === 'pengelola' && (
            <div className="profile-stat-card">
              <div className="profile-stat-icon-wrap"><span className="material-icons-round">map</span></div>
              <div className="profile-stat-count">{wisataCount}</div>
              <div className="profile-stat-label">Wisata Saya</div>
            </div>
          )}
        </div>
      )}

      <div className="container" style={{ paddingTop: currentUser ? '0' : '24px', paddingBottom: '40px' }}>
        {/* Account Menu */}
        <div className="profile-menu-group">
          <div className="profile-menu-label">Akun</div>
          <div className="profile-menu-card">
            <MenuItem icon="notifications" title="Notifikasi" subtitle="Status pengajuan pengelola" color="var(--primary)"
              onClick={() => navigate('/notifikasi')} />
          </div>
        </div>

        {/* Support & About */}
        <div className="profile-menu-group">
          <div className="profile-menu-label">Dukungan & Tentang</div>
          <div className="profile-menu-card">
            <MenuItem icon="help" title="Hubungi Bantuan" subtitle="Chat admin via WhatsApp" color="var(--primary)"
              onClick={() => window.open('https://wa.me/6289522932718?text=Halo%20Admin%20TravelWaka,%20saya%20butuh%20bantuan.', '_blank')} />
            <div style={{ borderTop: '1px solid var(--divider)', margin: '0 16px' }}></div>
            <MenuItem icon="info" title="Tentang Aplikasi" subtitle="Informasi versi dan pengembang" color="var(--primary)"
              onClick={showAboutDialog} />
          </div>
        </div>

        {/* Pengelola Menu (only for user role) */}
        {role === 'user' && (
          <div className="profile-menu-group">
            <div className="profile-menu-label">Pengelola</div>
            <div className="profile-menu-card">
              <MenuItem icon="store" title="Ajukan Jadi Pengelola" subtitle="Daftarkan destinasi wisatamu" color="var(--primary-medium)"
                onClick={() => navigate('/pengajuan')} />
            </div>
          </div>
        )}

        {/* Pengelola Wisata Menu (only for pengelola role) */}
        {role === 'pengelola' && (
          <div className="profile-menu-group">
            <div className="profile-menu-label">Pengelola Wisata</div>
            <div className="profile-menu-card">
              <MenuItem icon="map" title="Kelola Wisata" subtitle="Tambah, edit, hapus wisata milikmu" color="var(--primary)"
                onClick={() => navigate('/pengelola')} />
            </div>
          </div>
        )}

        {/* Logout */}
        <div className="profile-menu-group" style={{ marginTop: '8px' }}>
          <div className="profile-menu-card" style={{ background: 'rgba(220,38,38,0.05)' }}>
            <div className="profile-menu-item" onClick={handleLogout} style={{ cursor: 'pointer' }}>
              <div className="profile-menu-icon" style={{ background: 'rgba(220,38,38,0.12)' }}>
                <span className="material-icons-round" style={{ color: 'var(--error)' }}>logout</span>
              </div>
              <div className="profile-menu-text">
                <h4 style={{ color: 'var(--error)' }}>Keluar</h4>
                <p>Keluar dari akun</p>
              </div>
              <span className="material-icons-round chevron">chevron_right</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}

function MenuItem({ icon, title, subtitle, color, onClick }) {
  return (
    <div className="profile-menu-item" onClick={onClick} style={{ cursor: 'pointer' }}>
      <div className="profile-menu-icon" style={{ background: `${color}1a` }}>
        <span className="material-icons-round" style={{ color }}>{icon}</span>
      </div>
      <div className="profile-menu-text">
        <h4>{title}</h4>
        <p>{subtitle}</p>
      </div>
      <span className="material-icons-round chevron">chevron_right</span>
    </div>
  );
}
