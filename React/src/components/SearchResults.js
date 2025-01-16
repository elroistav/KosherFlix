import React from 'react';
import MovieCard from './MovieCard';
import '../styles/SearchResults.css';

function SearchResults({ searchResults, handleMovieClick }) {
  return (
    <div className="search-results">
      <h2>Search Results</h2>
      <div className="category-row">
        {searchResults.map((movie) => (
          <MovieCard key={movie._id} movie={movie} onClick={() => handleMovieClick(movie._id)} />
        ))}
      </div>
    </div>
  );
}

export default SearchResults;