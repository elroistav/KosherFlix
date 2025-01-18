import React, { useState, useEffect } from "react";
import "../styles/MoviePage.css"; // Assuming the same styles are used
import MoviePlayer from "../components/MoviePlayer"; // Same video player component

function MockMoviePage() {
  const [movie, setMovie] = useState(null);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    // Mock movie data
    const mockMovie = {
      videoUrl: "/videos/sample.mp4", // Use a local video file
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
        backgroundImage: `url(${movie.backgroundImageUrl})`
      }}
    >
      <div className="movie-container">
        <MoviePlayer videoUrl={movie.videoUrl} />
      </div>
    </div>
  );
}

export default MockMoviePage;
