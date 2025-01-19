import React, { useRef, useEffect } from 'react';
// import MoviePlayer from './MoviePlayer'; // Import the MoviePlayer component
import '../styles/RandomMovie.css';

function RandomMovie({ movie, onClick }) {
  const videoRef = useRef(null);

  useEffect(() => {
    if (videoRef.current) {
      videoRef.current.play().catch(error => {
        console.error('Error attempting to play:', error);
      });
    }
  }, [movie]);
        <video ref={videoRef} className="movie-player-container" src={movie.videoURL} controls />
  if (!movie) return null;

  return (
    <div className="random-movie">
        <button onClick={() => videoRef.current.play()}>Play</button>
      <h2>{movie.title}</h2>

      {/* Movie Player */}
      <div className="movie-player-wrapper">
        <video className="movie-player-container" src={movie.videoURL} autoPlay controls />
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