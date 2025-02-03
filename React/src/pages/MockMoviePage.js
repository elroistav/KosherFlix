import React, { useState, useEffect } from "react";
import "../styles/MoviePage.css"; // Assuming the same styles are used
import MoviePlayer from "../components/MoviePlayer"; // Same video player component

function MockMoviePage() {
  const BASE_URL = process.env.REACT_APP_BASE_URL;
  const [movie, setMovie] = useState(null);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    // Mock movie data
    const mockMovie = {
      videoUrl: "/videos/sample.mp4", // Use a local video file
      backgroundImageUrl: BASE_URL + "/images/brooklyn-99-thumbnail.png", // Placeholder image
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
    >
      <MoviePlayer videoUrl={movie.videoUrl} />
    </div>
  );
}

export default MockMoviePage;
