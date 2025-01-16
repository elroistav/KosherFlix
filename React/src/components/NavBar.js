import React, { useState, useEffect, useRef } from 'react';
import { Link } from 'react-router-dom';
import axios from 'axios';
import { FaSearch } from 'react-icons/fa'; // Importing the search icon
import '../styles/NavBar.css';

function Navbar( {onSearchResults} ) {
    const [searchOpen, setSearchOpen] = useState(false);
    const [searchQuery, setSearchQuery] = useState('');
    const searchPanelRef = useRef(null);
  
    const handleSearchClick = () => {
      setSearchOpen(!searchOpen);
    };
  
    const handleSearchChange = (e) => {
      setSearchQuery(e.target.value);
    };
  
    const handleSearchSubmit = async (e) => {
      e.preventDefault();
      console.log('Searching for:', searchQuery);
      try {
        const response = await axios.get(`http://localhost:4000/api/movies/search/${searchQuery}`, {
          headers: { 'user-id': '6788f8771a6c2941d023825c' }
        });
        onSearchResults(response.data);
      } catch (error) {
        console.error('Error fetching search results:', error);
      }
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
