const categoryService = require('../services/category');

// creating a new category
const createCategory = async (req, res) => {
    try {
        const category = await categoryService.createCategory(
            req.headers,
            req.body
        );

        return res.status(201)
                  .location(`/categories/${category._id}`) 
                  .json(category); // Send back the created category as JSON
    } catch (error) {
        if (error.message === 'No user ID found in headers' || error.message === 'Invalid user ID') {
            return res.status(401).json({ error: error.message });
        }
        else {
            // Handle other errors
            console.error(error);
            res.status(500).json({ errors: ['Internal Server Error'] });
        } 
    }
};

// Get all categories
const getCategories = async (req, res) => {
    try {
        const categories = await categoryService.getCategories(req.headers);
        return res.status(200).json({categories});
    } catch (error) {
        if (error.message === 'No user ID found in headers' || error.message === 'Invalid user ID') {
            return res.status(401).json({ error: error.message });
        }
        else {
            // Default error handler for unexpected errors
            console.error('Unexpected error:', error);
            res.status(500).json({ error: 'An unexpected error occurred.' });
        }
    }
};

// Get a single category by ID
const getCategory = async (req, res) => {
    try {
        const category = await categoryService.getCategoryById(req.headers, req.params.id);
        return res.status(200).json(category);
    } catch (error) {
        if (error.message === 'No user ID found in headers' || error.message === 'Invalid user ID') {
            return res.status(401).json({ error: error.message });
        }
        else {
            // Default error handler for unexpected errors
            console.error('Unexpected error:', error);
            res.status(500).json({ error: 'An unexpected error occurred.' });
        }
    }
};

// Update a category by ID
const updateCategory = async (req, res) => {
    try {
        const category = await categoryService.updateCategory(req.headers, req.params.id, req.body);
        return res.status(204).json(category);
    }
    catch (error) {
        if (error.message === 'No user ID found in headers' || error.message === 'Invalid user ID') {
            return res.status(401).json({ error: error.message });
        }
        if (error.message.includes("Category not found")) {
            return res.status(400).json({ error: error.message });
        }
        
        else {
            // Default error handler for unexpected errors
            console.error('Unexpected error:', error);
            res.status(500).json({ error: 'An unexpected error occurred.' });
        }
    }
};

// Delete a category by ID
const deleteCategory = async (req, res) => {
    try {
        const deleted = await categoryService.deleteCategory(req.headers, req.params.id);
        return res.status(204).json();
    }
    catch (error) {
        if (error.message === 'No user ID found in headers' || error.message === 'Invalid user ID') {
            return res.status(401).json({ error: error.message });
        }
        if (error.message.includes("Category not found")) {
            return res.status(400).json({ error: error.message });
        }
        
        else {
            // Default error handler for unexpected errors
            console.error('Unexpected error:', error);
            res.status(500).json({ error: 'An unexpected error occurred.' });
        }
    }
};

module.exports = { createCategory, getCategories, getCategory, updateCategory, deleteCategory };