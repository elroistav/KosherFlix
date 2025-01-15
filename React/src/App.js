import React, { useState, useEffect } from 'react';
import './pages/HomeScreen.css';
// import './App.js'

function HomeScreen() {
  const [movies, setMovies] = useState([]);
  const [randomMovie, setRandomMovie] = useState(null);

  // Fetch movies data from API
  useEffect(() => {
    async function fetchMovies() {
      try {
        const response = await fetch('http://localhost:5000/api/movies'); // Adjust your API URL
        const data = await response.json();
        setMovies(data);

        // Get a random movie from the response
        if (data.length > 0) {
          const random = data[Math.floor(Math.random() * data.length)];
          setRandomMovie(random);
        }
      } catch (error) {
        console.error("Error fetching movies:", error);
      }
    }

    fetchMovies();
  }, []);

  return (
    <div>
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
            <h3>{category.name}</h3>
            <div className="category-movies">
              {category.movies.map((movie, idx) => (
                <div className="movie-card" key={idx}>
                  <img src={movie.thumbnail} alt={movie.title} />
                  <p>{movie.title}</p>
                </div>
              ))}
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}

export default HomeScreen;
