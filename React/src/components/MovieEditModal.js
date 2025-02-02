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
      e.preventDefault(); // מונע את ההתנהגות הברירת מחדל של הטופס
      try {
          console.log('Form submitted'); // לוג ראשוני
          console.log('Form data:', formData); // הדפסת המידע בטופס
          console.log('onSave function:', onSave); // וודא שהפונקציה קיימת
   
          // נקה שגיאות קודמות
          setLocalError(null);
   
          // צור FormData חדש
          const formDataToSend = new FormData();
   
          // המר קטגוריות למזהים
          const categoryIds = await converCategoriesToIDs(formData.categories, );
          console.log('Category IDs:', categoryIds);
   
          // הוסף שדות רגילים
          formDataToSend.append('title', formData.title);
          formDataToSend.append('description', formData.description);
          formDataToSend.append('rating', formData.rating);
          formDataToSend.append('length', formData.length);
          formDataToSend.append('director', formData.director);
          formDataToSend.append('releaseDate', formData.releaseDate);
          formDataToSend.append('language', formData.language);
          formDataToSend.append('ageRestriction', formData.ageRestriction);
   
          // הוסף קטגוריות
          categoryIds.forEach(id => formDataToSend.append('categories[]', id));
   
          // הוסף קבצים אם קיימים
          if (formData.thumbnail) {
              console.log('Thumbnail file:', formData.thumbnail);
              formDataToSend.append('thumbnail', formData.thumbnail);
          }
   
          if (formData.videoUrl) {
              console.log('Video file:', formData.videoUrl);
              formDataToSend.append('videoUrl', formData.videoUrl);
          }
   
          // הוסף שדות נוספים
          formData.cast.forEach((castMember) => {
              formDataToSend.append('cast[]', castMember);
          });
          formData.subtitles.forEach((subtitle) => {
              formDataToSend.append('subtitles[]', subtitle);
          });
   
          // וודא שישנה פונקציית onSave
          if (typeof onSave !== 'function') {
              console.error('onSave is not a valid function');
              setLocalError('Error: Save function is not defined');
              return;
          }
   
          // קרא לפונקציית השמירה
          console.log('Calling onSave with FormData');
          await onSave(formDataToSend);
   
          console.log('Save completed successfully');
      } catch (error) {
          // טיפול בשגיאות
          console.error('Full error in submit:', error);
          
          if (error.response) {
              // שגיאה מהשרת
              console.error('Server error response:', error.response.data);
              setLocalError(error.response.data.message || 'Failed to save movie');
          } else if (error.request) {
              // הבקשה נשלחה אך לא התקבלה תגובה
              console.error('No response received');
              setLocalError('No response from server');
          } else {
              // שגיאה אחרת
              console.error('Error', error.message);
              setLocalError(error.message || 'An unexpected error occurred');
          }
   
          // טיפול בשגיאה ספציפית של כפילות
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