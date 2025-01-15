import React from 'react';
import '../styles/MovieModal.css';

function MovieModal({ movie, onClose }) {
  if (!movie) return null;

  return (
    <div className="movie-modal-overlay" onClick={onClose}>
      <div className="movie-modal-content" onClick={(e) => e.stopPropagation()}>
        <button className="close-button" onClick={onClose}>X</button>
        <h2>{movie.title}</h2>
        <p><strong>Length:</strong> {movie.length} mins</p>
        <p><strong>Director:</strong> {movie.director}</p>
        <p><strong>Language:</strong> {movie.language}</p>
        <p><strong>Release Date:</strong> {new Date(movie.releaseDate).toDateString()}</p>
        <p><strong>Subtitles:</strong> {movie.subtitles.join(', ')}</p>
        <button className="watch-button">Watch Movie</button>
      </div>
    </div>
  );
}

export default MovieModal;
