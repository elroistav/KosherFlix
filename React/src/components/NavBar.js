import React, { useState, useEffect, useRef } from 'react';
import { Link } from 'react-router-dom';
import { FaSearch } from 'react-icons/fa'; // Importing the search icon
import '../styles/NavBar.css';

function Navbar() {
    const [searchOpen, setSearchOpen] = useState(false);
    const [searchQuery, setSearchQuery] = useState('');
    const searchPanelRef = useRef(null);
  
    const handleSearchClick = () => {
      setSearchOpen(!searchOpen);
    };
  
    const handleSearchChange = (e) => {
      setSearchQuery(e.target.value);
    };
  
    const handleSearchSubmit = (e) => {
      e.preventDefault();
      // Implement your search logic here
      console.log('Searching for:', searchQuery);
    };

    const handleClickOutside = (event) => {
        if (searchPanelRef.current && !searchPanelRef.current.contains(event.target)) {
          setSearchOpen(false);
        }
      };

    useEffect(() => {
    if (searchOpen) {
        document.addEventListener('mousedown', handleClickOutside);
    } else {
        document.removeEventListener('mousedown', handleClickOutside);
    }

    return () => {
        document.removeEventListener('mousedown', handleClickOutside);
    };
    }, [searchOpen]);

  return (
    <div className="navbar">
      {/* Logo */}
      <div className="logo">
        Netflick
      </div>

      {/* Navbar Links */}
      <div className="nav-links">
        <Link to="/">Home</Link>
        <Link to="/movies">Movies</Link>
        <Link to="/tv-shows">TV Shows</Link>
        <Link to="/my-list">My List</Link>
      </div>

      {/* Profile and Search Icons */}
      <div className="profile-and-search">
        {/* Search Icon */}
        <FaSearch className="search-icon" onClick={handleSearchClick} />

        {/* Profile Icon */}
        <div className="profile-icon">
          <img src="https://example.com/profile-pic.jpg" alt="Profile" />
        </div>
        {/* Search Panel */}
        {searchOpen && (
            <div className="search-panel" ref={searchPanelRef}>
            <form onSubmit={handleSearchSubmit}>
                <input
                type="text"
                value={searchQuery}
                onChange={handleSearchChange}
                placeholder="Search for movies, TV shows..."
                />
                <button type="submit">Search</button>
            </form>
            </div>
        )}
      </div>
    </div>
  );
}

export default Navbar;
