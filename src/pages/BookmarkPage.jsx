import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import * as API from '../services/api';
import { useAuth } from '../contexts/AuthContext';
import { WisataCard, SkeletonCards } from '../components/WisataCard';

export default function BookmarkPage() {
  const navigate = useNavigate();
  const { isLoggedIn, token } = useAuth();
  const [bookmarks, setBookmarks] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    if (!isLoggedIn) return;

    async function load() {
      try {
        const res = await API.getBookmarks(token);
        setBookmarks(res.data || []);
      } catch (err) {
        console.error('Bookmarks load failed:', err);
        setError(err);
      } finally {
        setLoading(false);
      }
    }
    load();
  }, [isLoggedIn, token]);

  if (!isLoggedIn) {
    return (
      <div className="page-wrapper">
        <div className="empty-state" style={{ paddingTop: '120px' }}>
          <div className="empty-state-icon">🔒</div>
          <div className="empty-state-title">Login Diperlukan</div>
          <p className="empty-state-text">Masuk ke akunmu untuk melihat wisata yang disimpan</p>
          <button className="btn btn-primary" onClick={() => navigate('/login')} style={{ marginTop: '16px' }}>
            <span className="material-icons-round">login</span> Masuk
          </button>
        </div>
      </div>
    );
  }

  return (
    <div className="page-wrapper page-enter">
      <div className="container" style={{ paddingTop: '32px', paddingBottom: '40px' }}>
        <div style={{ marginBottom: '24px' }}>
          <h1 className="text-h1">
            <span className="material-icons-round" style={{ verticalAlign: 'middle', color: 'var(--primary)', marginRight: '8px' }}>bookmark</span>
            Wisata Tersimpan
          </h1>
          <p className="text-body-sm" style={{ color: 'var(--text-secondary)', marginTop: '4px' }}>Koleksi wisata favoritmu</p>
        </div>

        <div className="bookmark-grid">
          {loading ? (
            <SkeletonCards count={4} />
          ) : error ? (
            <div className="empty-state" style={{ gridColumn: '1/-1' }}>
              <div className="empty-state-icon">😕</div>
              <div className="empty-state-title">Gagal memuat bookmark</div>
              <p className="empty-state-text">{error.message || 'Periksa koneksi internet'}</p>
            </div>
          ) : bookmarks.length === 0 ? (
            <div className="empty-state" style={{ gridColumn: '1/-1' }}>
              <div className="empty-state-icon">📌</div>
              <div className="empty-state-title">Belum ada bookmark</div>
              <p className="empty-state-text">Simpan wisata favoritmu untuk dilihat nanti</p>
              <button className="btn btn-primary" onClick={() => navigate('/explore')} style={{ marginTop: '16px' }}>
                <span className="material-icons-round">explore</span> Jelajahi Wisata
              </button>
            </div>
          ) : (
            bookmarks.map(bm => <WisataCard key={bm.id} wisata={bm.wisata} />)
          )}
        </div>
      </div>
    </div>
  );
}
