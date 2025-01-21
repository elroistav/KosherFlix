import React, { useState, useEffect } from "react";
import { useParams, useLocation } from "react-router-dom";
import axios from "axios";
import MoviePlayer from "../components/MoviePlayer";
import Navbar from "../components/NavBar";
import "../styles/MoviePage.css";
import { useNavigate } from "react-router-dom";

function MoviePage() {
  const { movieId } = useParams();
  const location = useLocation();
  const [movie, setMovie] = useState(location.state?.movie || null);
  const [relatedMovies, setRelatedMovies] = useState([]);



  const navigate = useNavigate();

  useEffect(() => {
    if (!movie) {
      alert("Movie not found. Redirecting to homepage.");
      navigate("/");
    }
  }, [movie, navigate]);

  return (
    <div className="movie-page">
      <Navbar />
      {movie && <MoviePlayer videoUrl={movie.videoUrl} />}
    </div>
  );
}

export default MoviePage;
