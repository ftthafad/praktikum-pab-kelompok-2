import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import * as API from '../services/api';
import { useAuth } from '../contexts/AuthContext';

export default function NotifikasiPage() {
  const navigate = useNavigate();
  const { isLoggedIn, token } = useAuth();
  const [pengajuan, setPengajuan] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    if (!isLoggedIn) {
      navigate('/login');
      return;
    }

    async function load() {
      try {
        const res = await API.getPengajuanStatus(token);
        setPengajuan(res.data);
      } catch (err) {
        // No pengajuan found
      } finally {
        setLoading(false);
      }
    }
    load();
  }, [isLoggedIn, token, navigate]);

  const statusConfig = {
    approved: { icon: 'check_circle', color: 'var(--success)', label: 'Disetujui', message: 'Selamat! Pengajuanmu telah disetujui. Kamu sekarang bisa menambahkan destinasi wisata.' },
    rejected: { icon: 'cancel', color: 'var(--error)', label: 'Ditolak', message: 'Maaf, pengajuanmu ditolak. Kamu bisa mengajukan kembali melalui halaman Profil.' },
    pending: { icon: 'hourglass_empty', color: 'var(--pending)', label: 'Menunggu Review', message: 'Pengajuanmu sedang dalam proses peninjauan oleh admin. Mohon tunggu.' },
  };

  return (
    <div className="page-enter">
      <div className="notifikasi-header">
        <button className="btn-icon" onClick={() => navigate(-1)} style={{ color: 'var(--white)' }}>
          <span className="material-icons-round">arrow_back</span>
        </button>
        <h2>Notifikasi</h2>
      </div>

      <div className="notifikasi-body">
        {loading ? (
          <div className="page-loader"><div className="spinner"></div></div>
        ) : !pengajuan ? (
          <div className="empty-state" style={{ paddingTop: '80px' }}>
            <div className="empty-state-icon">🔔</div>
            <div className="empty-state-title">Belum ada notifikasi</div>
            <p className="empty-state-text">Kamu belum pernah mengajukan pendaftaran pengelola</p>
            <button className="btn btn-primary" onClick={() => navigate('/pengajuan')} style={{ marginTop: '16px' }}>
              Ajukan Sekarang
            </button>
          </div>
        ) : (
          <div>
            <div style={{ marginBottom: '12px' }}>
              <div style={{ fontSize: '1rem', fontWeight: 700, color: 'var(--text-primary)' }}>Status Pengajuan Pengelola</div>
            </div>

            <div className="notifikasi-status-card">
              <div className="notifikasi-status-header">
                <span className="material-icons-round" style={{ color: statusConfig[pengajuan.status]?.color }}>
                  {statusConfig[pengajuan.status]?.icon}
                </span>
                <div>
                  <div className="notifikasi-status-label" style={{ color: statusConfig[pengajuan.status]?.color }}>
                    {statusConfig[pengajuan.status]?.label}
                  </div>
                  {pengajuan.created_at && (
                    <div className="notifikasi-status-date">{pengajuan.created_at.substring(0, 10)}</div>
                  )}
                </div>
              </div>

              <div className="divider"></div>

              <div className="notifikasi-info-row">
                <div className="notifikasi-info-label">Nama Usaha</div>
                <div className="notifikasi-info-value">{pengajuan.nama_usaha}</div>
              </div>
              <div className="notifikasi-info-row">
                <div className="notifikasi-info-label">Deskripsi</div>
                <div className="notifikasi-info-value">{pengajuan.deskripsi}</div>
              </div>
              <div className="notifikasi-info-row">
                <div className="notifikasi-info-label">Alasan</div>
                <div className="notifikasi-info-value">{pengajuan.alasan}</div>
              </div>

              {pengajuan.status === 'rejected' && pengajuan.catatan_admin && (
                <div className="notifikasi-admin-note">
                  <div className="notifikasi-admin-note-label">Catatan Admin:</div>
                  <div className="notifikasi-admin-note-text">{pengajuan.catatan_admin}</div>
                </div>
              )}

              <div className="notifikasi-status-msg">
                {statusConfig[pengajuan.status]?.message}
              </div>
            </div>
          </div>
        )}
      </div>
    </div>
  );
}
