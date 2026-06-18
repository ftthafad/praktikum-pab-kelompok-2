import { useState } from 'react';

export default function RatingInput({ onChange, initialValue = 0 }) {
  const [currentRating, setCurrentRating] = useState(initialValue);
  const [hoverRating, setHoverRating] = useState(0);

  const handleClick = (i) => {
    setCurrentRating(i);
    if (onChange) onChange(i);
  };

  const displayRating = hoverRating || currentRating;

  return (
    <div className="rating-input" onMouseLeave={() => setHoverRating(0)}>
      {[1, 2, 3, 4, 5].map(i => (
        <span
          key={i}
          className={`material-icons-round star ${i <= displayRating ? 'filled' : ''}`}
          onClick={() => handleClick(i)}
          onMouseEnter={() => setHoverRating(i)}
        >
          {i <= displayRating ? 'star' : 'star_border'}
        </span>
      ))}
    </div>
  );
}
