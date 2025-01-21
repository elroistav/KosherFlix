import React from 'react';
import { FaEdit, FaTrash } from 'react-icons/fa';
import '../styles/AdminMovieCard.css';

function AdminMovieCard({ movie, onClick }) {
  return (
    <div
        className="movie-card"
        onClick={onClick}
        style={{
            backgroundImage: `url(${movie.thumbnail})`, // Set movie thumbnail dynamically
            backgroundSize: 'cover',
            backgroundPosition: 'center',
        }}
    >
        <p>{movie.title}</p> {/* Display the movie title */}
        <button className="delete-movie-button"><FaTrash /></button>
        <button className="edit-movie-button"><FaEdit /></button>
    </div>
  );
}

export default AdminMovieCard;
