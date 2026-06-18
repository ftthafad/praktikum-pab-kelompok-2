export default function RatingDisplay({ rating, maxStars = 5 }) {
  const stars = [];
  for (let i = 1; i <= maxStars; i++) {
    if (i <= Math.floor(rating)) {
      stars.push(<span key={i} className="material-icons-round filled">star</span>);
    } else if (i - 0.5 <= rating) {
      stars.push(<span key={i} className="material-icons-round filled">star_half</span>);
    } else {
      stars.push(<span key={i} className="material-icons-round">star_border</span>);
    }
  }
  return <span className="rating-display">{stars}</span>;
}
