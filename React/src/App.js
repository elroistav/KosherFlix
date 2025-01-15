import React from 'react';
//import './pages/HomeScreen.css';

function HomeScreen() {
  return (
    <div>
      <div className="overlay"></div>
      <h1 className="welcome-text">Welcome to Netflix</h1>
      <div className="button-container">
        <button>Play</button>
        <button>My List</button>
      </div>
    </div>
  );
}

export default HomeScreen;
