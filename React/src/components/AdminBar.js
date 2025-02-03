import React from 'react';
import '../styles/AdminBar.css';

/**
 * AdminBar component renders a bar with options to add a movie or a category.
 *
 * @param {Object} props - The properties object.
 * @param {Function} props.onAddMovie - Callback function to handle adding a movie.
 * @param {Function} props.onAddCategory - Callback function to handle adding a category.
 * @returns {JSX.Element} The AdminBar component.
 */
const AdminBar = ({ onAddMovie, onAddCategory }) => {
  return (
    <div className="admin-bar">
      <span className="admin-mode-text">Admin Mode</span>
      <button onClick={onAddMovie} className="admin-btn">Add Movie</button>
      <button onClick={onAddCategory} className="admin-btn">Add Category</button>
    </div>
  );
};

export default AdminBar;
