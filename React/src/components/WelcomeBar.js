import React, { useState, useRef, useEffect } from 'react';
import { Link } from 'react-router-dom';
import '../styles/WelcomeBar.css';

function WelcomeBar() {

  


  return (
    <div className="WelcomeBar">
      {/* Logo */}
      <Link to="/" className="logo" >
        Netflick
      </Link>

      {/* Login Button on the Right */}
      <div className="login-button">
        <Link to="/login">Login</Link>
      </div>
    </div>
  );
}

export default WelcomeBar;
