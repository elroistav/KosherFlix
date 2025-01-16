import React, { useState, useEffect, useRef } from 'react';
import { Link } from 'react-router-dom';
import axios from 'axios';
import { FaSearch } from 'react-icons/fa'; // Importing the search icon
import '../styles/NavBar.css';

function Navbar( { onSearchResults, clearSearchResults } ) {
    const [searchOpen, setSearchOpen] = useState(false);
    const [searchQuery, setSearchQuery] = useState('');
    const [noResults, setNoResults] = useState(false);
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
        if (response.data.length === 0) {
            setNoResults(true);
            setTimeout(() => setNoResults(false), 3000); // Hide the message after 3 seconds
        } else {
            setNoResults(false);
        }
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
      <Link to="/" className="logo" onClick={clearSearchResults}>
        Netflick
      </Link>

      {/* Navbar Links */}
      <div className="nav-links" onClick={clearSearchResults}>
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
            {noResults && <div className="no-results-toast">No results found</div>}
            </div>
        )}
      </div>
    </div>
  );
}

export default Navbar;
