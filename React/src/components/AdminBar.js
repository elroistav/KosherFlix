import React from 'react';
import '../styles/AdminBar.css';  // ייבוא קובץ CSS לעיצוב ה-adminBar

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
