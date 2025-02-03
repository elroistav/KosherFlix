import React from 'react';
import '../styles/MovieCard.css';


function MovieCard({ movie, onClick }) {
  const BASE_URL = process.env.REACT_APP_BASE_URL;
  return (
    <div
        className="movie-card"
        onClick={onClick}
        style={{
            backgroundImage: `url(${BASE_URL}/${movie.thumbnail})`, // Set movie thumbnail dynamically
            backgroundSize: 'cover',
            backgroundPosition: 'center',
        }}
    >
        <p>{movie.title}</p> {/* Display the movie title */}
    </div>
  );
}

export default MovieCard;
