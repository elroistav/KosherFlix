import React from 'react';
import MovieCard from './MovieCard';
import AdminMovieCard from './AdminMovieCard';
import '../styles/SearchResults.css';

/**
 * Component to display search results.
 *
 * @param {Object} props - The component props.
 * @param {Array} props.searchResults - The list of search results to display.
 * @param {Function} props.handleMovieClick - The function to handle movie click events.
 * @param {string} props.searchText - The text to display as the search query.
 * @param {boolean} props.isAdmin - Flag to determine if the user is an admin.
 * @returns {JSX.Element} The rendered component.
 */
function SearchResults({ searchResults, handleMovieClick, searchText, isAdmin }) {
  return (
    <div className="search-results">
      <h2>{searchText}</h2>
      <div className="category-row">
        {searchResults.map((movie) => (
          isAdmin ? 
          <AdminMovieCard key={movie._id} movie={movie} onClick={() => handleMovieClick(movie._id)} /> :
          <MovieCard key={movie._id} movie={movie} onClick={() => handleMovieClick(movie._id)} />
        ))}
      </div>
    </div>
  );
}

export default SearchResults;