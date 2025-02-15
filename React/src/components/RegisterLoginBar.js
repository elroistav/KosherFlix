import React, { useState, useRef, useEffect } from 'react';
import { Link } from 'react-router-dom';
import '../styles/WelcomeBar.css';

/**
 * RegisterLoginBar component renders a navigation bar with a logo and a profile dropdown.
 *
 * @component
 * @param {Object} props - The component props.
 * @param {Function} props.onSearchResults - Callback function to handle search results.
 * @param {Function} props.clearSearchResults - Callback function to clear search results.
 *
 * @returns {JSX.Element} The rendered RegisterLoginBar component.
 */
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
        KOSHERFLIX
      </Link>

      {/* Login Button on the Right */}
      
    </div>
  );
}

export default RegisterLoginBar;
