import React from 'react';
import MovieCard from './MovieCard';

function CategoryRow({ categoryName, movies, onMovieClick }) {
  return (
    <div className="category-row">
      <h3>{categoryName}</h3>
      <div className="category-movies">
        {movies.map((movieId, index) => (
          <MovieCard key={index} movieId={movieId} onClick={() => onMovieClick(movieId)} />
        ))}
      </div>
    </div>
  );
}

export default CategoryRow;