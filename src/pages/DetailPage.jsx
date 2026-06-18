import { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { MapContainer, TileLayer, Marker, Popup } from 'react-leaflet';
import * as API from '../services/api';
import { useAuth } from '../contexts/AuthContext';
import { useToast } from '../contexts/ToastContext';
import { useModal } from '../contexts/ModalContext';
import RatingDisplay from '../components/RatingDisplay';

export default function DetailPage() {
  const { id } = useParams();
  const navigate = useNavigate();
  const { isLoggedIn, token } = useAuth();
  const toast = useToast();
  const modal = useModal();

  const [wisata, setWisata] = useState(null);
  const [reviews, setReviews] = useState([]);
  const [isBookmarked, setIsBookmarked] = useState(false);
  const [currentPhotoIndex, setCurrentPhotoIndex] = useState(0);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    async function load() {
      try {
        const [detailRes, reviewsRes] = await Promise.all([
          API.getWisataDetail(id),
          API.getReviews(id).catch(() => ({ data: [] }))
        ]);

        setWisata(detailRes.data);
        setReviews(reviewsRes.data || []);

        if (detailRes.data && isLoggedIn) {
          try {
            const bRes = await API.checkBookmark(id, token);
            setIsBookmarked(bRes.is_bookmarked || false);
          } catch (e) { /* ignore */ }
        }
      } catch (err) {
        console.error('Detail load failed:', err);
        setError(err);
      } finally {
        setLoading(false);
      }
    }
    load();
  }, [id, isLoggedIn, token]);

  const handleBookmark = async () => {
    if (!isLoggedIn) {
      modal.show({
        icon: 'bookmark_border',
        iconClass: 'modal-icon-info',
        title: 'Login untuk Menyimpan',
        body: 'Kamu perlu login terlebih dahulu untuk menyimpan wisata favoritmu.',
        confirmText: 'Login Sekarang',
        cancelText: 'Nanti Saja',
        onConfirm: () => navigate('/login'),
      });
      return;
    }

    try {
      await API.toggleBookmark(wisata.id, token);
      const newState = !isBookmarked;
      setIsBookmarked(newState);
      toast.success(newState ? 'Disimpan ke bookmark!' : 'Dihapus dari bookmark');
    } catch (err) {
      toast.error('Gagal mengubah bookmark');
    }
  };

  const handleShare = () => {
    if (!wisata) return;
    const shareText = `🌿 ${wisata.name}\n📍 ${wisata.location}\n💰 ${wisata.price}\n⭐ ${wisata.rating}\n\nTemukan wisata menarik di Jawa Tengah dengan TravelWaka!`;

    if (navigator.share) {
      navigator.share({ title: wisata.name, text: shareText });
    } else {
      navigator.clipboard.writeText(shareText).then(() => {
        toast.success('Link berhasil disalin!');
      });
    }
  };

  const handleWriteReview = () => {
    if (!isLoggedIn) {
      modal.show({
        icon: 'bookmark_border',
        iconClass: 'modal-icon-info',
        title: 'Login untuk Menulis Ulasan',
        body: 'Kamu perlu login terlebih dahulu untuk menulis ulasan.',
        confirmText: 'Login Sekarang',
        cancelText: 'Nanti Saja',
        onConfirm: () => navigate('/login'),
      });
      return;
    }
    navigate(`/review/${id}`);
  };

  if (loading) {
    return (
      <div className="page-wrapper">
        <div className="page-loader"><div className="spinner"></div></div>
      </div>
    );
  }

  if (error || !wisata) {
    return (
      <div className="page-wrapper">
        <div className="empty-state" style={{ paddingTop: '120px' }}>
          <div className="empty-state-icon">😕</div>
          <div className="empty-state-title">{error ? 'Gagal memuat data' : 'Wisata tidak ditemukan'}</div>
          <p className="empty-state-text">{error?.message || 'Periksa koneksi internet'}</p>
          <button className="btn btn-primary" onClick={() => navigate(-1)} style={{ marginTop: '16px' }}>
            <span className="material-icons-round">arrow_back</span> Kembali
          </button>
        </div>
      </div>
    );
  }

  const photos = wisata.photos || [];

  return (
    <div className="page-enter">
      {/* Photo Gallery */}
      <div className="detail-hero" id="detail-hero">
        {photos.length > 0 ? (
          photos.map((p, i) => (
            <div key={i} className={`hero-banner-slide ${i === currentPhotoIndex ? 'active' : ''}`}>
              <img src={p.photo_url} alt={`Photo ${i + 1}`} loading={i === 0 ? 'eager' : 'lazy'} />
            </div>
          ))
        ) : (
          <div className="detail-hero-placeholder">
            <span className="material-icons-round">landscape</span>
          </div>
        )}

        <div className="detail-hero-nav">
          <button className="detail-hero-btn" onClick={() => navigate(-1)}>
            <span className="material-icons-round">arrow_back</span>
          </button>
          <div style={{ display: 'flex', gap: '8px' }}>
            <button className="detail-hero-btn" onClick={handleBookmark}>
              <span className="material-icons-round">{isBookmarked ? 'bookmark' : 'bookmark_border'}</span>
            </button>
            <button className="detail-hero-btn" onClick={handleShare}>
              <span className="material-icons-round">share</span>
            </button>
          </div>
        </div>

        {photos.length > 1 && (
          <div className="detail-hero-dots">
            {photos.map((_, i) => (
              <div
                key={i}
                className={`detail-hero-dot ${i === currentPhotoIndex ? 'active' : ''}`}
                onClick={() => setCurrentPhotoIndex(i)}
              ></div>
            ))}
          </div>
        )}
      </div>

      {/* Content */}
      <div className="detail-content">
        <div className="container">
          {/* Main Info Card */}
          <div className="detail-card">
            <div className="flex justify-between items-start" style={{ marginBottom: '12px' }}>
              <div style={{ flex: 1 }}>
                <h1 className="text-h1" style={{ marginBottom: '4px' }}>{wisata.name}</h1>
                <div className="flex items-center gap-xs" style={{ color: 'var(--text-secondary)' }}>
                  <span className="material-icons-round" style={{ fontSize: '16px', color: 'var(--primary)' }}>location_on</span>
                  <span className="text-body-sm">{wisata.location}</span>
                </div>
              </div>
            </div>

            <div className="flex gap-sm" style={{ marginBottom: '20px', flexWrap: 'wrap' }}>
              <span className="info-chip info-chip-star">
                <span className="material-icons-round">star</span>
                {Number(wisata.rating).toFixed(1)}
              </span>
              <span className="info-chip info-chip-price">
                <span className="material-icons-round">confirmation_number</span>
                {wisata.price}
              </span>
              <span className="info-chip info-chip-category">
                <span className="material-icons-round">category</span>
                {wisata.category?.name || '-'}
              </span>
            </div>

            <div className="divider"></div>

            {/* Operating Hours */}
            <h3 className="detail-section-title">Jam Operasional</h3>
            <div className="flex items-center gap-xs" style={{ color: 'var(--text-secondary)', marginBottom: '16px' }}>
              <span className="material-icons-round" style={{ fontSize: '16px', color: 'var(--primary)' }}>access_time</span>
              <span className="text-body-sm">{wisata.opening_hours || 'Tidak tersedia'}</span>
            </div>

            <div className="divider"></div>

            {/* Description */}
            <h3 className="detail-section-title">Deskripsi</h3>
            <p className="text-body" style={{ color: 'var(--text-secondary)', marginBottom: '16px', whiteSpace: 'pre-wrap' }}>
              {wisata.description || '-'}
            </p>

            <div className="divider"></div>

            {/* Map */}
            <h3 className="detail-section-title">Lokasi</h3>
            {wisata.latitude && wisata.longitude ? (
              <>
                <div className="map-container" style={{ marginBottom: '12px' }}>
                  <MapContainer
                    center={[wisata.latitude, wisata.longitude]}
                    zoom={15}
                    style={{ height: '100%', width: '100%' }}
                    scrollWheelZoom={false}
                  >
                    <TileLayer
                      attribution='&copy; OpenStreetMap contributors'
                      url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
                    />
                    <Marker position={[wisata.latitude, wisata.longitude]}>
                      <Popup>
                        <b>{wisata.name}</b><br />{wisata.location}
                      </Popup>
                    </Marker>
                  </MapContainer>
                </div>
                <a
                  href={`https://www.google.com/maps/dir/?api=1&destination=${wisata.latitude},${wisata.longitude}`}
                  target="_blank"
                  rel="noopener noreferrer"
                  className="btn btn-outline w-full"
                  style={{ marginBottom: '8px' }}
                >
                  <span className="material-icons-round">directions</span>
                  Get Direction
                </a>
              </>
            ) : (
              <p className="text-body-sm" style={{ color: 'var(--text-muted)' }}>Lokasi belum tersedia</p>
            )}
          </div>

          {/* Reviews Section */}
          <div className="detail-card">
            <div className="flex justify-between items-center" style={{ marginBottom: '16px' }}>
              <h3 className="detail-section-title" style={{ margin: 0 }}>
                Ulasan ({reviews.length})
              </h3>
              <button className="btn btn-sm btn-secondary" onClick={handleWriteReview}>
                <span className="material-icons-round">edit</span> Tulis Ulasan
              </button>
            </div>

            <div style={{ display: 'flex', flexDirection: 'column', gap: '12px' }}>
              {reviews.length > 0 ? (
                reviews.map((r, i) => <ReviewCard key={i} review={r} />)
              ) : (
                <p className="text-body" style={{ color: 'var(--text-muted)', textAlign: 'center', padding: '24px 0' }}>
                  Belum ada ulasan. Jadilah yang pertama!
                </p>
              )}
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}

function ReviewCard({ review }) {
  const initial = review.user?.name ? review.user.name.charAt(0).toUpperCase() : '?';
  return (
    <div className="review-card">
      <div className="review-header">
        <div className="review-user">
          <div className="review-avatar">{initial}</div>
          <div>
            <div className="review-name">{review.user?.name || 'Pengguna'}</div>
            <div className="review-date">{review.created_at ? review.created_at.substring(0, 10) : ''}</div>
          </div>
        </div>
        <RatingDisplay rating={review.rating} />
      </div>
      {review.comment && <p className="review-comment">{review.comment}</p>}
    </div>
  );
}
