const User = require('../models/user');
const Movie = require('../models/movie');


const createUser = async (userData) => {
    try {
        const requiredFields = Object.keys(User.schema.paths).filter(
                    path => User.schema.paths[path].isRequired
                );
        
                const missingFields = requiredFields.filter(field => userData[field] === undefined);
                if (missingFields.length > 0) {
                    throw new Error(`Missing required fields: ${missingFields.join(', ')}`);
                }

        // Generate a unique random integer for the intId field
        let uniqueIntId;
        while (true) {
            // Generate a random integer between 100000 and 999999 (6 digits)
            uniqueIntId = Math.floor(Math.random() * 900000) + 100000;

            // Check if this intId already exists in the database
            const existingUser = await User.findOne({ intId: uniqueIntId }).maxTimeMS(5000);
            if (!existingUser) break; // Exit loop if unique
        }

        // Create the user object with the intId field
        const user = new User(userData);
        user.intId = uniqueIntId;
        user.watchedMovies = [];

        // Save the user to the database
        return await user.save();
    } catch (error) {
        console.error('User validation error:', error.message);
        throw error;
    }
};

// Get a user by ID
const getUserById = async (id) => { 
    try {
        return User.findById(id).select('-password');
    } catch (e) {
        return null;
    }
};

// Validate user ID
const validateUserId = async (headers) => {
    try {
        // Get user ID from request headers
        const userId = headers['user-id'];
        if (!userId) {
            console.error('No user ID found in headers');
            throw new Error('No user ID found in headers');
        }

        // Check if user exists in database
        const user = await getUserById(userId);
        if (!user) {
            console.error('Invalid user ID' + userId);
            
            throw new Error('Invalid user ID');     
        }

        return user;
    } catch (error) {
        console.error(error.message);
        throw error;    
    }
};



module.exports = {createUser, getUserById, validateUserId};