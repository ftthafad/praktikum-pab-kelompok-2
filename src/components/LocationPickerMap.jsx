import { useState, useEffect, useRef } from 'react';
import { MapContainer, TileLayer, Marker, useMapEvents, useMap } from 'react-leaflet';
import L from 'leaflet';

// Fix default marker icon issue in Leaflet + bundlers
delete L.Icon.Default.prototype._getIconUrl;
L.Icon.Default.mergeOptions({
  iconRetinaUrl: 'https://unpkg.com/leaflet@1.9.4/dist/images/marker-icon-2x.png',
  iconUrl: 'https://unpkg.com/leaflet@1.9.4/dist/images/marker-icon.png',
  shadowUrl: 'https://unpkg.com/leaflet@1.9.4/dist/images/marker-shadow.png',
});

// Custom pulsing marker icon
const pulsingIcon = L.divIcon({
  className: 'location-picker-marker',
  html: `
    <div class="picker-marker-pin">
      <span class="material-icons-round">location_on</span>
    </div>
    <div class="picker-marker-pulse"></div>
  `,
  iconSize: [40, 40],
  iconAnchor: [20, 40],
});

// Component that handles map click events
function MapClickHandler({ onLocationSelect }) {
  useMapEvents({
    click(e) {
      onLocationSelect(e.latlng.lat, e.latlng.lng);
    },
  });
  return null;
}

// Component that re-centers the map when position changes externally
function MapRecenter({ lat, lng }) {
  const map = useMap();
  useEffect(() => {
    if (lat && lng) {
      map.setView([lat, lng], map.getZoom(), { animate: true });
    }
  }, [lat, lng, map]);
  return null;
}

// Search box component for geocoding
function SearchBox({ onResult }) {
  const [query, setQuery] = useState('');
  const [results, setResults] = useState([]);
  const [searching, setSearching] = useState(false);
  const [showResults, setShowResults] = useState(false);
  const timeoutRef = useRef(null);
  const wrapperRef = useRef(null);

  // Close dropdown on outside click
  useEffect(() => {
    function handleClickOutside(e) {
      if (wrapperRef.current && !wrapperRef.current.contains(e.target)) {
        setShowResults(false);
      }
    }
    document.addEventListener('mousedown', handleClickOutside);
    return () => document.removeEventListener('mousedown', handleClickOutside);
  }, []);

  const handleSearch = (value) => {
    setQuery(value);
    if (timeoutRef.current) clearTimeout(timeoutRef.current);
    if (value.trim().length < 3) {
      setResults([]);
      setShowResults(false);
      return;
    }
    setSearching(true);
    timeoutRef.current = setTimeout(async () => {
      try {
        const res = await fetch(
          `https://nominatim.openstreetmap.org/search?format=json&q=${encodeURIComponent(value)}&limit=5&countrycodes=id`
        );
        const data = await res.json();
        setResults(data);
        setShowResults(true);
      } catch {
        setResults([]);
      } finally {
        setSearching(false);
      }
    }, 500);
  };

  const handleSelect = (item) => {
    setQuery(item.display_name.split(',').slice(0, 2).join(','));
    setShowResults(false);
    onResult(parseFloat(item.lat), parseFloat(item.lon), item.display_name);
  };

  return (
    <div className="map-search-wrapper" ref={wrapperRef}>
      <div className="map-search-input-box">
        <span className="material-icons-round map-search-icon">search</span>
        <input
          type="text"
          className="map-search-input"
          placeholder="Cari lokasi..."
          value={query}
          onChange={(e) => handleSearch(e.target.value)}
          onFocus={() => results.length > 0 && setShowResults(true)}
        />
        {searching && (
          <div className="spinner map-search-spinner" style={{ width: '18px', height: '18px', borderWidth: '2px' }}></div>
        )}
      </div>
      {showResults && results.length > 0 && (
        <div className="map-search-results">
          {results.map((item, i) => (
            <div
              key={i}
              className="map-search-result-item"
              onClick={() => handleSelect(item)}
            >
              <span className="material-icons-round" style={{ fontSize: '18px', color: 'var(--primary)', flexShrink: 0 }}>
                place
              </span>
              <span className="map-search-result-text">
                {item.display_name}
              </span>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}

export default function LocationPickerMap({ latitude, longitude, onLocationSelect }) {
  const lat = parseFloat(latitude);
  const lng = parseFloat(longitude);
  const hasPosition = !isNaN(lat) && !isNaN(lng);

  // Default center: Central Java
  const defaultCenter = [-7.25, 110.42];
  const center = hasPosition ? [lat, lng] : defaultCenter;
  const zoom = hasPosition ? 15 : 8;

  const handleSearchResult = (searchLat, searchLng) => {
    onLocationSelect(searchLat, searchLng);
  };

  return (
    <div className="location-picker">
      <div className="location-picker-header">
        <div className="location-picker-title">
          <span className="material-icons-round" style={{ color: 'var(--primary)', fontSize: '20px' }}>
            map
          </span>
          <span>Pilih Lokasi di Peta</span>
        </div>
        <span className="location-picker-hint">
          Klik pada peta atau cari lokasi
        </span>
      </div>

      <SearchBox onResult={handleSearchResult} />

      <div className="location-picker-map-container">
        <MapContainer
          center={center}
          zoom={zoom}
          style={{ height: '100%', width: '100%' }}
          scrollWheelZoom={true}
        >
          <TileLayer
            attribution='&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a>'
            url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
          />
          <MapClickHandler onLocationSelect={onLocationSelect} />
          {hasPosition && (
            <>
              <Marker position={[lat, lng]} icon={pulsingIcon} />
              <MapRecenter lat={lat} lng={lng} />
            </>
          )}
        </MapContainer>

        {/* Crosshair overlay */}
        <div className="map-crosshair">
          <div className="map-crosshair-v"></div>
          <div className="map-crosshair-h"></div>
        </div>
      </div>

      {hasPosition && (
        <div className="location-picker-coords">
          <div className="location-coord-chip">
            <span className="material-icons-round" style={{ fontSize: '14px' }}>north</span>
            <span>{lat.toFixed(6)}</span>
          </div>
          <div className="location-coord-chip">
            <span className="material-icons-round" style={{ fontSize: '14px' }}>east</span>
            <span>{lng.toFixed(6)}</span>
          </div>
        </div>
      )}
    </div>
  );
}
