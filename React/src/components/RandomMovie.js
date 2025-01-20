import React from 'react';
import MoviePlayer from './MoviePlayer'; // Import the MoviePlayer component
import '../styles/RandomMovie.css';
import HomeScreen from '../pages/HomeScreen';

function RandomMovie({ movie, onClick }) {
  if (!movie) return null;

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