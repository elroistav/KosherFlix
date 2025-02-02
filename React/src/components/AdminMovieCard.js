import React, { useState, useEffect } from 'react';
import ReactDOM from 'react-dom';
import axios from 'axios';
import { FaEdit, FaTrash } from 'react-icons/fa';
import MovieEditModal from './MovieEditModal';
import '../styles/AdminMovieCard.css';

function AdminMovieCard({ movie, onClick, onMovieUpdate, onMovieDelete, userInfo, loading }) {
    const BASE_URL = process.env.REACT_APP_BASE_URL;
    const [isModalOpen, setIsModalOpen] = useState(false);
    const [isSaving, setIsSaving] = useState(false);
    const [error, setError] = useState(null);
    const [movieData, setMovieData] = useState(movie);


    console.log("movie.thumbnail**: " + movie.thumbnail);
    console.log("full thumbnail url: **" + `${BASE_URL}/${movie.thumbnail}`);

    // Add useEffect to update movieData when movie prop changes
    useEffect(() => {
        setMovieData(movie);
    }, [movie]);
  
    const handleEditClick = (e) => {
      if (loading || !userInfo) {
        console.error('Cannot edit movie: userInfo is not ready yet.');
        setError('Cannot edit movie while data is loading.');
        return;
    }
        console.log('Edit clicked:', movie);
        console.log('Movie data:', movieData);
      e.stopPropagation();
      setIsModalOpen(true);
      setError(null);
      document.body.style.overflow = 'hidden';
    };
  
    const handleCloseModal = () => {
      setIsModalOpen(false);
      setError(null);
      document.body.style.overflow = 'unset';
    };
  
    const validateMovie = (formData) => {
      const required = [
          'title', 
          'description', 
          'rating', 
          'length',
          'director',
          'releaseDate',
          'language'
      ];
      
      const missing = required.filter(field => {
          // עבור FormData, בודקים את הערך באמצעות get
          const value = formData.get(field);
          return !value || value.trim() === '';
      });
      
      // בדיקה נפרדת לקבצים
      if (!formData.get('thumbnail')) {
          missing.push('thumbnail');
      }
      
      if (!formData.get('videoUrl')) {
          missing.push('videoUrl');
      }
      
      if (missing.length > 0) {
          return `Missing required fields: ${missing.join(', ')}`;
      }
      return null;
  };
    
    const handleSave = async (updatedMovie) => {
      if (loading || !userInfo) {
        console.error('Cannot save movie: userInfo is not ready yet.');
        setError('Cannot save movie while data is loading.');
        return;
    }
    
      try {
        console.log('Starting save with data:', updatedMovie);
        setIsSaving(true);
        setError(null);

        const validationError = validateMovie(updatedMovie);
        if (validationError) {
            console.log('Validation failed:', validationError);
            setError(validationError);
            setIsSaving(false);
            return;
        }

        const response = await axios.put(
          `${BASE_URL}/api/movies/${movie._id}`, 
          updatedMovie, 
          { 
              headers: { 
                  'user-id': userInfo.userId,
                  'Content-Type': 'multipart/form-data'
              }
          }
      );

        console.log('API Response:', response.data);
        setMovieData(response.data); // Update local state
        
        if (onMovieUpdate) {
            console.log('Calling onMovieUpdate with:', response.data);
            onMovieUpdate(response.data); // Notify parent
        }
        
        handleCloseModal();
        window.location.reload(); // Refresh the main page
      } catch (error) {
        console.error('Save error:', error);
        const errorMessage = error.response?.status === 400 
            ? error.response?.data?.message || 'Invalid movie data'
            : 'Failed to update movie';
        setError(errorMessage);
      } finally {
        setIsSaving(false);
      }
    };

    const handleDelete = async (e) => {
        e.stopPropagation(); // Prevent card click

        if (loading || !userInfo) {
          console.error('Cannot delete category: userInfo is not ready yet.');
          return;
      }
        
        if (window.confirm('Are you sure you want to delete this movie?')) {
            try {
              console.log('user id is: ' + JSON.stringify(userInfo));
                await axios.delete(
                    `${BASE_URL}/api/movies/${movie._id}`,
                    { headers: { 'user-id': userInfo.userId }}
                  );
                
                // Notify parent of deletion
                if (onMovieDelete) {
                    onMovieDelete(movie._id);
                }
            } catch (error) {
                console.error('Delete error:', error);
                setError('Failed to delete movie');
            }
        }
    };

  return (
    <>
      <div
        className="movie-card"
        onClick={onClick}
        style={{
          backgroundImage: `url(${BASE_URL}/${movie.thumbnail})`,
          backgroundSize: 'cover',
          backgroundPosition: 'center',
        }}
      >
        <p>{movie.title}</p>
        <button className="delete-movie-button" onClick={handleDelete}><FaTrash /></button>
        <button className="edit-movie-button" onClick={handleEditClick}><FaEdit /></button>
      </div>
      {isModalOpen && ReactDOM.createPortal(
        <MovieEditModal 
            movie={movieData}
            isOpen={isModalOpen}
            onClose={handleCloseModal}
            onSave={handleSave}
            isSaving={isSaving}
            error={error}
            userInfo={userInfo}
        />,
        document.body
        )}
    </>
  );
}

export default AdminMovieCard;