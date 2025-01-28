import React, { useState, useEffect } from 'react'; // **הוספתי useState ו-useEffect**
import ReactDOM from 'react-dom';

import axios from 'axios';
import { FaEdit, FaTrash } from 'react-icons/fa'; // **הוספתי את FaEdit**
import AdminMovieCard from './AdminMovieCard';
import CategoryEditModal from './CategoryEditModal'; // **הוספתי את המודל של עריכת קטגוריה**
import '../styles/AdminCategoryRow.css';

function AdminCategoryRow({ category, onMovieClick, onMovieUpdate, onMovieDelete, onCategoryDelete, onCategoryUpdate, userInfo, loading }) {
    console.log('Category:', category);
    console.log('Name: ', category.category);

    const [isSaving, setIsSaving] = useState(false);
    const [error, setError] = useState(null);
    const [isModalOpen, setIsModalOpen] = useState(false); // **הוספתי מצב למודל**
    const [categoryData, setCategoryData] = useState(category); // **הוספתי מצב לקטגוריה**

    useEffect(() => { // **הוספתי useEffect לעדכון קטגוריה**
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
                    `http://localhost:4000/api/categories/${category._id}`,
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

    // **הוספתי פונקציה שתפתח את המודל לעריכת קטגוריה**
    const handleCategoryEdit = (e) => {
        e.stopPropagation();

        if (loading || !userInfo) {
            console.error('Cannot edit category: userInfo is not ready yet.');
            return;
        }

        setIsModalOpen(true);
        document.body.style.overflow = 'hidden'; // Disable scrolling when modal is open
    };

    // **הוספתי פונקציה לסגור את המודל**
    const handleCloseModal = () => {
        setIsModalOpen(false);
        document.body.style.overflow = 'unset'; // Enable scrolling when modal is closed
    };

    // **הוספתי פונקציה לשמירה של הקטגוריה**
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
                `http://localhost:4000/api/categories/${category._id}`,
                updatedCategory,
                { headers: { 'user-id': userInfo.userId }}
            );
    
            // עדכון הסטייט המקומי
            setCategoryData(response.data);
            if (onCategoryUpdate) {
                onCategoryUpdate(response.data); // עדכון ההורה
            }
    
            handleCloseModal(); // סגירת המודל
        } catch (error) {
            // טיפול בטעויות ועדכון error
            console.error('Error saving category:', error);
            setError('An error occurred while saving the category. Please try again.');
        } finally {
            // סיום שמירה
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
                {/* **הוספתי כפתור לעריכת הקטגוריה** */}
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

            {/* **הוספתי את המודל של עריכת הקטגוריה** */}
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


// import React from 'react';
// import axios from 'axios';
// import { FaTrash } from 'react-icons/fa';
// import AdminMovieCard from './AdminMovieCard';
// import '../styles/AdminCategoryRow.css';

// function AdminCategoryRow({ category, onMovieClick, onMovieUpdate, onMovieDelete, onCategoryDelete, userInfo, loading }) {
//     console.log('Category:', category);
//     console.log('Name: ', category.category);

//     const handleCategoryDelete = async (e) => {
//         e.stopPropagation();

//         if (loading || !userInfo) { 
//             console.error('Cannot delete category: userInfo is not ready yet.');
//             return;
//         }
        
//         if (window.confirm(`Are you sure you want to delete the category "${category.category}"?`)) {
//             try {
//                 await axios.delete(
//                     `http://localhost:4000/api/categories/${category._id}`,
//                     { headers: { 'user-id': userInfo.userId } }
//                 );
                
//                 if (onCategoryDelete) {
//                     onCategoryDelete(category._id);
//                 }
//             } catch (error) {
//                 console.error('Error deleting category:', error);
//             }
//         }
//     };

//     const handleMovieDelete = (movieId) => {

//         if (loading || !userInfo) {  
//             console.error('Cannot delete movie: userInfo is not ready yet.');
//             return;
//         }

//         if (onMovieDelete) {
//             onMovieDelete(movieId);
//         }
//     };

//     return (
//         <div className="category-row">
//             <h3>
//                 {category.category} 
//                 <button className="delete-category-button" onClick={handleCategoryDelete}>
//                     <FaTrash />
//                 </button>
//                 <button className="add-movie-button" onClick={() => onMovieUpdate(category._id)}>
//                     <FaTrash />
//                 </button>
//             </h3>
//             <div className="category-movies">
//                 {category.movies.map((movie) => (
//                     <AdminMovieCard 
//                         key={movie._id}
//                         movie={movie} 
//                         onClick={() => onMovieClick(movie._id)} 
//                         onMovieUpdate={onMovieUpdate}
//                         onMovieDelete={handleMovieDelete} 
//                         userInfo={userInfo}
//                         loading={loading}
//                     />
//                 ))}
//             </div>
//         </div>
//     );
// }

// export default AdminCategoryRow;