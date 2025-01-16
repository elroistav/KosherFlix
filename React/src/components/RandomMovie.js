import React from 'react';
import '../styles/RandomMovie.css';

function RandomMovie({ movie, onClick }) {
  if (!movie) return null;

  return (
    <div className="random-movie">
      <div className="overlay"></div>
      <h2>{movie.title}</h2>

      {/* Buttons */}
      <div className="buttons-container">
        <button>
          <span className="play-icon">▶</span> Play
        </button>
        <button onClick={onClick}>
          <span className="info-icon">ℹ️</span> More Info
        </button>
      </div>
    </div>
  );
}

export default RandomMovie;

