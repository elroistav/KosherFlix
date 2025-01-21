import React from 'react';
import { useNavigate } from 'react-router-dom';
import '../styles/MovieControls.css';

function MovieControls({
  isPlaying,
  isMuted,
  isFullScreen,
  progress,
  playbackSpeed,
  onRewind,
  onPlayPause,
  onForward,
  onProgressChange,
  onMute,
  onSpeedChange,
  onFullScreen,
  onBack
}) {
    const navigate = useNavigate();
    return (
        <>
        /* Back Button */
        <button onClick={() => navigate(-1)} className="back-button">
            ← Back
        </button>

        /* Video Controls */
          <div className="video-controls" onClick={(e) => e.stopPropagation()}>
            <button onClick={onRewind} className="control-button">
              ⏪ 10s
            </button>
            <button onClick={onPlayPause} className="control-button">
              {isPlaying ? "❚❚" : "▶"}
            </button>
            <button onClick={onForward} className="control-button">
              10s ⏩
            </button>
            <input
              type="range"
              className="progress-bar"
              value={progress}
              onChange={onProgressChange}
            />
            <button onClick={onMute} className="control-button">
              {isMuted ? "🔇" : "🔊"}
            </button>
            <div>
              <select
                className="speed-selector"
                value={playbackSpeed}
                onChange={onSpeedChange}
              >
                <option value={0.5}>0.5x</option>
                <option value={1}>1x</option>
                <option value={1.5}>1.5x</option>
                <option value={2}>2x</option>
              </select>
              <button onClick={onFullScreen} className="control-button">
                {isFullScreen ? "⤬" : "⤢"}
              </button>
            </div>
          </div>
        </>
      );
    }

export default MovieControls;