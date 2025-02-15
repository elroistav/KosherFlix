import React, { useState, useRef, useEffect } from 'react';
import { Link } from 'react-router-dom';
import '../styles/WelcomeBar.css';

/**
 * WelcomeBar component renders a navigation bar with a logo and a login button.
 *
 * @component
 * @example
 * return (
 *   <WelcomeBar />
 * )
 */
function WelcomeBar() {
  return (
    <div className="WelcomeBar">
      {/* Logo */}
      <Link to="/" className="logo" >
        KOSHERFLIX
      </Link>

      {/* Login Button on the Right */}
      <div className="login-button">
        <Link to="/login">Login</Link>
      </div>
    </div>
  );
}

export default WelcomeBar;
