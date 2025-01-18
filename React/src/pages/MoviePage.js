import React, { useState, useEffect } from "react";
import { useParams } from "react-router-dom"; // To get the movie ID from the URL
import axios from "axios";
import MoviePlayer from "../components/MoviePlayer";
import Navbar from "../components/NavBar"; // Reusable navbar
import "../styles/MoviePage.css";

function MoviePage() {
  const { movieId } = useParams(); // Fetch movie ID from URL
  const [movie, setMovie] = useState(null);
  const [relatedMovies, setRelatedMovies] = useState([]);

  useEffect(() => {
    async function fetchMovieDetails() {
      try {
        const response = await axios.get(`http://localhost:4000/api/movies/${movieId}`, {
          headers: { "user-id": "678820cd237c5fc9782768ba" },
        });
        setMovie(response.data);

        // Fetch related movies
        const relatedResponse = await axios.get(`http://localhost:4000/api/movies/related/${movieId}`, {
          headers: { "user-id": "678820cd237c5fc9782768ba" },
        });
        setRelatedMovies(relatedResponse.data);
      } catch (error) {
        console.error("Error fetching movie details:", error);
      }
    }

    fetchMovieDetails();
  }, [movieId]);

  if (!movie) {
    return <div className="loading">Loading...</div>;
  }

  return (
    <div
      className="movie-page"
      style={{
        backgroundImage: `url(${movie.backgroundImageUrl})`,
        backgroundSize: "cover",
        backgroundPosition: "center",
      }}
    >
      <Navbar />
      <div className="movie-container">
        <h1 className="movie-title">{movie.title}</h1>
        <p className="movie-description">{movie.description}</p>
        <div className="movie-metadata">
          <span>{movie.releaseYear}</span> • <span>{movie.genres.join(", ")}</span> •{" "}
          <span>{movie.rating} ⭐</span>
        </div>
        <MoviePlayer videoUrl={movie.videoUrl} />
        <div className="movie-actions">
          <button className="like-button">👍 Like</button>
          <button className="dislike-button">👎 Dislike</button>
          <button className="save-button">💾 Save to List</button>
        </div>
        <div className="related-movies">
          <h2>More Like This</h2>
          <div className="related-movies-list">
            {relatedMovies.map((relatedMovie) => (
              <div key={relatedMovie._id} className="related-movie-card">
                <img src={relatedMovie.thumbnail} alt={relatedMovie.title} />
                <p>{relatedMovie.title}</p>
              </div>
            ))}
          </div>
        </div>
      </div>
    </div>
  );
}

export default MoviePage;
