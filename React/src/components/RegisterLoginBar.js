import React, { useState, useRef, useEffect } from 'react';
import { Link } from 'react-router-dom';
import '../styles/WelcomeBar.css';

function RegisterLoginBar({ onSearchResults, clearSearchResults }) {
  const [dropdownOpen, setDropdownOpen] = useState(false);
  const dropdownRef = useRef(null);
  
  const handleProfileClick = () => {
    setDropdownOpen(!dropdownOpen);
  };

  return (
    <div className="WelcomeBar">
      {/* Logo */}
      <Link to="/" className="logo" onClick={clearSearchResults}>
        Netflick
      </Link>

      {/* Login Button on the Right */}
      
    </div>
  );
}

export default RegisterLoginBar;
