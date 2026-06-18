import { useState, useEffect, useMemo, useCallback } from 'react';
import { useNavigate } from 'react-router-dom';
import * as API from '../services/api';
import { useAuth } from '../contexts/AuthContext';
import { useToast } from '../contexts/ToastContext';
import { useModal } from '../contexts/ModalContext';

export default function AdminDashboardPage() {
  const navigate = useNavigate();
  const auth = useAuth();
  const toast = useToast();
  const modal = useModal();
  const { isLoggedIn, token, user } = auth;

  const [allPengajuan, setAllPengajuan] = useState([]);
  const [activeTab, setActiveTab] = useState('pending');
  const [loading, setLoading] = useState(true);

  const loadData = useCallback(async () => {
    try {
      const res = await API.getSemuaPengajuan(token);
      setAllPengajuan(res.data || []);
    } catch (err) {
      console.error('Failed to load pengajuan:', err);
    } finally {
      setLoading(false);
    }
  }, [token]);

  useEffect(() => {
    if (!isLoggedIn || user?.role !== 'super_admin') {
      navigate('/login');
      return;
    }
    loadData();
  }, [isLoggedIn, user, navigate, loadData]);

  const stats = useMemo(() => ({
    total: allPengajuan.length,
    pending: allPengajuan.filter(p => p.status === 'pending').length,
    approved: allPengajuan.filter(p => p.status === 'approved').length,
    rejected: allPengajuan.filter(p => p.status === 'rejected').length,
  }), [allPengajuan]);

  const filtered = useMemo(() => {
    return allPengajuan.filter(p => p.status === activeTab);
  }, [allPengajuan, activeTab]);

  const handleApprove = (id) => {
    modal.show({
      icon: 'check_circle',
      iconClass: 'modal-icon-success',
      title: 'Setujui Pengajuan?',
      body: 'Pengguna akan mendapatkan akses sebagai pengelola wisata.',
      confirmText: 'Setujui',
      cancelText: 'Batal',
      onConfirm: async () => {
        try {
          await API.approvePengajuan(id, token);
          toast.success('Pengajuan disetujui!');
          loadData();
        } catch (err) {
          toast.error(err.message || 'Gagal menyetujui');
        }
      }
    });
  };

  const handleReject = (id) => {
    modal.prompt({
      title: 'Tolak Pengajuan',
      body: 'Berikan alasan penolakan:',
      placeholder: 'Tuliskan catatan untuk pemohon...',
      confirmText: 'Tolak',
      onConfirm: async (catatan) => {
        if (!catatan) {
          toast.error('Catatan penolakan harus diisi');
          return;
        }
        try {
          await API.rejectPengajuan(id, catatan, token);
          toast.success('Pengajuan ditolak');
          loadData();
        } catch (err) {
          toast.error(err.message || 'Gagal menolak');
        }
      }
    });
  };

  const handleLogout = () => {
    modal.show({
      icon: 'logout',
      iconClass: 'modal-icon-danger',
      title: 'Keluar dari Akun?',
      body: 'Kamu akan keluar dari dashboard admin.',
      confirmText: 'Keluar',
      cancelText: 'Batal',
      dangerConfirm: true,
      onConfirm: async () => {
        try { await API.logout(token); } catch (e) { /* ignore */ }
        auth.logout();
        navigate('/login');
      }
    });
  };

  return (
    <div className="page-wrapper page-enter">
      {/* Admin Header */}
      <div className="admin-header">
        <div className="container">
          <div className="flex justify-between items-center">
            <div>
              <h1 className="text-h1" style={{ color: 'var(--white)', marginBottom: '4px' }}>Dashboard Admin</h1>
              <p className="text-body-sm" style={{ color: 'var(--primary-light)' }}>Kelola pengajuan pengelola wisata</p>
            </div>
            <button className="btn btn-sm" style={{ background: 'rgba(255,255,255,0.15)', color: 'var(--white)', border: '1px solid rgba(255,255,255,0.2)' }}
              onClick={handleLogout}>
              <span className="material-icons-round">logout</span> Keluar
            </button>
          </div>

          <div className="stat-cards">
            <div className="stat-card">
              <div className="stat-card-value">{stats.total}</div>
              <div className="stat-card-label">Total Pengajuan</div>
            </div>
            <div className="stat-card">
              <div className="stat-card-value" style={{ color: 'var(--pending)' }}>{stats.pending}</div>
              <div className="stat-card-label">Menunggu</div>
            </div>
            <div className="stat-card">
              <div className="stat-card-value" style={{ color: 'var(--success)' }}>{stats.approved}</div>
              <div className="stat-card-label">Disetujui</div>
            </div>
            <div className="stat-card">
              <div className="stat-card-value" style={{ color: '#f87171' }}>{stats.rejected}</div>
              <div className="stat-card-label">Ditolak</div>
            </div>
          </div>
        </div>
      </div>

      <div className="container" style={{ paddingTop: '24px', paddingBottom: '40px' }}>
        {/* Tabs */}
        <div className="tabs">
          {['pending', 'approved', 'rejected'].map(tab => (
            <div key={tab} className={`tab ${activeTab === tab ? 'active' : ''}`} onClick={() => setActiveTab(tab)}>
              <span className="material-icons-round" style={{ fontSize: '16px', verticalAlign: 'middle', marginRight: '4px' }}>
                {tab === 'pending' ? 'hourglass_empty' : tab === 'approved' ? 'check_circle' : 'cancel'}
              </span>
              {tab === 'pending' ? 'Menunggu' : tab === 'approved' ? 'Disetujui' : 'Ditolak'}
            </div>
          ))}
        </div>

        {/* List */}
        <div style={{ display: 'flex', flexDirection: 'column', gap: '16px' }}>
          {loading ? (
            <div className="page-loader"><div className="spinner"></div></div>
          ) : filtered.length === 0 ? (
            <div className="empty-state">
              <div className="empty-state-icon">{activeTab === 'pending' ? '📋' : activeTab === 'approved' ? '✅' : '❌'}</div>
              <div className="empty-state-title">Tidak ada pengajuan {activeTab}</div>
            </div>
          ) : (
            filtered.map(p => (
              <PengajuanCard key={p.id} pengajuan={p} onApprove={handleApprove} onReject={handleReject} />
            ))
          )}
        </div>
      </div>
    </div>
  );
}

