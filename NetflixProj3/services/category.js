const Category = require('../models/category');
const Movie = require('../models/movie');
const { validateUserId } = require('./user');

// Create a new category
const createCategory = async (headers, categoryData) => {
    try {
        await validateUserId(headers);
            const requiredFields = Object.keys(Category.schema.paths).filter(
                path => Category.schema.paths[path].isRequired // Check if the field is required
            );
            // Check if all required fields are present
            const missingFields = requiredFields.filter(field => categoryData[field] === undefined);
            if (missingFields.length > 0) {
                throw new Error(`Missing required fields: ${missingFields.join(', ')}`);
            }
        const category = new Category(categoryData);
        if (categoryData.promoted) category.promoted = true;
        return await category.save();
    } catch (error) {
        console.error('Error creating category:', error);
        throw error;
    }
};

// Get a category by ID
const getCategoryById = async (headers, id) => { 
    try {
        await validateUserId(headers);        
        
        return await Category.findById(id);
    } catch (error) {
        console.error('Error getting category by ID:', error);
        throw error;
   }
};

// Get all categories   
const getCategories = async (headers) => { 
    try {
        console.log('I entered getCategories');
        await validateUserId(headers);
        return await Category.find({}); 
    } catch (error) {
        console.error('Error getting categories:', error);
        throw error;
    }
};

// Update a category by ID
const updateCategory = async (headers, id, updates) => {
    try {
        await validateUserId(headers);        
        const category = await Category.findById(id);
        if (!category) throw new Error('Category not found');

        Object.keys(updates).forEach((key) => { // Loop through each key in the updates object
            if (key in category) {
                category[key] = updates[key];
            }
        });

        return await category.save();
    } catch (error) {
        console.error('Error updating category:', error);
        throw error;
    }
};  

// Delete a category by ID
const deleteCategory = async (headers, id) => {
    try {
        await validateUserId(headers);
        
        const category = await Category.findById(id);
        if (!category) throw new Error('Category not found');


        const moviesArray = category.movies;

        await category.deleteOne();

        await removeUpdateMoviesArray(moviesArray, id);

        return category;
    } catch (error) {
        console.error('Error deleting category:', error);
        throw error;
    }
};

// Remove category from movies array
const removeUpdateMoviesArray = async (movies, categoryId) => {
    try {
        console.log('Movies to process:', movies);
        for (const movieID of movies) {
            console.log('Processing movie with ID:', movieID);
            const existingMovie = await Movie.findById(movieID); 
            if (!existingMovie) {
                throw new Error(`Movie with ID ${movieID} does not exist`);
            }
            console.log('Existing movie categories:', existingMovie.categories);
            existingMovie.categories = existingMovie.categories.filter(category => category != categoryId);
            console.log('Updated categories:', existingMovie.categories);
            await existingMovie.save();
        }
    } catch (error) {
        console.log('Error removing category from movie:', error.message);
        throw error;
    }
};

module.exports = {createCategory, getCategoryById, getCategories, updateCategory, deleteCategory,removeUpdateMoviesArray }