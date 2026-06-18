import { useState, useEffect, useRef, useMemo } from 'react';
import { Link } from 'react-router-dom';
import * as API from '../services/api';
import { WisataCard, WisataListCard, SkeletonCards, SkeletonList } from '../components/WisataCard';

const bannerImages = [
  'https://images.unsplash.com/photo-1596402184320-417e7178b2cd?w=1200&q=80',
  'https://images.unsplash.com/photo-1555400038-63f5ba517a47?w=1200&q=80',
  'https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=1200&q=80',
];

const bannerTexts = [
  { title: 'Destinasi Wisata Unggulan', sub: 'Temukan keindahan tersembunyi di Jawa Tengah' },
  { title: 'Petualangan Menanti', sub: 'Jelajahi alam, budaya, dan kuliner terbaik' },
  { title: 'Pengalaman Tak Terlupakan', sub: 'Buat kenangan indah bersama TravelWaka' },
];

export default function HomePage() {
  const [bannerIndex, setBannerIndex] = useState(0);
  const [categories, setCategories] = useState([]);
  const [wisataList, setWisataList] = useState([]);
  const [selectedCat, setSelectedCat] = useState('');
  const [loading, setLoading] = useState(true);
  const intervalRef = useRef(null);

  // Banner carousel
  useEffect(() => {
    intervalRef.current = setInterval(() => {
      setBannerIndex(prev => (prev + 1) % bannerImages.length);
    }, 5000);
    return () => clearInterval(intervalRef.current);
  }, []);

  const goToBanner = (idx) => {
    setBannerIndex(idx);
    clearInterval(intervalRef.current);
    intervalRef.current = setInterval(() => {
      setBannerIndex(prev => (prev + 1) % bannerImages.length);
    }, 5000);
  };

  // Load data
  useEffect(() => {
    async function loadData() {
      try {
        const [catRes, wisataRes] = await Promise.all([
          API.getCategories().catch(() => ({ data: [] })),
          API.getWisata().catch(() => ({ data: [] }))
        ]);
        setCategories(catRes.data || []);
        setWisataList(wisataRes.data || []);
      } catch (err) {
        console.error('Home data load failed:', err);
      } finally {
        setLoading(false);
      }
    }
    loadData();
  }, []);

  // Filtered data
  const filteredWisata = useMemo(() => {
    if (!selectedCat) return wisataList;
    return wisataList.filter(w => String(w.category_id) === selectedCat);
  }, [wisataList, selectedCat]);

  const popular = useMemo(() => {
    return [...filteredWisata].sort((a, b) => b.rating - a.rating).slice(0, 6);
  }, [filteredWisata]);

  return (
    <div className="page-wrapper page-enter">
      {/* Hero Banner */}
      <div className="hero-banner" id="hero-banner">
        {bannerImages.map((img, i) => (
          <div key={i} className={`hero-banner-slide ${i === bannerIndex ? 'active' : ''}`}>
            <img src={img} alt={`Banner ${i + 1}`} loading={i === 0 ? 'eager' : 'lazy'} />
            <div className="hero-banner-overlay"></div>
            <div className="hero-banner-content">
              <h1>{bannerTexts[i].title}</h1>
              <p>{bannerTexts[i].sub}</p>
            </div>
          </div>
        ))}
        <div className="hero-dots">
          {bannerImages.map((_, i) => (
            <div key={i} className={`hero-dot ${i === bannerIndex ? 'active' : ''}`} onClick={() => goToBanner(i)}></div>
          ))}
        </div>
      </div>

      <div className="container">
        {/* Categories */}
        <div style={{ marginTop: '32px' }}>
          <h2 className="section-title">Kategori</h2>
          <div className="chips-row" style={{ marginTop: '12px' }}>
            <span className={`chip ${selectedCat === '' ? 'chip-active' : 'chip-default'}`} onClick={() => setSelectedCat('')}>
              Semua
            </span>
            {loading ? (
              <>
                <span className="skeleton" style={{ width: '80px', height: '32px', borderRadius: '9999px' }}></span>
                <span className="skeleton" style={{ width: '80px', height: '32px', borderRadius: '9999px' }}></span>
                <span className="skeleton" style={{ width: '80px', height: '32px', borderRadius: '9999px' }}></span>
              </>
            ) : (
              categories.map(c => (
                <span
                  key={c.id}
                  className={`chip ${selectedCat === String(c.id) ? 'chip-active' : 'chip-default'}`}
                  onClick={() => setSelectedCat(String(c.id))}
                >
                  {c.name}
                </span>
              ))
            )}
          </div>
        </div>

        {/* Popular Wisata */}
        <div style={{ marginTop: '32px' }}>
          <div className="section-header">
            <h2 className="section-title">Wisata Populer</h2>
            <Link to="/explore" className="section-link">
              Lihat Semua <span className="material-icons-round">arrow_forward</span>
            </Link>
          </div>
          <div className="hscroll">
            {loading ? (
              <SkeletonCards count={4} />
            ) : popular.length > 0 ? (
              popular.map(w => <WisataCard key={w.id} wisata={w} />)
            ) : (
              <div className="empty-state"><p className="empty-state-text">Belum ada wisata</p></div>
            )}
          </div>
        </div>

        {/* All Wisata */}
        <div style={{ marginTop: '40px', marginBottom: '40px' }}>
          <h2 className="section-title" style={{ marginBottom: '16px' }}>Semua Wisata</h2>
          <div style={{ display: 'flex', flexDirection: 'column', gap: '12px' }}>
            {loading ? (
              <SkeletonList count={4} />
            ) : filteredWisata.length > 0 ? (
              filteredWisata.map(w => <WisataListCard key={w.id} wisata={w} />)
            ) : (
              <div className="empty-state">
                <div className="empty-state-icon">😕</div>
                <div className="empty-state-title">Belum ada wisata</div>
                <p className="empty-state-text">Wisata akan segera ditambahkan</p>
              </div>
            )}
          </div>
        </div>
      </div>
    </div>
  );
}
