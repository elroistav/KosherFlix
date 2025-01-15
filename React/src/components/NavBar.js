import React from 'react';
import { FaSearch } from 'react-icons/fa'; // Importing the search icon
import '../styles/NavBar.css';

function Navbar() {
  return (
    <div className="navbar">
      {/* Logo */}
      <div className="logo">
        Netflick
      </div>

      {/* Navbar Links */}
      <div className="nav-links">
        <a href="#home">Home</a>
        <a href="#movies">Movies</a>
        <a href="#tv-shows">TV Shows</a>
        <a href="#my-list">My List</a>
      </div>

      {/* Profile and Search Icons */}
      <div className="profile-and-search">
        {/* Search Icon */}
        <FaSearch className="search-icon" />

        {/* Profile Icon */}
        <div className="profile-icon">
          <img src="https://example.com/profile-pic.jpg" alt="Profile" />
        </div>
      </div>
    </div>
  );
}

export default Navbar;
