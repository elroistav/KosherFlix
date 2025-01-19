import React from 'react';
import MovieCard from './MovieCard';
import '../styles/CategoryRow.css';

function CategoryRow({ categoryName, movies, onMovieClick }) {
  return (
    <div className="category-row">
      <h3>{categoryName}</h3>
      <div className="category-movies">
        {movies.map((movie, index) => (
          <MovieCard key={index} movie={movie} onClick={() => onMovieClick(movie._id)} />
        ))}
      </div>
    </div>
  );
}

export default CategoryRow;