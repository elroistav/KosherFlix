import React, { useEffect, useState } from 'react';
import axios from 'axios';
import MovieCard from './MovieCard'; // Import your existing MovieCard component
import { useNavigate } from 'react-router-dom';
import MoviePlayer from './MoviePlayer'; // Import your existing MoviePlayer component
import MovieInfo from './MovieInfo';
import '../styles/MoviePopup.css';
//import user from '../../../NetflixProj3/models/user';

function MoviePopup({ userInfo, initialMovie, onClose }) {
  const [movie, setMovie] = useState(initialMovie);
  const [recommendations, setRecommendations] = useState([]);
  const [loadingRecommendations, setLoadingRecommendations] = useState(false);
  const [show, setShow] = useState(false);

  const navigate = useNavigate();

  // Fetch recommendations when the modal opens
  useEffect(() => {
    async function fetchRecommendations() {
      if (!movie?._id) return;

      setLoadingRecommendations(true);

      try {
        console.log('Fetching recommendations for movie:', movie.title, movie._id);
        console.log('fetch User ID:', userInfo.userId);
        const response = await axios.get(
          `http://localhost:4000/api/movies/${movie._id}/recommend`,
          {
              headers: { 'user-id': userInfo.userId } // כאן מעבירים את ה-Headers
          }
      );
        const recommendedMovieIds = response.data; // Assuming the response is a list of movie IDs

        // Fetch details for each recommended movie
        const recommendedMovies = await Promise.all(
          recommendedMovieIds.map(async (id) => {
            try {
              const movieResponse = await axios.get(`http://localhost:4000/api/movies/${id}`, {
                headers: { 'user-id': userInfo.userId }
              });
              return movieResponse.data;
            } catch (error) {
              console.error(`Error fetching recommended movie ${id}:`, error);
              return null;
            }
          })
        );

        setRecommendations(recommendedMovies.filter((rec) => rec !== null)); // Filter out null responses
      } catch (error) {
        console.error("Error fetching recommendations:", error);
      } finally {
        setLoadingRecommendations(false); 
      }
    }

    fetchRecommendations();
  }, [movie]);

  useEffect(() => {
    // Add class to body to disable scrolling
    document.body.classList.add('no-scroll');
    setShow(true);

    // Cleanup function to remove class when modal is closed
    return () => {
      document.body.classList.remove('no-scroll');
      setShow(false);
    };
  }, []);

  if (!movie) return null;

  const handleVideoEnd = () => {
    const videoElement = document.querySelector('.movie-video-prview video');
    if (videoElement) {
      videoElement.currentTime = 0;
      videoElement.play();
    }
  };

  const handleWatchClick = async () => {
    try {
        console.log('post User ID:', userInfo.userId);
        await axios.post(
            `http://localhost:4000/api/movies/${movie._id}/recommend`,
            {}, // גוף הבקשה (אם אין מידע בגוף, העבר אובייקט ריק)
            {
                headers: { 'user-id': userInfo.userId } // כאן מעבירים את ה-Headers
            }
        );
        navigate('/movie', { state: { movie } });
    } catch (error) {
        console.error('Failed to update recommendations:', error);
        navigate('/movie', { state: { movie } });
    }
};


  return (
    <div className="movie-popup-overlay" onClick={onClose}>
      <div className="movie-popup-content" onClick={(e) => e.stopPropagation()}>
        <button className="close-button" onClick={onClose}>X</button>
        <h2>{movie.title}</h2>
        {movie.videoUrl && (
          <div className="movie-video-prview">
            <MoviePlayer 
              key={movie.videoUrl}
              videoUrl={movie.videoUrl} 
              controlsAppear={false}
              onEnded={handleVideoEnd} 
            />
          </div>
        )}
        <MovieInfo movie={movie} />
        <button className="watch-button" onClick={handleWatchClick}>
          Watch Now
      </button>
        <div className="movie-popup-recommendations">
        <h3>Movie recommendations based of your previous watches:</h3>
          <div className="recommendations-container">
            {loadingRecommendations ? ( 
              <p>Loading recommendations...</p> 
            ) : recommendations.length > 0 ? (
              <div className="carousel">
                {recommendations.map((rec) => (
                  <MovieCard
                    key={rec._id}
                    movie={rec}
                    onClick={() => setMovie(rec)}
                  />
                ))}
              </div>
            ) : (
              <p>No recommendations available.</p>
            )}
          </div>
        </div>
      </div>
    </div>
  );
}

export default MoviePopup;
