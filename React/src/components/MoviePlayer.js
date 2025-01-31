import React, { useRef, useState, useEffect } from "react";
import MovieControls from "./MovieControls";
import "../styles/MoviePlayer.css";

function MoviePlayer({ videoUrl, controlsAppear = true, onEnded }) {
  const BASE_URL = process.env.REACT_APP_BASE_URL;
  const videoRef = useRef(null);
  const [isPlaying, setIsPlaying] = useState(false);
  const [isMuted, setIsMuted] = useState(!controlsAppear);
  const [isFullScreen, setIsFullScreen] = useState(false);
  const [showControls, setShowControls] = useState(true);
  const [progress, setProgress] = useState(0);
  const [playbackSpeed, setPlaybackSpeed] = useState(1); // Default speed is 1x
  const [fullVideoUrl, setFullVideoUrl] = useState("");

  useEffect(() => {
    if (!BASE_URL || !videoUrl) return; // Prevent running with invalid values

    // Fetch the full video URL from the server
    const fetchVideoUrl = async () => {
      try {
        //const response = await fetch(`${BASE_URL}/${videoUrl}`);
        if (true) {
          console.log("full url: ", `${BASE_URL}/${videoUrl}`);
          //const data = await response.json();
          setFullVideoUrl(`${BASE_URL}/${videoUrl}`); // Set the video URL directly
        } else {
          console.error("Failed to fetch video URL");
        }
      } catch (error) {
        console.log(BASE_URL + "/" + videoUrl);
        console.error("Error fetching video URL:", error);
      }
    };

    fetchVideoUrl();
  }, [videoUrl]);

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
    videoRef.current.requestFullscreen();
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

    if (!controlsAppear) {
      if (!video) return; // Ensure video exists before adding event listener
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
      {fullVideoUrl ? ( // Only render if fullVideoUrl is not empty
      <video
        ref={videoRef}
        className="video-player"
        onTimeUpdate={handleTimeUpdate}
        onEnded={onEnded}
        muted={!controlsAppear}
        loop={!controlsAppear}
        autoPlay={!controlsAppear}
      >
        <source src={fullVideoUrl} type="video/mp4" />
        Your browser does not support the video tag.
      </video>
    ) : (
      <p>Loading video...</p> // Optional: Display a loading message instead of a broken video element
    )}
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
        <MovieControls
          isPlaying={isPlaying}
          isMuted={isMuted}
          isFullScreen={isFullScreen}
          progress={progress}
          playbackSpeed={playbackSpeed}
          onRewind={(e) => {
            e.stopPropagation();
            rewind(e);
          }}
          onPlayPause={(e) => {
            e.stopPropagation();
            togglePlayPause();
          }}
          onForward={(e) => {
            e.stopPropagation();
            forward(e);
          }}
          onProgressChange={(e) => {
            e.stopPropagation();
            const newTime = (e.target.value / 100) * videoRef.current.duration;
            videoRef.current.currentTime = newTime;
            setProgress(e.target.value);
          }}
          onMute={(e) => {
            e.stopPropagation();
            toggleMute(e);
          }}
          onSpeedChange={(e) => {
            e.stopPropagation();
            changePlaybackSpeed(Number(e.target.value));
          }}
          onFullScreen={(e) => {
            e.stopPropagation();
            toggleFullScreen(e);
          }}
        />
      )}
    </div>
  );
}

export default MoviePlayer;