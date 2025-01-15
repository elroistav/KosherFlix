import React from 'react';

function MovieCard({ movie, onClick }) {
  return (
    <div
        className="movie-card"
        onClick={onClick}
        style={{
            backgroundImage: `url(${movie.thumbnail})`, // Set movie thumbnail dynamically
            backgroundSize: 'cover',
            backgroundPosition: 'center',
        }}
    >
        <p>{movie.title}</p> {/* Display the movie title */}
    </div>
  );
}

export default MovieCard;
