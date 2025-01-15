import React from 'react';

function MovieCard({ movieId, onClick }) {
  return (
    <div className="movie-card" onClick={onClick}>
      <p>{movieId}</p>
    </div>
  );
}

export default MovieCard;
