import React from 'react';
import axios from 'axios';
import { FaTrash } from 'react-icons/fa';
import AdminMovieCard from './AdminMovieCard';
import '../styles/AdminCategoryRow.css';

function AdminCategoryRow({ category, onMovieClick, onMovieUpdate, onMovieDelete, onCategoryDelete }) {
    console.log('Category:', category);
    console.log('Name: ', category.category);
    const handleCategoryDelete = async (e) => {
        e.stopPropagation();
        
        if (window.confirm(`Are you sure you want to delete the category "${category.category}"?`)) {
            try {
                await axios.delete(
                    `http://localhost:4000/api/categories/${category._id}`,
                    { headers: { 'user-id': '6790aeff2af1fd8ab364f8f3' }}
                );
                
                if (onCategoryDelete) {
                    onCategoryDelete(category._id);
                }
            } catch (error) {
                console.error('Error deleting category:', error);
            }
        }
    };

    const handleMovieDelete = (movieId) => {
        if (onMovieDelete) {
            onMovieDelete(movieId);
        }
    };

    return (
        <div className="category-row">
            <h3>
                {category.category} 
                <button className="delete-category-button" onClick={handleCategoryDelete}>
                    <FaTrash />
                </button>
            </h3>
            <div className="category-movies">
                {category.movies.map((movie) => (
                    <AdminMovieCard 
                        key={movie._id}
                        movie={movie} 
                        onClick={() => onMovieClick(movie._id)} 
                        onMovieUpdate={onMovieUpdate}
                        onMovieDelete={handleMovieDelete} 
                    />
                ))}
            </div>
        </div>
    );
}

export default AdminCategoryRow;