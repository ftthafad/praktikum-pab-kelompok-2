import { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import * as API from '../services/api';
import { useAuth } from '../contexts/AuthContext';
import { useToast } from '../contexts/ToastContext';
import LocationPickerMap from '../components/LocationPickerMap';

export default function FormWisataPage() {
  const { id: wisataId } = useParams();
  const navigate = useNavigate();
  const { isLoggedIn, token, user } = useAuth();
  const toast = useToast();

  const isEdit = !!wisataId;

  const [name, setName] = useState('');
  const [categoryId, setCategoryId] = useState('');
  const [description, setDescription] = useState('');
  const [location, setLocation] = useState('');
  const [latitude, setLatitude] = useState('');
  const [longitude, setLongitude] = useState('');
  const [price, setPrice] = useState('');
  const [hours, setHours] = useState('');
  const [categories, setCategories] = useState([]);
  const [photos, setPhotos] = useState([]);
  const [error, setError] = useState('');
  const [submitting, setSubmitting] = useState(false);
  const [uploading, setUploading] = useState(false);

  useEffect(() => {
    if (!isLoggedIn || user?.role !== 'pengelola') {
      navigate('/profile');
      return;
    }

    async function loadData() {
      // Load categories
      try {
        const catRes = await API.getCategories();
        setCategories(catRes.data || []);
      } catch (e) { /* ignore */ }

      // If editing, load existing data
      if (isEdit) {
        try {
          const res = await API.getWisataDetail(wisataId);
          const w = res.data;
          if (w) {
            setName(w.name || '');
            setCategoryId(String(w.category_id || ''));
            setDescription(w.description || '');
            setLocation(w.location || '');
            setLatitude(w.latitude || '');
            setLongitude(w.longitude || '');
            setPrice(w.price || '');
            setHours(w.opening_hours || '');
            setPhotos(w.photos || []);
          }
        } catch (e) {
          toast.error('Gagal memuat data wisata');
        }
      }
    }
    loadData();
  }, [isLoggedIn, user, wisataId, isEdit, navigate, token, toast]);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setSubmitting(true);

    const data = {
      name: name.trim(),
      category_id: parseInt(categoryId),
      description: description.trim(),
      location: location.trim(),
      latitude: parseFloat(latitude) || null,
      longitude: parseFloat(longitude) || null,
      price: price.trim(),
      opening_hours: hours.trim(),
    };

    try {
      if (isEdit) {
        await API.updateWisata(wisataId, data, token);
        toast.success('Wisata berhasil diperbarui!');
      } else {
        await API.tambahWisata(data, token);
        toast.success('Wisata berhasil ditambahkan!');
      }
      navigate('/pengelola');
    } catch (err) {
      setError(err.message || 'Gagal menyimpan wisata');
      setSubmitting(false);
    }
  };

  const handlePhotoUpload = async (e) => {
    const file = e.target.files[0];
    if (!file) return;

    setUploading(true);
    try {
      await API.uploadFotoWisata(wisataId, file, token);
      toast.success('Foto berhasil diupload');
      // Reload photos
      const res = await API.getWisataDetail(wisataId);
      setPhotos(res.data?.photos || []);
    } catch (err) {
      toast.error(err.message || 'Gagal upload foto');
    } finally {
      setUploading(false);
      e.target.value = '';
    }
  };

  const handleDeletePhoto = async (photoId) => {
    try {
      await API.deleteFotoWisata(wisataId, photoId, token);
      toast.success('Foto dihapus');
      const res = await API.getWisataDetail(wisataId);
      setPhotos(res.data?.photos || []);
    } catch (err) {
      toast.error(err.message || 'Gagal menghapus foto');
    }
  };

  return (
    <div className="page-wrapper page-enter">
      <div className="form-page">
        <div className="form-page-title">
          <button className="btn btn-icon btn-ghost" onClick={() => navigate('/pengelola')}>
            <span className="material-icons-round">arrow_back</span>
          </button>
          {isEdit ? 'Edit Wisata' : 'Tambah Wisata Baru'}
        </div>

        <div className="detail-card">
          <form onSubmit={handleSubmit}>
            <div className="form-group">
              <label className="form-label" htmlFor="fw-name">Nama Wisata *</label>
              <input type="text" className="form-input" id="fw-name" placeholder="Nama destinasi wisata" required
                value={name} onChange={(e) => setName(e.target.value)} />
            </div>

            <div className="form-group">
              <label className="form-label" htmlFor="fw-category">Kategori *</label>
              <select className="form-input form-select" id="fw-category" required
                value={categoryId} onChange={(e) => setCategoryId(e.target.value)}>
                <option value="">Pilih Kategori</option>
                {categories.map(c => (
                  <option key={c.id} value={c.id}>{c.name}</option>
                ))}
              </select>
            </div>

            <div className="form-group">
              <label className="form-label" htmlFor="fw-description">Deskripsi *</label>
              <textarea className="form-input form-textarea" id="fw-description" placeholder="Deskripsi lengkap wisata..." required rows="5"
                value={description} onChange={(e) => setDescription(e.target.value)} />
            </div>

            <div className="form-group">
              <label className="form-label" htmlFor="fw-location">Lokasi *</label>
              <input type="text" className="form-input" id="fw-location" placeholder="Alamat atau lokasi wisata" required
                value={location} onChange={(e) => setLocation(e.target.value)} />
            </div>

            <div className="form-group">
              <LocationPickerMap
                latitude={latitude}
                longitude={longitude}
                onLocationSelect={(lat, lng) => {
                  setLatitude(String(lat));
                  setLongitude(String(lng));
                }}
              />
            </div>

            <div className="form-row">
              <div className="form-group">
                <label className="form-label" htmlFor="fw-price">Harga Tiket *</label>
                <input type="text" className="form-input" id="fw-price" placeholder="Rp 25.000" required
                  value={price} onChange={(e) => setPrice(e.target.value)} />
              </div>
              <div className="form-group">
                <label className="form-label" htmlFor="fw-hours">Jam Operasional</label>
                <input type="text" className="form-input" id="fw-hours" placeholder="08:00 - 17:00"
                  value={hours} onChange={(e) => setHours(e.target.value)} />
              </div>
            </div>

            {isEdit && (
              <>
                <div className="divider" style={{ margin: '24px 0' }}></div>
                <h3 className="detail-section-title">Foto Wisata</h3>

                <div className="photo-preview-grid" style={{ marginBottom: '12px' }}>
                  {photos.map(p => (
                    <div key={p.id} className="photo-preview-item">
                      <img src={p.photo_url} alt="Photo" loading="lazy" />
                      <div className="photo-preview-remove" onClick={() => handleDeletePhoto(p.id)} title="Hapus foto">
                        <span className="material-icons-round" style={{ fontSize: '14px' }}>close</span>
                      </div>
                    </div>
                  ))}
                </div>

                <div className="file-upload-area" onClick={() => document.getElementById('fw-photo').click()}>
                  <span className="material-icons-round">cloud_upload</span>
                  <p>Klik untuk upload foto</p>
                  <input type="file" id="fw-photo" accept="image/*" style={{ display: 'none' }}
                    onChange={handlePhotoUpload} />
                </div>
                {uploading && (
                  <div className="flex items-center gap-sm" style={{ marginTop: '8px' }}>
                    <div className="spinner" style={{ width: '16px', height: '16px', borderWidth: '2px' }}></div> Mengupload...
                  </div>
                )}
              </>
            )}

            {error && <div className="form-error" style={{ margin: '16px 0', display: 'block' }}>{error}</div>}

            <button type="submit" className="btn btn-primary btn-lg w-full" disabled={submitting} style={{ marginTop: '20px' }}>
              {submitting ? (
                <><div className="spinner" style={{ width: '20px', height: '20px', borderWidth: '2px' }}></div> Menyimpan...</>
              ) : (
                <><span className="material-icons-round">{isEdit ? 'save' : 'add'}</span> {isEdit ? 'Simpan Perubahan' : 'Tambah Wisata'}</>
              )}
            </button>
          </form>
        </div>
      </div>
    </div>
  );
}
