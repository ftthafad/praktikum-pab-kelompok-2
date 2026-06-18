import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import * as API from '../services/api';
import { useAuth } from '../contexts/AuthContext';
import { useToast } from '../contexts/ToastContext';

export default function PengajuanPage() {
  const navigate = useNavigate();
  const { isLoggedIn, token } = useAuth();
  const toast = useToast();

  const [pengajuan, setPengajuan] = useState(null);
  const [loading, setLoading] = useState(true);
  const [showForm, setShowForm] = useState(false);

  // Form state
  const [namaUsaha, setNamaUsaha] = useState('');
  const [deskripsi, setDeskripsi] = useState('');
  const [alasan, setAlasan] = useState('');
  const [error, setError] = useState('');
  const [submitting, setSubmitting] = useState(false);

  useEffect(() => {
    if (!isLoggedIn) {
      navigate('/login');
      return;
    }

    async function checkStatus() {
      try {
        const res = await API.getPengajuanStatus(token);
        if (res.data) {
          setPengajuan(res.data);
        } else {
          setShowForm(true);
        }
      } catch (e) {
        setShowForm(true);
      } finally {
        setLoading(false);
      }
    }
    checkStatus();
  }, [isLoggedIn, token, navigate]);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setSubmitting(true);

    try {
      await API.submitPengajuan(namaUsaha.trim(), deskripsi.trim(), alasan.trim(), token);
      toast.success('Pengajuan berhasil dikirim!');
      // Reload to show status
      const res = await API.getPengajuanStatus(token);
      setPengajuan(res.data);
      setShowForm(false);
    } catch (err) {
      setError(err.message || 'Gagal mengirim pengajuan');
      setSubmitting(false);
    }
  };

  const statusConfig = {
    pending: { icon: 'hourglass_empty', color: 'var(--pending)', bg: 'var(--pending-light)', label: 'Menunggu Persetujuan', text: 'Pengajuanmu sedang ditinjau oleh admin. Kami akan memberitahu hasilnya.' },
    approved: { icon: 'check_circle', color: 'var(--success)', bg: 'var(--success-light)', label: 'Disetujui', text: 'Selamat! Pengajuanmu telah disetujui. Kamu sekarang bisa mengelola wisata.' },
    rejected: { icon: 'cancel', color: 'var(--error)', bg: 'var(--error-light)', label: 'Ditolak', text: 'Maaf, pengajuanmu ditolak.' },
  };

  return (
    <div className="page-wrapper page-enter">
      <div className="form-page">
        <div className="form-page-title">
          <button className="btn btn-icon btn-ghost" onClick={() => navigate(-1)}>
            <span className="material-icons-round">arrow_back</span>
          </button>
          Ajukan Jadi Pengelola
        </div>

        <div>
          {loading ? (
            <div className="page-loader"><div className="spinner"></div></div>
          ) : pengajuan && !showForm ? (
            <>
              <div className="pengajuan-status-card">
                <div className="pengajuan-status-icon" style={{ background: statusConfig[pengajuan.status]?.bg }}>
                  <span className="material-icons-round" style={{ color: statusConfig[pengajuan.status]?.color }}>
                    {statusConfig[pengajuan.status]?.icon}
                  </span>
                </div>
                <h3 className="text-h3" style={{ marginBottom: '8px' }}>{statusConfig[pengajuan.status]?.label}</h3>
                <p className="text-body" style={{ color: 'var(--text-secondary)', marginBottom: '16px' }}>
                  {statusConfig[pengajuan.status]?.text}
                </p>

                {pengajuan.catatan_admin && (
                  <div style={{ background: 'rgba(var(--primary-rgb),0.05)', borderRadius: 'var(--radius-md)', padding: '16px', textAlign: 'left', marginTop: '8px' }}>
                    <p className="text-label" style={{ color: 'var(--text-secondary)', marginBottom: '4px' }}>Catatan Admin:</p>
                    <p className="text-body" style={{ color: 'var(--text-primary)' }}>{pengajuan.catatan_admin}</p>
                  </div>
                )}

                <div style={{ marginTop: '20px' }}>
                  <div style={{ textAlign: 'left', display: 'grid', gap: '8px' }}>
                    <div><span className="text-label" style={{ color: 'var(--text-secondary)' }}>Nama Usaha:</span> <span className="text-body">{pengajuan.nama_usaha}</span></div>
                    <div><span className="text-label" style={{ color: 'var(--text-secondary)' }}>Deskripsi:</span> <span className="text-body">{pengajuan.deskripsi}</span></div>
                    <div><span className="text-label" style={{ color: 'var(--text-secondary)' }}>Alasan:</span> <span className="text-body">{pengajuan.alasan}</span></div>
                  </div>
                </div>
              </div>

              <button className="btn btn-ghost w-full" onClick={() => navigate(-1)}>
                <span className="material-icons-round">arrow_back</span> Kembali ke Profil
              </button>
            </>
          ) : (
            <div className="detail-card" style={{ marginBottom: '24px' }}>
              <div style={{ display: 'flex', alignItems: 'center', gap: '12px', marginBottom: '16px' }}>
                <div style={{ width: '48px', height: '48px', borderRadius: 'var(--radius-md)', background: 'rgba(var(--primary-rgb),0.1)', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
                  <span className="material-icons-round" style={{ color: 'var(--primary)', fontSize: '24px' }}>store</span>
                </div>
                <div>
                  <h3 className="text-h3">Form Pengajuan</h3>
                  <p className="text-body-sm" style={{ color: 'var(--text-secondary)' }}>Isi data untuk mengajukan diri sebagai pengelola wisata</p>
                </div>
              </div>

              <form onSubmit={handleSubmit}>
                <div className="form-group">
                  <label className="form-label" htmlFor="peng-nama">Nama Usaha</label>
                  <input type="text" className="form-input" id="peng-nama" placeholder="Nama usaha wisata Anda" required
                    value={namaUsaha} onChange={(e) => setNamaUsaha(e.target.value)} />
                </div>

                <div className="form-group">
                  <label className="form-label" htmlFor="peng-deskripsi">Deskripsi Usaha</label>
                  <textarea className="form-input form-textarea" id="peng-deskripsi" placeholder="Jelaskan usaha wisata Anda..." required rows="4"
                    value={deskripsi} onChange={(e) => setDeskripsi(e.target.value)} />
                </div>

                <div className="form-group">
                  <label className="form-label" htmlFor="peng-alasan">Alasan Pengajuan</label>
                  <textarea className="form-input form-textarea" id="peng-alasan" placeholder="Mengapa Anda ingin menjadi pengelola?" required rows="3"
                    value={alasan} onChange={(e) => setAlasan(e.target.value)} />
                </div>

                {error && <div className="form-error" style={{ marginBottom: '12px', display: 'block' }}>{error}</div>}

                <button type="submit" className="btn btn-primary btn-lg w-full" disabled={submitting}>
                  {submitting ? (
                    <><div className="spinner" style={{ width: '20px', height: '20px', borderWidth: '2px' }}></div> Mengirim...</>
                  ) : (
                    <><span className="material-icons-round">send</span> Kirim Pengajuan</>
                  )}
                </button>
              </form>
            </div>
          )}
        </div>
      </div>
    </div>
  );
}
