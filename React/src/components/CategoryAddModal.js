import React, { useState } from 'react';
import '../styles/MovieEditModal.css';

const CategoryAddModal = ({ isOpen, onClose, onSave, isSaving, error: propError }) => {
    const [localError, setLocalError] = useState(null);
    const [formData, setFormData] = useState({
        name: '',
        description: '',
        promoted: false,
    });

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            setLocalError(null);
            await onSave(formData);
        } catch (error) {
            if (error.response?.data?.code === 11000) {
                setLocalError('A category with this name already exists. Please choose a different name.');
            } else {
                setLocalError('An error occurred while saving. Please try again.');
            }
        }
    };

    if (!isOpen) return null;

    return (
        <div className="modal-overlay">
            <div className="modal-content">
                <h2>Add New Category</h2>
                {(propError || localError) && (
                    <div className="error-alert">
                        {propError || localError}
                    </div>
                )}
                <form onSubmit={handleSubmit}>
                    <div className="form-group">
                        <label>Name:</label>
                        <input
                            type="text"
                            value={formData.name}
                            onChange={(e) => setFormData({ ...formData, name: e.target.value })}
                            disabled={isSaving}
                        />
                    </div>
                    <div className="form-group">
                        <label>Description:</label>
                        <textarea
                            value={formData.description}
                            onChange={(e) => setFormData({ ...formData, description: e.target.value })}
                            disabled={isSaving}
                        />
                    </div>
                    <div className="form-group">
                        <label>Promoted:</label>
                        <input
                            type="checkbox"
                            checked={formData.promoted}
                            onChange={(e) => setFormData({ ...formData, promoted: e.target.checked })}
                            disabled={isSaving}
                        />
                    </div>
                    <div className="modal-buttons">
                        <button type="submit" disabled={isSaving}>
                            {isSaving ? 'Saving...' : 'Add Category'}
                        </button>
                        <button type="button" onClick={onClose} disabled={isSaving}>
                            Cancel
                        </button>
                    </div>
                </form>
            </div>
        </div>
    );
};

export default CategoryAddModal;
