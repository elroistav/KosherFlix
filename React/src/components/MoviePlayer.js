import React, { useRef, useState, useEffect } from "react";
import "../styles/MoviePlayer.css";

function MoviePlayer({ videoUrl }) {
  const videoRef = useRef(null);
  const [isPlaying, setIsPlaying] = useState(false);
  const [isMuted, setIsMuted] = useState(false);
  const [isFullScreen, setIsFullScreen] = useState(false);
  const [showControls, setShowControls] = useState(true);
  const [progress, setProgress] = useState(0);

  // Play/Pause functionality
  const togglePlayPause = () => {
    if (isPlaying) {
      videoRef.current.pause();
    } else {
      videoRef.current.play();
    }
    setIsPlaying(!isPlaying);
  };

  // Mute/Unmute functionality
  const toggleMute = () => {
    videoRef.current.muted = !isMuted;
    setIsMuted(!isMuted);
  };

  // Full-screen functionality
  const toggleFullScreen = () => {
    if (!isFullScreen) {
      videoRef.current.requestFullscreen();
    } else {
      document.exitFullscreen();
    }
    setIsFullScreen(!isFullScreen);
  };

  // Update progress bar
  const handleTimeUpdate = () => {
    const progress = (videoRef.current.currentTime / videoRef.current.duration) * 100;
    setProgress(progress);
  };

  // Show controls on mouse activity
  useEffect(() => {
    const timer = setTimeout(() => setShowControls(false), 3000);
    return () => clearTimeout(timer);
  }, [showControls]);

  return (
    <div
      className="movie-player-container"
      onMouseMove={() => setShowControls(true)} // Show controls on mouse move
      onClick={togglePlayPause} // Play/pause when clicking anywhere on the video
    >
      <video
        ref={videoRef}
        className="video-player"
        onTimeUpdate={handleTimeUpdate}
      >
        <source src={videoUrl} type="video/mp4" />
        Your browser does not support the video tag.
      </video>
      {/* Big Center Play/Pause Button */}
      {showControls && (
        <button
          className={`center-play-button ${isPlaying ? "hidden" : ""}`}
          onClick={(e) => {
            e.stopPropagation(); // Prevent triggering play/pause twice
            togglePlayPause();
          }}
        >
          {isPlaying ? "❚❚" : "▶"}
        </button>
      )}
      {/* Overlay Controls */}
      {showControls && (
        <div className="video-controls">
          <button onClick={togglePlayPause} className="control-button">
            {isPlaying ? "❚❚" : "▶"}
          </button>
          <input
            type="range"
            className="progress-bar"
            value={progress}
            onChange={(e) => {
              const newTime = (e.target.value / 100) * videoRef.current.duration;
              videoRef.current.currentTime = newTime;
              setProgress(e.target.value);
            }}
          />
          <button onClick={toggleMute} className="control-button">
            {isMuted ? "🔇" : "🔊"}
          </button>
          <button onClick={toggleFullScreen} className="control-button">
            {isFullScreen ? "⤬" : "⤢"}
          </button>
        </div>
      )}
    </div>
  );
}

export default MoviePlayer;
