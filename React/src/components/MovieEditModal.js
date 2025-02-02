import React, { useState } from 'react';
import axios from 'axios';
import '../styles/MovieEditModal.css';

const MovieEditModal = ({ movie, isOpen, onClose, onSave, error: propError, userInfo }) => {
    const BASE_URL = process.env.REACT_APP_BASE_URL;
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
        thumbnail: null, // Changed to support file upload
        videoUrl: null, // Changed to support file upload
        rating: movie.rating || 0,
        length: movie.length || 0,
        director: movie.director || '',
        releaseDate: formatDate(movie.releaseDate),
        language: movie.language || '',
        categories: movie.categories.map(cat => cat.name).join(', '), // Convert category objects to names
        cast: movie.cast || [],
        ageRestriction: movie.ageRestriction || '',
        subtitles: movie.subtitles || [],
    });

    // File change handler
    const handleFileChange = (e, field) => {
        setFormData({ ...formData, [field]: e.target.files[0] });
    };

    // Category change handler
    const handleCategoryChange = (e) => {
        setFormData({ ...formData, categories: e.target.value });
    };

    // Convert categories to IDs
    const converCategoriesToIDs = async (categories) => {
        const categoriesIDs = [];
        
        // Call the server to get all categories
        const response = await axios.get(BASE_URL + '/api/categories',
                                         { headers: { 'user-id': userInfo.userId } });
        const allCategories = response.data.categories;

        // For each category in the categories array
        categories.split(',').map(item => item.trim()).forEach((category) => {
            // Search for the category in the categories array
            const categoryData = allCategories.find((cat) => cat.name === category);
            if (categoryData) {
                // If the category exists, add its ID to the categories array
                categoriesIDs.push(categoryData._id);
            }
        });

        return categoriesIDs;
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            setLocalError(null);

            // Create FormData
            const formDataToSend = new FormData();

            const categoryIds = await converCategoriesToIDs(formData.categories);

            // Add regular fields
            formDataToSend.append('title', formData.title);
            formDataToSend.append('description', formData.description);
            formDataToSend.append('rating', formData.rating);
            formDataToSend.append('length', formData.length);
            formDataToSend.append('director', formData.director);
            formDataToSend.append('releaseDate', formData.releaseDate);
            formDataToSend.append('language', formData.language);
            formDataToSend.append('ageRestriction', formData.ageRestriction);

            // Add categories
            categoryIds.forEach(id => formDataToSend.append('categories[]', id));

            // Add files if they exist
            if (formData.thumbnail) {
                formDataToSend.append('thumbnail', formData.thumbnail);
            }

            if (formData.videoUrl) {
                formDataToSend.append('videoUrl', formData.videoUrl);
            }

            // Add other fields
            formData.cast.forEach((castMember) => formDataToSend.append('cast[]', castMember));
            formData.subtitles.forEach((subtitle) => formDataToSend.append('subtitles[]', subtitle));

            // Call the function that sends the data
            await onSave(formDataToSend);
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
                            onChange={(e) => setFormData({ ...formData, title: e.target.value })}
                        />
                    </div>
                    <div className="form-group">
                        <label>Description:</label>
                        <textarea
                            value={formData.description}
                            onChange={(e) => setFormData({ ...formData, description: e.target.value })}
                        />
                    </div>
                    <div className="form-group">
                        <label>Thumbnail (Image):</label>
                        <input
                            type="file"
                            accept="image/*"
                            onChange={(e) => handleFileChange(e, 'thumbnail')}
                        />
                    </div>
                    <div className="form-group">
                        <label>Video Clip (Video):</label>
                        <input
                            type="file"
                            accept="video/*"
                            onChange={(e) => handleFileChange(e, 'videoUrl')}
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
                            onChange={(e) => setFormData({ ...formData, rating: e.target.value })}
                        />
                    </div>
                    <div className="form-group">
                        <label>Length (minutes):</label>
                        <input
                            type="number"
                            min="1"
                            value={formData.length}
                            onChange={(e) => setFormData({ ...formData, length: e.target.value })}
                        />
                    </div>
                    <div className="form-group">
                        <label>Director:</label>
                        <input
                            type="text"
                            value={formData.director}
                            onChange={(e) => setFormData({ ...formData, director: e.target.value })}
                        />
                    </div>
                    <div className="form-group">
                        <label>Release Date:</label>
                        <input
                            type="date"
                            value={formData.releaseDate}
                            onChange={(e) => setFormData({ ...formData, releaseDate: e.target.value })}
                        />
                    </div>
                    <div className="form-group">
                        <label>Language:</label>
                        <input
                            type="text"
                            value={formData.language}
                            onChange={(e) => setFormData({ ...formData, language: e.target.value })}
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
                    <div className="form-group">
                        <label>Categories (comma separated):</label>
                        <input
                            type="text"
                            value={formData.categories}
                            onChange={handleCategoryChange}
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