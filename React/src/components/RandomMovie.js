import React from 'react';
import '../styles/HomeScreen.css';

function RandomMovie({ movie }) {
  if (!movie) return null;

  return (
    <div className="random-movie">
      <div className="overlay"></div>
      <h1 className="welcome-text">Play Random Movie</h1>
      <h2>{movie.title}</h2> {/* Display the movie title */}
      <button>Play</button>
    </div>
  );
}

export default RandomMovie;