function PengajuanCard({ pengajuan: p, onApprove, onReject }) {
  const statusConfig = {
    pending: { class: 'status-pending', label: 'Menunggu' },
    approved: { class: 'status-approved', label: 'Disetujui' },
    rejected: { class: 'status-rejected', label: 'Ditolak' },
  };
  const sc = statusConfig[p.status] || statusConfig.pending;

  return (
    <div className="pengajuan-card">
      <div className="flex justify-between items-start" style={{ marginBottom: '12px' }}>
        <div>
          <h3 className="text-h3">{p.nama_usaha}</h3>
          <div className="flex items-center gap-sm" style={{ marginTop: '4px' }}>
            <span className="text-body-sm" style={{ color: 'var(--text-secondary)' }}>
              <span className="material-icons-round" style={{ fontSize: '14px', verticalAlign: 'middle' }}>person</span>
              {' '}{p.user?.name || 'Unknown'}
            </span>
            <span className="text-body-sm" style={{ color: 'var(--text-muted)' }}>
              {p.user?.email || ''}
            </span>
          </div>
        </div>
        <span className={`pengajuan-status ${sc.class}`}>{sc.label}</span>
      </div>

      <div style={{ marginBottom: '12px' }}>
        <p className="text-label" style={{ color: 'var(--text-secondary)', marginBottom: '2px' }}>Deskripsi:</p>
        <p className="text-body-sm">{p.deskripsi}</p>
      </div>

      <div style={{ marginBottom: '12px' }}>
        <p className="text-label" style={{ color: 'var(--text-secondary)', marginBottom: '2px' }}>Alasan:</p>
        <p className="text-body-sm">{p.alasan}</p>
      </div>

      {p.catatan_admin && (
        <div style={{ background: 'rgba(var(--primary-rgb),0.05)', borderRadius: 'var(--radius-sm)', padding: '12px', marginBottom: '12px' }}>
          <p className="text-label" style={{ color: 'var(--text-secondary)', marginBottom: '2px' }}>Catatan Admin:</p>
          <p className="text-body-sm">{p.catatan_admin}</p>
        </div>
      )}

      {p.created_at && (
        <p className="text-caption" style={{ color: 'var(--text-muted)', marginBottom: '12px' }}>
          Diajukan: {new Date(p.created_at).toLocaleDateString('id-ID', { day: 'numeric', month: 'long', year: 'numeric' })}
        </p>
      )}

      {p.status === 'pending' && (
        <div className="flex gap-sm">
          <button className="btn btn-sm btn-primary" onClick={() => onApprove(p.id)}>
            <span className="material-icons-round">check</span> Setujui
          </button>
          <button className="btn btn-sm btn-danger" onClick={() => onReject(p.id)}>
            <span className="material-icons-round">close</span> Tolak
          </button>
        </div>
      )}
    </div>
  );
}
