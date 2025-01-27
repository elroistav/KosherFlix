import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import MoviePlayer from './MoviePlayer';
import '../styles/RandomMovie.css';

function RandomMovie({ movie, onClick }) {
  const navigate = useNavigate();
  const [isVideoReady, setIsVideoReady] = useState(false);

  useEffect(() => {
    if (movie && movie.videoUrl) {
      // Ensure video is fully loaded before attempting playback
      const video = new Audio(movie.videoUrl);
      video.load();
      video.oncanplaythrough = () => setIsVideoReady(true);
    }
  }, [movie]);

  if (!movie) return null;

  const handlePlayClick = () => {
    navigate('/movie', { state: { movie } });
  };

  return (
    <div className="random-movie">
      <div className="movie-player-wrapper">
        <MoviePlayer 
          videoUrl={movie.videoUrl} 
          controlsAppear={false}
          muted={true} // Force muted to bypass autoplay restrictions
          autoPlay={true}
          key={movie.videoUrl} // Force re-render with new video
        />
      </div>

      <div className="random-movie-info">
        <h2 className="movie-title">{movie.title}</h2>
        <p>Isnt this a great soccer game?? Does it need anything else? If so, call 1800-CHANGE to let us know and maybe you'll win a NEW CAR!</p>
      </div>

      {/* Buttons */}
      <div className="buttons-container">
        <button onClick={handlePlayClick}>
          <span className="play-icon">▶</span> Play
        </button>
        <button onClick={onClick}>
          <span className="info-icon">ℹ️</span> More Info
        </button>
      </div>
    </div>
  );
}

export default RandomMovie;