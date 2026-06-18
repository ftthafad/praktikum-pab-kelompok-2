import { useState, useEffect, useCallback } from 'react';
import { useNavigate } from 'react-router-dom';
import * as API from '../services/api';
import { useAuth } from '../contexts/AuthContext';
import { useToast } from '../contexts/ToastContext';
import { useModal } from '../contexts/ModalContext';

export default function PengelolaPage() {
  const navigate = useNavigate();
  const { isLoggedIn, token, user } = useAuth();
  const toast = useToast();
  const modal = useModal();
  const [wisataList, setWisataList] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    if (!isLoggedIn || user?.role !== 'pengelola') {
      navigate('/profile');
      return;
    }
    loadWisata();
  }, [isLoggedIn, user, navigate]);

  const loadWisata = useCallback(async () => {
    try {
      const res = await API.getWisataSaya(token);
      setWisataList(res.data || []);
    } catch (err) {
      console.error('Failed to load wisata:', err);
    } finally {
      setLoading(false);
    }
  }, [token]);

  const confirmDelete = (wisataId, name) => {
    modal.show({
      icon: 'delete',
      iconClass: 'modal-icon-danger',
      title: 'Hapus Wisata?',
      body: `Wisata "${name}" akan dihapus permanen. Tindakan ini tidak bisa dibatalkan.`,
      confirmText: 'Hapus',
      cancelText: 'Batal',
      dangerConfirm: true,
      onConfirm: async () => {
        try {
          await API.deleteWisata(wisataId, token);
          toast.success('Wisata berhasil dihapus');
          loadWisata();
        } catch (err) {
          toast.error(err.message || 'Gagal menghapus wisata');
        }
      }
    });
  };

  return (
    <div className="page-wrapper page-enter">
      <div className="container" style={{ paddingTop: '32px', paddingBottom: '40px' }}>
        <div className="flex justify-between items-center" style={{ marginBottom: '24px' }}>
          <div className="flex items-center gap-md">
            <button className="btn btn-icon btn-ghost" onClick={() => navigate('/profile')}>
              <span className="material-icons-round">arrow_back</span>
            </button>
            <div>
              <h1 className="text-h1">Wisata Saya</h1>
              <p className="text-body-sm" style={{ color: 'var(--text-secondary)' }}>Kelola destinasi wisata milikmu</p>
            </div>
          </div>
          <button className="btn btn-primary" onClick={() => navigate('/wisata/new')}>
            <span className="material-icons-round">add</span> Tambah
          </button>
        </div>

        <div style={{ display: 'flex', flexDirection: 'column', gap: '16px' }}>
          {loading ? (
            <div className="page-loader"><div className="spinner"></div></div>
          ) : wisataList.length === 0 ? (
            <div className="empty-state">
              <div className="empty-state-icon">🏖️</div>
              <div className="empty-state-title">Belum ada wisata</div>
              <p className="empty-state-text">Mulai tambahkan destinasi wisata yang kamu kelola</p>
              <button className="btn btn-primary" onClick={() => navigate('/wisata/new')} style={{ marginTop: '16px' }}>
                <span className="material-icons-round">add</span> Tambah Wisata
              </button>
            </div>
          ) : (
            wisataList.map(w => (
              <WisataManageCard key={w.id} wisata={w} navigate={navigate} onDelete={confirmDelete} />
            ))
          )}
        </div>
      </div>
    </div>
  );
}

function WisataManageCard({ wisata, navigate, onDelete }) {
  const imageUrl = wisata.cover_photo?.photo_url || (wisata.photos && wisata.photos[0]?.photo_url) || '';

  return (
    <div className="card" style={{ padding: 0 }}>
      <div className="flex" style={{ minHeight: '120px' }}>
        {imageUrl ? (
          <img src={imageUrl} alt={wisata.name} style={{ width: '160px', minWidth: '160px', height: '120px', objectFit: 'cover' }}
            onError={(e) => { e.target.style.background = 'linear-gradient(135deg,#9CD5FF,#7AAACE)'; e.target.src = ''; }} />
        ) : (
          <div style={{ width: '160px', minWidth: '160px', height: '120px', background: 'linear-gradient(135deg,#9CD5FF,#7AAACE)', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
            <span className="material-icons-round" style={{ fontSize: '36px', color: 'var(--primary)', opacity: 0.5 }}>landscape</span>
          </div>
        )}
        <div style={{ flex: 1, padding: '16px', display: 'flex', flexDirection: 'column', justifyContent: 'space-between' }}>
          <div>
            <h3 className="text-h3" style={{ marginBottom: '4px' }}>{wisata.name}</h3>
            <div className="flex items-center gap-xs" style={{ color: 'var(--text-secondary)' }}>
              <span className="material-icons-round" style={{ fontSize: '14px' }}>location_on</span>
              <span className="text-body-sm">{wisata.location}</span>
            </div>
          </div>
          <div className="flex gap-sm" style={{ marginTop: '12px' }}>
            <button className="btn btn-sm btn-secondary" onClick={() => navigate(`/wisata/edit/${wisata.id}`)}>
              <span className="material-icons-round">edit</span> Edit
            </button>
            <button className="btn btn-sm btn-ghost" style={{ color: 'var(--error)' }} onClick={() => onDelete(wisata.id, wisata.name)}>
              <span className="material-icons-round">delete</span> Hapus
            </button>
          </div>
        </div>
      </div>
    </div>
  );
}
