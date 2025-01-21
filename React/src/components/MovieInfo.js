import React from 'react';
import '../styles/MovieInfo.css';

function MovieInfo({ movie }) {
  return (
    <div className="movie-info-grid">
      <div className="info-card description-card">
        <span className="info-label">About movie:</span>
        <span className="info-value">{movie.description}</span>
      </div>
      <div className="info-card">
        <span className="info-icon">⏱️</span>
        <span className="info-label">Length</span>
        <span className="info-value">{movie.length} mins</span>
      </div>
      <div className="info-card">
        <span className="info-icon">🎬</span>
        <span className="info-label">Director</span>
        <span className="info-value">{movie.director}</span>
      </div>
      <div className="info-card">
        <span className="info-icon">🗣️</span>
        <span className="info-label">Language</span>
        <span className="info-value">{movie.language}</span>
      </div>
      <div className="info-card">
        <span className="info-icon">📅</span>
        <span className="info-label">Release Date</span>
        <span className="info-value">{new Date(movie.releaseDate).toDateString()}</span>
      </div>
      <div className="info-card">
        <span className="info-icon">💬</span>
        <span className="info-label">Subtitles</span>
        <span className="info-value">{movie.subtitles.join(', ')}</span>
      </div>
    </div>
  );
}

export default MovieInfo;