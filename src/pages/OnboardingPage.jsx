import { useState, useEffect, useCallback } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';

const pages = [
  {
    title: 'Jelajahi Wisata Jawa Tengah',
    description: 'Temukan ratusan destinasi wisata terbaik di Jawa Tengah dalam genggaman tanganmu.',
    imageUrl: 'https://images.unsplash.com/photo-1596402184320-417e7178b2cd?w=800',
  },
  {
    title: 'Informasi Lengkap & Terpercaya',
    description: 'Dapatkan info harga tiket, jam operasional, fasilitas, dan lokasi wisata secara akurat.',
    imageUrl: 'https://images.unsplash.com/photo-1555400038-63f5ba517a47?w=800',
  },
  {
    title: 'Rencanakan Perjalananmu',
    description: 'Simpan destinasi favorit, baca ulasan wisatawan, dan bagikan pengalamanmu.',
    imageUrl: 'https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=800',
  },
];

export default function OnboardingPage() {
  const navigate = useNavigate();
  const { setOnboardingSeen } = useAuth();
  const [currentPage, setCurrentPage] = useState(0);
  const [touchStartX, setTouchStartX] = useState(0);

  const finish = useCallback(() => {
    setOnboardingSeen();
    navigate('/login');
  }, [setOnboardingSeen, navigate]);

  const goTo = (idx) => {
    setCurrentPage(idx);
  };

  const handleNext = () => {
    if (currentPage < pages.length - 1) {
      goTo(currentPage + 1);
    } else {
      finish();
    }
  };

  const handleTouchStart = (e) => {
    setTouchStartX(e.touches[0].clientX);
  };

  const handleTouchEnd = (e) => {
    const dx = e.changedTouches[0].clientX - touchStartX;
    if (Math.abs(dx) > 60) {
      if (dx < 0 && currentPage < pages.length - 1) goTo(currentPage + 1);
      if (dx > 0 && currentPage > 0) goTo(currentPage - 1);
    }
  };

  return (
    <div
      className="onboarding"
      onTouchStart={handleTouchStart}
      onTouchEnd={handleTouchEnd}
    >
      {pages.map((page, i) => (
        <div key={i} className={`onboarding-slide ${i === currentPage ? 'active' : ''}`} data-slide={i}>
          <img src={page.imageUrl} alt={page.title} loading={i === 0 ? 'eager' : 'lazy'} />
          <div className="onboarding-slide-overlay"></div>
          <div className="onboarding-slide-content">
            <div className="onboarding-slide-brand">Travel Waka</div>
            <h1 className="onboarding-slide-title">{page.title}</h1>
            <p className="onboarding-slide-desc">{page.description}</p>
          </div>
        </div>
      ))}

      <button className="onboarding-skip" onClick={finish}>Skip</button>

      <div className="onboarding-controls">
        <div className="onboarding-dots">
          {pages.map((_, i) => (
            <div
              key={i}
              className={`onboarding-dot ${i === currentPage ? 'active' : ''}`}
              onClick={() => goTo(i)}
            ></div>
          ))}
        </div>
        <button className="onboarding-btn" onClick={handleNext}>
          {currentPage === pages.length - 1 ? 'Mulai Jelajahi' : 'Selanjutnya'}
        </button>
      </div>
    </div>
  );
}
