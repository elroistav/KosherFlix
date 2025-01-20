import React, { useRef, useEffect } from "react";
import "../styles/HomeScreenVideo.css";

function HomeScreenVideo({ videoUrl }) {
  const videoRef = useRef(null);

  // Autoplay the video when the component loads
  useEffect(() => {
    videoRef.current.play().catch((error) => {
      console.error("Video autoplay failed:", error);
    });
  }, []);

  return (
    <div className="home-screen-video-container">
      <video
        ref={videoRef}
        className="home-screen-video"
        src={videoUrl}
        muted
        loop
        playsInline
      />
    </div>
  );
}

export default HomeScreenVideo;
