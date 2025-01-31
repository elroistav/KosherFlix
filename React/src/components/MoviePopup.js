import React, { useEffect, useState } from 'react';
import axios from 'axios';
import MovieCard from './MovieCard'; // Import your existing MovieCard component
import { useNavigate } from 'react-router-dom';
import MoviePlayer from './MoviePlayer'; // Import your existing MoviePlayer component
import MovieInfo from './MovieInfo';
import '../styles/MoviePopup.css';
//import user from '../../../NetflixProj3/models/user';

/**
 * MoviePopup component displays a popup with movie details and recommendations.
 * 
 * @component
 * @param {Object} props - The component props.
 * @param {Object} props.userInfo - The user information.
 * @param {string} props.userInfo.userId - The user's ID.
 * @param {Object} props.initialMovie - The initial movie to display in the popup.
 * @param {string} props.initialMovie._id - The movie's ID.
 * @param {string} props.initialMovie.title - The movie's title.
 * @param {string} [props.initialMovie.videoUrl] - The URL of the movie's video.
 * @param {Function} props.onClose - The function to call when the popup is closed.
 * 
 * @returns {JSX.Element|null} The rendered component or null if no movie is provided.
 * 
 * @example
 * <MoviePopup 
 *   userInfo={{ userId: '12345' }} 
 *   initialMovie={{ _id: '67890', title: 'Example Movie', videoUrl: 'http://example.com/video.mp4' }} 
 *   onClose={() => console.log('Popup closed')} 
 * />
 */
function MoviePopup({ userInfo, initialMovie, onClose }) {
  const BASE_URL = process.env.REACT_APP_BASE_URL;
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
          `${BASE_URL}/api/movies/${movie._id}/recommend`,
          {
              headers: { 'user-id': userInfo.userId }
          }
      );
        const recommendedMovieIds = response.data; // Assuming the response is a list of movie IDs

        // Fetch details for each recommended movie
        const recommendedMovies = await Promise.all(
          recommendedMovieIds.map(async (id) => {
            try {
              const movieResponse = await axios.get(`${BASE_URL}/api/movies/${id}`, {
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

  /**
   * Handles the click event for watching a movie.
   * Sends a POST request to recommend the movie for the user.
   * Navigates to the movie page after the request is completed.
   * 
   * @async
   * @function handleWatchClick
   * @returns {Promise<void>} A promise that resolves when the operation is complete.
   * @throws Will log an error message if the request fails.
   */
  const handleWatchClick = async () => {
    try {
        console.log('post User ID:', userInfo.userId);
        await axios.post(
            `${BASE_URL}/api/movies/${movie._id}/recommend`,
            {},
            {
                headers: { 'user-id': userInfo.userId }
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
