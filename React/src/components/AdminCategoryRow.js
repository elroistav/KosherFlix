import React from 'react';
import { FaTrash } from 'react-icons/fa';
import AdminMovieCard from './AdminMovieCard';
import '../styles/AdminCategoryRow.css';

function AdminCategoryRow({ categoryName, movies, onMovieClick }) {
return (
    <div className="category-row">
        <h3>{categoryName} <button className="delete-category-button"><FaTrash /></button></h3>
        <div className="category-movies">
            {movies.map((movie, index) => (
                <AdminMovieCard key={index} movie={movie} onClick={() => onMovieClick(movie._id)} />
            ))}
        </div>
    </div>
);
}

export default AdminCategoryRow;