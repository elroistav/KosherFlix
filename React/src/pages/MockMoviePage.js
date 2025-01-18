import React, { useState, useEffect } from "react";
import "../styles/MoviePage.css"; // Assuming the same styles are used
import MoviePlayer from "../components/MoviePlayer"; // Same video player component

function MockMoviePage() {
  const [movie, setMovie] = useState(null);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    // Mock movie data
    const mockMovie = {
      title: "Sample Movie",
      description: "A thrilling adventure through the world of testing UIs.",
      videoUrl: "/videos/sample.mp4", // Use a local video file
      genres: ["Action", "Adventure"],
      releaseYear: 2023,
      rating: 8.5,
      backgroundImageUrl: "https://via.placeholder.com/1920x1080", // Placeholder image
    };

    // Simulate API delay
    setTimeout(() => {
      setMovie(mockMovie);
      setIsLoading(false);
    }, 1000); // 1-second delay
  }, []);

  if (isLoading) {
    return <div>Loading...</div>;
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
      <div className="movie-container">
        <h1 className="movie-title">{movie.title}</h1>
        <p className="movie-description">{movie.description}</p>
        <div className="movie-metadata">
          <span>{movie.releaseYear}</span> • <span>{movie.genres.join(", ")}</span> •{" "}
          <span>{movie.rating} ⭐</span>
        </div>
        <MoviePlayer videoUrl={movie.videoUrl} />
      </div>
    </div>
  );
}

export default MockMoviePage;
