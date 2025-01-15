import React, { useState, useEffect } from 'react';
import axios from 'axios';
import Navbar from '../components/NavBar';  // Importing the Navbar component
import MovieModal from '../components/MovieModal';  // assuming you already have the modal component
import '../styles/HomeScreen.css';

function HomeScreen() {
  const [movies, setMovies] = useState([]);
  const [randomMovie, setRandomMovie] = useState(null);
  const [selectedMovie, setSelectedMovie] = useState(null);

  // Fetch movies data from API and details for each movie
  useEffect(() => {
    async function fetchMovies() {
      try {
        const response = await axios.get('http://localhost:4000/api/movies', {
          headers: { 'user-id': '6787bd0b237c5fc978270e18' }
        });

        // Fetch the details of each movie in each category
        const fetchedMoviesByCategory = [];
        for (const category of response.data.promotedCategories) {
          const fetchedMovies = [];
          for (const movieId of category.movies) {
            try {
              const movieResponse = await axios.get(`http://localhost:4000/api/movies/${movieId}`, {
                headers: { 'user-id': '6787bd0b237c5fc978270e18' }
              });
              fetchedMovies.push(movieResponse.data);
            } catch (error) {
              console.error(`Error fetching movie details for movieId ${movieId}:`, error);
            }
          }
          fetchedMoviesByCategory.push({ category: category.category, movies: fetchedMovies });
        }

        setMovies(fetchedMoviesByCategory); // Set all the fetched movies by categories

        // Get a random movie from the response (for the random movie section)
        if (fetchedMoviesByCategory.length > 0) {
          const randomCategory = fetchedMoviesByCategory[Math.floor(Math.random() * fetchedMoviesByCategory.length)];
          const random = randomCategory.movies[Math.floor(Math.random() * randomCategory.movies.length)];
          setRandomMovie(random);
        }
      } catch (error) {
        console.error("Error fetching categories or movie details:", error);
      }
    }

    fetchMovies();
  }, []);

  // Handle click on a movie card to open the modal
  const handleMovieClick = (movieId) => {
    const movie = movies.flatMap(category => category.movies).find(m => m._id === movieId);
    setSelectedMovie(movie);
  };

  return (
    <div>
        <Navbar /> {/* Navbar component */}
      {/* Random Movie Section */}
      {randomMovie && (
        <div className="random-movie">
          <div className="overlay"></div>
          <h1 className="welcome-text">Play Random Movie</h1>
          <h2>{randomMovie.title}</h2>
          <button>Play</button>
        </div>
      )}

      {/* Movie Categories Section */}
      <div className="movie-categories">
        {movies.length > 0 && movies.map((category, index) => (
          <div className="category-row" key={index}>
            <h3>{category.category}</h3>
            <div className="category-movies">
              {category.movies.map((movie, idx) => (
                <div
                  className="movie-card"
                  key={idx}
                  onClick={() => handleMovieClick(movie._id)}
                  style={{
                    backgroundImage: `url(${movie.thumbnail})`, // Set movie thumbnail dynamically
                    backgroundSize: 'cover',
                    backgroundPosition: 'center',
                  }}
                >
                  <p>{movie.title}</p> {/* Display the movie title */}
                </div>
              ))}
            </div>
          </div>
        ))}
      </div>

      {/* Movie Modal */}
      {selectedMovie && (
        <MovieModal movie={selectedMovie} onClose={() => setSelectedMovie(null)} />
      )}
    </div>
  );
}

export default HomeScreen;
