import React, { useState } from 'react';
import '../styles/MovieEditModal.css';

const MovieEditModal = ({ movie, isOpen, onClose, onSave, error: propError }) => {
    const [localError, setLocalError] = useState(null);

      // Format date for input field
    const formatDate = (dateString) => {
        if (!dateString) return '';
        const date = new Date(dateString);
        return date.toISOString().split('T')[0];
    };

    const [formData, setFormData] = useState({
        title: movie.title || '',
        description: movie.description || '',
        thumbnail: movie.thumbnail || '',
        videoUrl: movie.videoUrl || '',
        rating: movie.rating || 0,
        length: movie.length || 0,
        director: movie.director || '',
        releaseDate: formatDate(movie.releaseDate),
        language: movie.language || '',
        categories: movie.categories || [],
        cast: movie.cast || [],
        ageRestriction: movie.ageRestriction || '',
        subtitles: movie.subtitles || [],
    });

    const handleSubmit = async (e) => {
        e.preventDefault();
        const updateData = {
            ...formData,
            categories: movie.categories
        };

        try {
            setLocalError(null);
            await onSave(updateData);
        } catch (error) {
            if (error.response?.data?.code === 11000) {
                setLocalError('A movie with this title already exists. Please choose a different title.');
            }
        }
    };

  if (!isOpen) return null;

  return (
    <div className="modal-overlay">
      <div className="modal-content">
        <h2>Edit Movie</h2>
        {(propError || localError) && (
            <div className="error-alert">
                {propError || localError}
            </div>
        )}
        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label>Title:</label>
            <input
              type="text"
              value={formData.title}
              onChange={(e) => setFormData({...formData, title: e.target.value})}
            />
          </div>
          <div className="form-group">
            <label>Description:</label>
            <textarea
              value={formData.description}
              onChange={(e) => setFormData({...formData, description: e.target.value})}
            />
          </div>
          <div className="form-group">
            <label>Thumbnail URL:</label>
            <input
              type="text"
              value={formData.thumbnail}
              onChange={(e) => setFormData({...formData, thumbnail: e.target.value})}
            />
          </div>
          <div className="form-group">
            <label>Video URL:</label>
            <input
              type="text"
              value={formData.videoUrl}
              onChange={(e) => setFormData({...formData, videoUrl: e.target.value})}
            />
          </div>
          <div className="form-group">
            <label>Rating:</label>
            <input
              type="number"
              min="0"
              max="10"
              step="0.1"
              value={formData.rating}
              onChange={(e) => setFormData({...formData, rating: e.target.value})}
            />
          </div>
          <div className="form-group">
            <label>Length (minutes):</label>
            <input
              type="number"
              min="1"
              value={formData.length}
              onChange={(e) => setFormData({...formData, length: e.target.value})}
            />
          </div>
          <div className="form-group">
            <label>Director:</label>
            <input
              type="text"
              value={formData.director}
              onChange={(e) => setFormData({...formData, director: e.target.value})}
            />
          </div>
          <div className="form-group">
            <label>Release Date:</label>
            <input
              type="date"
              value={formData.releaseDate}
              onChange={(e) => setFormData({...formData, releaseDate: e.target.value})}
            />
          </div>
          <div className="form-group">
            <label>Language:</label>
            <input
              type="text"
              value={formData.language}
              onChange={(e) => setFormData({...formData, language: e.target.value})}
            />
          </div>
          <div className="form-group">
                    <label>Cast (comma separated):</label>
                    <input
                        type="text"
                        value={formData.cast.join(', ')}
                        onChange={(e) => setFormData({
                            ...formData, 
                            cast: e.target.value.split(',').map(item => item.trim())
                        })}
                    />
                </div>
                <div className="form-group">
                    <label>Age Restriction:</label>
                    <input
                        type="number"
                        min="0"
                        value={formData.ageRestriction}
                        onChange={(e) => setFormData({
                            ...formData, 
                            ageRestriction: parseInt(e.target.value)
                        })}
                    />
                </div>
                <div className="form-group">
                    <label>Subtitles (comma separated):</label>
                    <input
                        type="text"
                        value={formData.subtitles.join(', ')}
                        onChange={(e) => setFormData({
                            ...formData, 
                            subtitles: e.target.value.split(',').map(item => item.trim())
                        })}
                    />
                </div>
          <div className="modal-buttons">
            <button type="submit">Save Changes</button>
            <button type="button" onClick={onClose}>Cancel</button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default MovieEditModal;