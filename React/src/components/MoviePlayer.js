import React, { useRef, useState, useEffect } from "react";
import "../styles/MoviePlayer.css";

function MoviePlayer({ videoUrl, controlsAppear = true }) {
  const videoRef = useRef(null);
  const [isPlaying, setIsPlaying] = useState(false);
  const [isMuted, setIsMuted] = useState(!controlsAppear);
  const [isFullScreen, setIsFullScreen] = useState(false);
  const [showControls, setShowControls] = useState(true);
  const [progress, setProgress] = useState(0);
  const [playbackSpeed, setPlaybackSpeed] = useState(1); // Default speed is 1x

  // Play/Pause functionality
  const togglePlayPause = () => {
    if (isPlaying && controlsAppear) {
      videoRef.current.pause();
    } else {
      videoRef.current.play();
    }
    setIsPlaying(!isPlaying);
  };

  // Mute/Unmute functionality
  const toggleMute = (e) => {
    e.stopPropagation(); // Prevent event propagation
    videoRef.current.muted = !isMuted;
    setIsMuted(!isMuted);
  };

  // Full-screen functionality
  const toggleFullScreen = (e) => {
    e.stopPropagation(); // Prevent event propagation
    if (!isFullScreen) {
      videoRef.current.requestFullscreen();
    } else {
      document.exitFullscreen();
    }
    setIsFullScreen(!isFullScreen);
  };

  // Update progress bar
  const handleTimeUpdate = (e) => {
    e.stopPropagation(); // Prevent event propagation
    const progress = (videoRef.current.currentTime / videoRef.current.duration) * 100;
    setProgress(progress);
  };

  // Rewind 10 seconds
  const rewind = (e) => {
    e.stopPropagation(); // Prevent event propagation
    videoRef.current.currentTime = Math.max(0, videoRef.current.currentTime - 10);
  };

  // Forward 10 seconds
  const forward = (e) => {
    e.stopPropagation(); // Prevent event propagation
    videoRef.current.currentTime = Math.min(
      videoRef.current.duration,
      videoRef.current.currentTime + 10
    );
  };

  // Change playback speed
  const changePlaybackSpeed = (speed) => {
    videoRef.current.playbackRate = speed;
    setPlaybackSpeed(speed);
  };

  // Show controls on mouse activity
  useEffect(() => {
    const timer = setTimeout(() => setShowControls(false), 3000);
    return () => clearTimeout(timer);
  }, [showControls]);

  useEffect(() => {
    const video = videoRef.current;

    // Attempt to play the video automatically if controlsAppear is false
    if (!controlsAppear) {
      const playPromise = video.play();
      if (playPromise !== undefined) {
        playPromise.catch(error => {
          console.log('Autoplay was prevented. Attempting to play after user interaction.');
          // Add a click event listener to attempt to play the video after user interaction
          const handleUserInteraction = () => {
            video.play();
            document.removeEventListener('click', handleUserInteraction);
          };
          document.addEventListener('click', handleUserInteraction);
        });
      }
    }

    if (!controlsAppear) {
      // Disable the ability to pause the video
      const handlePlay = () => {
        video.play();
      };

      video.addEventListener('pause', handlePlay);

      return () => {
        video.removeEventListener('pause', handlePlay);
      };
    }
  }, [controlsAppear]);

  return (
    <div
      className="movie-player-container"
      onMouseMove={() => setShowControls(controlsAppear)} // Show controls on mouse move
      onClick={controlsAppear ? togglePlayPause : null} // Play/pause when clicking anywhere on the video if controlsAppear is true
    >
      <video
        ref={videoRef}
        className="video-player"
        onTimeUpdate={handleTimeUpdate}
        controls={controlsAppear}
        muted={!controlsAppear}
        loop={!controlsAppear}
        autoPlay={!controlsAppear} // Autoplay if controlsAppear is false
      >
        <source src={videoUrl} type="video/mp4" />
        Your browser does not support the video tag.
      </video>
      {/* Big Center Play/Pause Button */}
      {controlsAppear && showControls && (
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
      {controlsAppear && showControls && (
        <div className="video-controls">
          <button onClick={rewind} className="control-button">
            ⏪ 10s
          </button>
          <button onClick={togglePlayPause} className="control-button">
            {isPlaying ? "❚❚" : "▶"}
          </button>
          <button onClick={forward} className="control-button">
            10s ⏩
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
          <select
            className="speed-selector"
            value={playbackSpeed}
            onChange={(e) => {
              e.stopPropagation(); // Prevent event propagation
              changePlaybackSpeed(Number(e.target.value))
            }}
          >
            <option value={0.5}>0.5x</option>
            <option value={1}>1x</option>
            <option value={1.5}>1.5x</option>
            <option value={2}>2x</option>
          </select>
          <button onClick={toggleFullScreen} className="control-button">
            {isFullScreen ? "⤬" : "⤢"}
          </button>
        </div>
      )}
    </div>
  );
}

export default MoviePlayer;