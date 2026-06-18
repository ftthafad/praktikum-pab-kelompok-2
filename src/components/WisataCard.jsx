import { useNavigate } from 'react-router-dom';

function esc(str) {
  if (!str) return '';
  return str;
}

function getImageUrl(wisata) {
  if (wisata.cover_photo?.photo_url) return wisata.cover_photo.photo_url;
  if (wisata.photos && wisata.photos.length > 0) return wisata.photos[0].photo_url;
  return null;
}

export function WisataCard({ wisata }) {
  const navigate = useNavigate();
  const imageUrl = getImageUrl(wisata);

  return (
    <div className="wisata-card card-interactive" onClick={() => navigate(`/detail/${wisata.id}`)} id={`wisata-card-${wisata.id}`}>
      <div style={{ position: 'relative' }}>
        {imageUrl ? (
          <img
            src={imageUrl}
            alt={esc(wisata.name)}
            className="wisata-card-img"
            loading="lazy"
            onError={(e) => {
              e.target.parentElement.innerHTML = '<div class="wisata-card-img-placeholder"><span class="material-icons-round">landscape</span></div>';
            }}
          />
        ) : (
          <div className="wisata-card-img-placeholder">
            <span className="material-icons-round">landscape</span>
          </div>
        )}
        <span className="wisata-card-badge">{esc(wisata.category?.name || 'Wisata')}</span>
      </div>
      <div className="wisata-card-body">
        <div className="wisata-card-name">{esc(wisata.name)}</div>
        <div className="wisata-card-location">
          <span className="material-icons-round">location_on</span>
          {esc(wisata.location)}
        </div>
        <div className="wisata-card-footer">
          <span className="wisata-card-rating">
            <span className="material-icons-round">star</span>
            {Number(wisata.rating).toFixed(1)}
          </span>
          <span className="wisata-card-price">{esc(wisata.price)}</span>
        </div>
      </div>
    </div>
  );
}

export function WisataListCard({ wisata }) {
  const navigate = useNavigate();
  const imageUrl = getImageUrl(wisata);

  return (
    <div className="wisata-list-card card-interactive" onClick={() => navigate(`/detail/${wisata.id}`)} id={`wisata-list-${wisata.id}`}>
      {imageUrl ? (
        <img
          src={imageUrl}
          alt={esc(wisata.name)}
          className="wisata-list-card-img"
          loading="lazy"
          onError={(e) => { e.target.style.background = 'linear-gradient(135deg,#9CD5FF,#7AAACE)'; e.target.src = ''; }}
        />
      ) : (
        <div className="wisata-list-card-img" style={{ background: 'linear-gradient(135deg,#9CD5FF,#7AAACE)', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
          <span className="material-icons-round" style={{ fontSize: '32px', color: 'var(--primary)', opacity: 0.5 }}>landscape</span>
        </div>
      )}
      <div className="wisata-list-card-body">
        <div className="wisata-card-name">{esc(wisata.name)}</div>
        <div className="wisata-card-location">
          <span className="material-icons-round">location_on</span>
          {esc(wisata.location)}
        </div>
        <div className="wisata-card-footer">
          <span className="wisata-card-rating">
            <span className="material-icons-round">star</span>
            {Number(wisata.rating).toFixed(1)}
            <span style={{ color: 'var(--text-muted)', fontWeight: 400, marginLeft: '2px' }}>({wisata.review_count || 0})</span>
          </span>
          <span className="wisata-card-price">{esc(wisata.price)}</span>
        </div>
      </div>
    </div>
  );
}

export function SkeletonCards({ count = 4 }) {
  return Array.from({ length: count }).map((_, i) => (
    <div className="wisata-card" style={{ pointerEvents: 'none' }} key={i}>
      <div className="skeleton skeleton-img"></div>
      <div style={{ padding: '14px' }}>
        <div className="skeleton skeleton-text" style={{ width: '80%' }}></div>
        <div className="skeleton skeleton-text" style={{ width: '50%' }}></div>
        <div className="skeleton skeleton-text" style={{ width: '60%', marginTop: '8px' }}></div>
      </div>
    </div>
  ));
}

export function SkeletonList({ count = 4 }) {
  return Array.from({ length: count }).map((_, i) => (
    <div className="wisata-list-card" style={{ pointerEvents: 'none' }} key={i}>
      <div className="skeleton" style={{ width: '140px', minWidth: '140px', height: '110px' }}></div>
      <div style={{ padding: '14px', flex: 1 }}>
        <div className="skeleton skeleton-text" style={{ width: '70%' }}></div>
        <div className="skeleton skeleton-text" style={{ width: '45%' }}></div>
        <div className="skeleton skeleton-text" style={{ width: '55%', marginTop: '12px' }}></div>
      </div>
    </div>
  ));
}
