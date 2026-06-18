import { useState, useEffect, useMemo } from 'react';
import * as API from '../services/api';
import { WisataCard, SkeletonCards } from '../components/WisataCard';

export default function ExplorePage() {
  const [allWisata, setAllWisata] = useState([]);
  const [categories, setCategories] = useState([]);
  const [selectedCategoryId, setSelectedCategoryId] = useState(null);
  const [searchQuery, setSearchQuery] = useState('');
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    async function loadData() {
      try {
        const [catRes, wisataRes] = await Promise.all([
          API.getCategories().catch(() => ({ data: [] })),
          API.getWisata().catch(() => ({ data: [] }))
        ]);
        setCategories(catRes.data || []);
        setAllWisata(wisataRes.data || []);
      } catch (err) {
        console.error('Explore load failed:', err);
        setError(err);
      } finally {
        setLoading(false);
      }
    }
    loadData();
  }, []);

  const filtered = useMemo(() => {
    let result = [...allWisata];

    if (selectedCategoryId !== null) {
      result = result.filter(w => w.category_id === selectedCategoryId);
    }

    if (searchQuery.trim()) {
      const q = searchQuery.toLowerCase();
      result = result.filter(w =>
        w.name.toLowerCase().includes(q) ||
        w.location.toLowerCase().includes(q) ||
        (w.category?.name || '').toLowerCase().includes(q)
      );
    }

    return result;
  }, [allWisata, selectedCategoryId, searchQuery]);

  return (
    <div className="page-wrapper page-enter">
      {/* Search Hero */}
      <div className="search-hero">
        <div className="container">
          <h2 style={{ color: 'var(--white)', fontWeight: 700, marginBottom: '12px' }}>
            <span className="material-icons-round" style={{ verticalAlign: 'middle', marginRight: '4px' }}>explore</span>
            Jelajahi Wisata
          </h2>
          <div className="search-input-wrapper">
            <span className="material-icons-round">search</span>
            <input
              type="text"
              placeholder="Cari destinasi wisata..."
              autoComplete="off"
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
            />
          </div>
        </div>
      </div>

      <div className="container">
        {/* Category Filters */}
        <div className="chips-row" style={{ padding: '16px 0' }}>
          <span
            className={`chip ${selectedCategoryId === null ? 'chip-active' : 'chip-default'}`}
            onClick={() => setSelectedCategoryId(null)}
          >
            Semua
          </span>
          {categories.map(c => (
            <span
              key={c.id}
              className={`chip ${selectedCategoryId === c.id ? 'chip-active' : 'chip-default'}`}
              onClick={() => setSelectedCategoryId(c.id)}
            >
              {c.name}
            </span>
          ))}
        </div>

        {/* Result count */}
        {!loading && (
          <p className="text-body-sm" style={{ color: 'var(--text-secondary)', marginBottom: '12px' }}>
            {filtered.length} destinasi ditemukan
          </p>
        )}

        {/* Wisata Grid */}
        <div className="wisata-grid" style={{ marginBottom: '40px' }}>
          {loading ? (
            <SkeletonCards count={6} />
          ) : error ? (
            <div className="empty-state" style={{ gridColumn: '1/-1' }}>
              <div className="empty-state-icon">😕</div>
              <div className="empty-state-title">Gagal memuat data</div>
              <p className="empty-state-text">Periksa koneksi internet Anda</p>
            </div>
          ) : filtered.length > 0 ? (
            filtered.map(w => <WisataCard key={w.id} wisata={w} />)
          ) : (
            <div className="empty-state" style={{ gridColumn: '1/-1' }}>
              <div className="empty-state-icon">😕</div>
              <div className="empty-state-title">Wisata tidak ditemukan</div>
              <p className="empty-state-text">Coba ubah kata kunci atau filter kategori</p>
            </div>
          )}
        </div>
      </div>
    </div>
  );
}
