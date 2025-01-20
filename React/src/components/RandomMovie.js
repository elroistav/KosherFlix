import React from 'react';
import { useNavigate } from 'react-router-dom';
import MoviePlayer from './MoviePlayer';
import '../styles/RandomMovie.css';

function RandomMovie({ movie, onClick }) {
  const navigate = useNavigate();

  if (!movie) return null;

  const handlePlayClick = () => {
    navigate('/movie', { state: { movie } });
  };

  return (
    <div className="random-movie">
      {/* Movie Player */}
      <div className="movie-player-wrapper">
        <MoviePlayer videoUrl={movie.videoUrl} controlsAppear={false} />
      </div>

      <div className="movie-info">
        <h2 className="movie-title">{movie.title}</h2>
        <p>This is the description of the movieThis is the description of the movieThis is the description of the movieThis is the description of the movieThis is the description of the movieThis is the description of the movieThis is the description of the movieThis is the description of the movieThis is the description of the movieThis is the description of the movieThis is the description of the movieThis is the description of the movieThis is the description of the movieThis is the description of the movieThis is the description of the movieThis is the description of the movieThis is the description of the movieThis is the description of the movieThis is the description of the movieThis is the description of the movieThis is the description of the movie</p>
      </div>

      {/* Buttons */}
      <div className="buttons-container">
        <button onClick={handlePlayClick}>
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