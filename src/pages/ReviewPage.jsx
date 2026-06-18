import { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import * as API from '../services/api';
import { useAuth } from '../contexts/AuthContext';
import { useToast } from '../contexts/ToastContext';

const ratingLabels = ['', 'Sangat Buruk', 'Buruk', 'Cukup', 'Bagus', 'Luar Biasa!'];

export default function ReviewPage() {
  const { wisataId } = useParams();
  const navigate = useNavigate();
  const { isLoggedIn, token } = useAuth();
  const toast = useToast();

  const [selectedRating, setSelectedRating] = useState(0);
  const [reviewText, setReviewText] = useState('');
  const [existingReview, setExistingReview] = useState(null);
  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);

  useEffect(() => {
    if (!isLoggedIn) {
      navigate('/login');
      return;
    }

    async function checkExisting() {
      try {
        const checkRes = await API.checkReview(wisataId, token);
        if (checkRes.data) {
          setExistingReview(checkRes.data);
          setSelectedRating(checkRes.data.rating);
          setReviewText(checkRes.data.comment || '');
        }
      } catch (e) {
        // No existing review
      } finally {
        setLoading(false);
      }
    }
    checkExisting();
  }, [wisataId, isLoggedIn, token, navigate]);

  const handleSubmit = async () => {
    if (selectedRating === 0) return;

    setSubmitting(true);
    try {
      await API.submitReview(wisataId, selectedRating, reviewText.trim() || null, token);
      toast.success(existingReview ? 'Ulasan berhasil diperbarui!' : 'Ulasan berhasil dikirim!');
      navigate(-1);
    } catch (err) {
      toast.error(err.message || 'Gagal mengirim ulasan');
      setSubmitting(false);
    }
  };

  return (
    <div className="page-enter">
      <div className="review-page-header">
        <button className="btn-icon" onClick={() => navigate(-1)} style={{ color: 'var(--white)' }}>
          <span className="material-icons-round">arrow_back</span>
        </button>
        <h2>{existingReview ? 'Edit Ulasan' : 'Tulis Ulasan'}</h2>
      </div>

      <div className="review-page-body">
        {loading ? (
          <div className="page-loader"><div className="spinner"></div></div>
        ) : (
          <div>
            <div className="review-page-card">
              <h3 className="review-page-question">Bagaimana pengalamanmu?</h3>
              <p className="review-page-hint">Berikan rating dan ulasanmu untuk membantu wisatawan lain</p>

              <div className="review-page-stars">
                {[1, 2, 3, 4, 5].map(i => (
                  <span
                    key={i}
                    className={`material-icons-round star ${i <= selectedRating ? 'filled' : ''}`}
                    onClick={() => setSelectedRating(i)}
                  >
                    star
                  </span>
                ))}
              </div>
              <div
                className="review-page-rating-label"
                style={{ color: selectedRating > 0 ? 'var(--star)' : 'var(--text-secondary)' }}
              >
                {ratingLabels[selectedRating] || 'Pilih rating'}
              </div>

              <div className="divider"></div>

              <div className="form-group" style={{ textAlign: 'left', marginTop: '24px', marginBottom: 0 }}>
                <textarea
                  className="form-input form-textarea"
                  placeholder="Tulis ulasanmu..."
                  maxLength="500"
                  style={{ minHeight: '140px', resize: 'vertical' }}
                  value={reviewText}
                  onChange={(e) => setReviewText(e.target.value)}
                />
                <div className="review-page-counter">
                  <span>{reviewText.length}</span>/500
                </div>
              </div>
            </div>

            <button
              className="btn btn-primary btn-lg w-full"
              onClick={handleSubmit}
              disabled={selectedRating === 0 || submitting}
              style={{ marginBottom: '8px' }}
            >
              {submitting ? (
                <><div className="spinner" style={{ width: '20px', height: '20px', borderWidth: '2px' }}></div> Mengirim...</>
              ) : (
                existingReview ? 'Simpan Perubahan' : 'Kirim Ulasan'
              )}
            </button>
            <button className="btn btn-outline btn-lg w-full" onClick={() => navigate(-1)}>
              Batal
            </button>
          </div>
        )}
      </div>
    </div>
  );
}
