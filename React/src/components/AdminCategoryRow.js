import React, { useState, useEffect } from 'react'; // Added useState and useEffect
import ReactDOM from 'react-dom';

import axios from 'axios';
import { FaEdit, FaTrash } from 'react-icons/fa'; // Added FaEdit
import AdminMovieCard from './AdminMovieCard';
import CategoryEditModal from './CategoryEditModal'; // Added the category edit modal
import '../styles/AdminCategoryRow.css';

function AdminCategoryRow({ category, onMovieClick, onMovieUpdate, onMovieDelete, onCategoryDelete, onCategoryUpdate, userInfo, loading }) {
    const BASE_URL = process.env.REACT_APP_BASE_URL;
    const [isSaving, setIsSaving] = useState(false);
    const [error, setError] = useState(null);
    const [isModalOpen, setIsModalOpen] = useState(false); // Added state for modal
    const [categoryData, setCategoryData] = useState(category); // Added state for category
    const [isEditModalOpen, setIsEditModalOpen] = useState(false);

    

    useEffect(() => { // Added useEffect to update category
        setCategoryData(category);
    }, [category]);

    const handleCategoryDelete = async (e) => {
        e.stopPropagation();

        if (loading || !userInfo) {
            console.error('Cannot delete category: userInfo is not ready yet.');
            return;
        }

        if (window.confirm(`Are you sure you want to delete the category "${category.category}"?`)) {
            try {
                await axios.delete(
                    `${BASE_URL}/api/categories/${category._id}`,
                    { headers: { 'user-id': userInfo.userId } }
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
        if (loading || !userInfo) {
            console.error('Cannot delete movie: userInfo is not ready yet.');
            return;
        }

        if (onMovieDelete) {
            onMovieDelete(movieId);
        }
    };

    // Added function to open the category edit modal
    const handleCategoryEdit = (e) => {
        e.stopPropagation();

        if (loading || !userInfo) {
            console.error('Cannot edit category: userInfo is not ready yet.');
            return;
        }

        setIsModalOpen(true);
        document.body.style.overflow = 'hidden'; // Disable scrolling when modal is open
    };

    // Added function to close the modal
    const handleCloseModal = () => {
        setIsModalOpen(false);
        document.body.style.overflow = 'unset'; // Enable scrolling when modal is closed
    };

    // Added function to save the category
    const handleSaveCategory = async (updatedCategory) => {
        if (loading || !userInfo) {
            console.error('Cannot save category: userInfo is not ready yet.');
            return;
        }

        setIsSaving(true);
        setError(null);

        try {
            console.log('Saving category:', updatedCategory);
            console.log('user id is: ' + JSON.stringify(userInfo));
            console.log('category is: ' + JSON.stringify(category));
            console.log('category id is: ' + JSON.stringify(category._id));

            if (!category || !category._id) {
                console.error('Invalid category ID:', category);
                return;
            } 
            const response = await axios.patch(
                `${BASE_URL}/api/categories/${category._id}`,
                updatedCategory,
                { headers: { 'user-id': userInfo.userId }}
            );
    
            // Update local state
            setCategoryData(response.data);
            if (onCategoryUpdate) {
                onCategoryUpdate(response.data); // Update parent
            }
    
            handleCloseModal(); // Close modal
        } catch (error) {
            // Handle errors and update error state
            console.error('Error saving category:', error);
            setError('An error occurred while saving the category. Please try again.');
        } finally {
            // Finish saving
            setIsSaving(false);
        }
    };

    return (
        <div className="category-row">
            <h3>
                {category.category}
                <button className="delete-category-button" onClick={handleCategoryDelete}>
                    <FaTrash />
                </button>
                {/* Added button to edit the category */}
                <button className="edit-category-button" onClick={handleCategoryEdit}>
                    <FaEdit />
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
                        userInfo={userInfo}
                        loading={loading}
                    />
                ))}
            </div>

            {/* Added the category edit modal */}
            {isModalOpen && ReactDOM.createPortal(
                <CategoryEditModal
                    category={categoryData} 
                    onSave={handleSaveCategory} 
                    onClose={handleCloseModal}
                    isOpen={isModalOpen}
                    isSaving={isSaving}
                    error={error}
                />,
                document.body
                )}
        </div>
    );
}

export default AdminCategoryRow;