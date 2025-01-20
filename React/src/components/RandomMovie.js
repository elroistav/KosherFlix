import React from 'react';
import MoviePlayer from './MoviePlayer'; // Import the MoviePlayer component
import '../styles/RandomMovie.css';

function RandomMovie({ movie, onClick }) {
  if (!movie) return null;

  return (
    <div className="random-movie">
      <div className="overlay"></div>
      <h2>{movie.title}</h2>

      {/* Movie Player */}
      <div className="movie-player-wrapper">
        <MoviePlayer videoUrl={movie.videoUrl} controlsAppear={false} />
      </div>

      {/* Buttons */}
      <div className="buttons-container">
        <button onClick={() => document.querySelector('.movie-player-container video').play()}>
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