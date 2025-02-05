const fs = require('fs');
const path = require('path');
const Movie = require('../models/movie');
const Category = require('../models/category');
const User = require('../models/user');
const { validateUserId } = require('./user');

// Create a new movie
const createMovie = async (headers, movieData) => {
    try {
        console.log("Starting movie creation process...");

        //await validateUserId(headers);
        console.log("User ID validated successfully.");

        // Check if required fields are present
        const requiredFields = Object.keys(Movie.schema.paths).filter(
            path => Movie.schema.paths[path].isRequired // Check if the field is required
        );

        const missingFields = requiredFields.filter(field => movieData[field] === undefined);
        if (missingFields.length > 0) {
            console.log(`Missing required fields: ${missingFields.join(', ')}`);
            throw new Error(`Missing required fields: ${missingFields.join(', ')}`);
        }
        console.log("All required fields are present.");

        // // Add paths for poster and video if they exist
        // const poster = movieData.poster || null;
        // const video = movieData.video || null;

        // if (poster) {
        //     console.log("Poster provided, adding to movie data.");
        //     movieData.poster = poster;
        // }
        // if (video) {
        //     console.log("Video provided, adding to movie data.");
        //     movieData.video = video;
        // }

        // Generate a unique random integer for the intId field
        let uniqueIntId;
        console.log("Generating unique intId...");
        while (true) {
            uniqueIntId = Math.floor(Math.random() * 900000) + 100000;
            console.log(`Generated intId: ${uniqueIntId}`);

            // Check if this intId already exists in the database
            const existingMovie = await Movie.findOne({ intId: uniqueIntId }).maxTimeMS(3000);
            if (!existingMovie) break; // Exit loop if id is unique
            console.log(`IntId ${uniqueIntId} already exists, generating a new one.`);
        }

        // Add the intId field to movieData
        movieData.intId = uniqueIntId;
        console.log(`Assigned unique intId: ${uniqueIntId}`);

        const movie = new Movie(movieData);
        console.log("Movie object created:", movie);

        const savedMovie = await movie.save();
        console.log("Movie saved successfully:", savedMovie);

        // Update categories array
        await addUpdateCategoriesArray(movieData.categories, savedMovie._id); 
        console.log("Categories updated successfully for the movie.");

        return savedMovie;
    } catch (error) {
        console.log('Error creating movie:', error.message);
        throw error;
    }
};



// Update categories array
const addUpdateCategoriesArray = async (categories, movieId) => {
    try {
        for (const categoryID of categories) {
            const existingCategory = await Category.findById(categoryID);
            if (!existingCategory) {
                throw new Error(`Category with ID ${categoryID} does not exist`);
            }
            // Add the movie ID to the category's movies array
            existingCategory.movies.push(movieId);
            await existingCategory.save();
        }
    } catch (error) {
        console.log('Error updating categories array:', error.message);
        throw error;
    }
}

// Update categories array
const removeUpdateCategoriesArray = async (categories, movieId) => {
    try {
        for (const categoryID of categories) {
            const existingCategory = await Category.findById(categoryID);
            if (!existingCategory) {
                throw new Error(`Category with ID ${categoryID} does not exist`);
            }
            // Remove the movie ID from the category's movies array
            existingCategory.movies = existingCategory.movies.filter(movie => movie != movieId);
            await existingCategory.save();
        }
    } catch (error) {
        console.log('Error removing movie from categories:', error.message);
        throw error;
    }
}

const removeFromWatchedMovies = async (movieId) => {
    try {
        const users = await User.find();
        for (const user of users) {
            user.watchedMovies = user.watchedMovies.filter(movie => movie != movieId);
            await user.save();
        }
    }
    catch (error) {
        console.log('Error removing movie from watched movies:', error.message);
        throw error;
    }
}

// Get a movie by ID
const getMovieById = async (headers, id) => {
    try {
        await validateUserId(headers);
        if (!id) {
            throw new Error('Movie ID is required');
        }

        const movie = await Movie.findById(id).populate('categories'); 
        if (!movie) {
            throw new Error('Movie not found');
        }
        return movie;
    } catch (error) {
        console.log('Error getting movie:', error.message);
        throw error;
    }
};


// Get all movies
const getMovies = async (userId, headers) => {
    try {
        await validateUserId(headers);
        if (!userId) {
            throw new Error('User ID is required');
        }

        const user = await User.findById(userId);
        if (!user) {
            throw new Error('User not found');
        }

        const userWatchedMovies = user.watchedMovies;

        const categories = await Category.find();
        // Get all promoted categories
        const promotedCategories = categories.filter(category => category.promoted);

        promotedCategoriesData = promotedCategories.map(category => {
            let moviesCount = 0;
            const max20Movies = [];
            console.log('Category movies: ', category.movies);
            const shuffledMovies = shuffleArray(category.movies); // Shuffle the movies array

            shuffledMovies.forEach(movieId => {
                if (moviesCount >= 20) {
                    return;
                }
                if (!userWatchedMovies.includes(movieId)) {
                    moviesCount++;
                    max20Movies.push(movieId);
                }
            });

            return {
                category: category.name,
                movies: max20Movies
            };
        });
        console.log('Promoted categories:', promotedCategoriesData);

        // Get the last 20 watched movies
        const lastWhatchedMovies = userWatchedMovies.slice(-20);
        console.log('Last watched movies:', lastWhatchedMovies);
        const shuffledWatchedMovies = shuffleArray(lastWhatchedMovies); // Shuffle the movies array
        const lastWatchededData = {
            category: 'Last watched',
            movies: shuffledWatchedMovies
        }

        // Combine the data into a single JSON object
        const jsonData = {
            promotedCategories: promotedCategoriesData,
            lastWatched: lastWatchededData
        };

        return jsonData;

    } catch (error) {
        console.log('Error getting movies:', error.message);
        throw error;
    }
};

// shuffleArray function
const shuffleArray = array => {
    for (let i = array.length - 1; i > 0; i--) {
        const j = Math.floor(Math.random() * (i + 1));
        [array[i], array[j]] = [array[j], array[i]];
    }
    return array;
};

// Update a movie
const updateMovie = async (headers, id, movieData) => {
    try {
        console.log("Starting movie update process...");

        await validateUserId(headers);
        console.log("User ID validated successfully.");

        // Check if movie exists
        const existingMovie = await Movie.findById(id);
        if (!existingMovie) {
            throw new Error('Movie not found');
        }

        // Check if required fields are present
        const requiredFields = Object.keys(Movie.schema.paths).filter(
            path => Movie.schema.paths[path].isRequired // Check if the field is required
        );

        const missingFields = requiredFields.filter(field => 
            field !== 'thumbnail' && 
            field !== 'videoUrl' && 
            movieData[field] === undefined
        );

        if (missingFields.length > 0) {
            console.log(`Missing required fields: ${missingFields.join(', ')}`);
            throw new Error(`Missing required fields: ${missingFields.join(', ')}`);
        }
        console.log("All required fields are present.");

        // Update movie data
        const updatedMovieData = {
            ...movieData,
            intId: existingMovie.intId // Keep the original intId
        };

        // Remove existing category associations
        await removeUpdateCategoriesArray(existingMovie.categories, id);
        
        // Add new category associations
        await addUpdateCategoriesArray(movieData.categories, id);
        console.log("Categories updated successfully.");

        // Remove from watched movies and send server command
        await removeFromWatchedMovies(id);
        const userId = headers['user-id'];
        const command = await deleteToString(userId, id);
        await sendToServer(command);

        // Update the movie document
        const updatedMovie = await Movie.findByIdAndUpdate(
            id, 
            updatedMovieData, 
            { 
                new: true, // Return the updated document
                runValidators: true // Run schema validation
            }
        ).populate('categories');

        if (!updatedMovie) {
            throw new Error('Update failed');
        }

        console.log("Movie updated successfully:", updatedMovie);
        return updatedMovie;

    } catch (error) {
        console.log('Error updating movie:', error.message);
        throw error;
    }
};


// Delete a movie
const deleteMovie = async (headers, id) => {
    try {
        await validateUserId(headers);
        const userId = headers['user-id'];
        if (!id) {
            throw new Error('Movie ID is required');
        }
        const command = await deleteToString(userId, id);

        const movie = await Movie.findById(id);
        if (!movie) {
            throw new Error('Movie not found');
        }
        await Movie.findByIdAndDelete(id);

        const categoriesArray = movie.categories;
        await removeUpdateCategoriesArray(categoriesArray, id);
        await removeFromWatchedMovies(id);

        await sendToServer(command);
        return movie;
    } catch (error) {
        console.log('Error deleting movie:', error.message);
        throw error;
    }
};

// Get the string representation of a GET request
const getToString = async (headers, userHexId, movieHexId) => {
    try {
        await validateUserId(headers);
        const userId = await hexUserToDec(userHexId);
        const movieId = await hexMovieToDec(movieHexId);
        return "GET " + userId + " " + movieId;
    } catch (error) {
        console.log('Error in getToString:', error.message);
        throw error;
    }
};

// Get the string representation of a POST request
const postToString = async (userHexId, movieHexId) => {
    try {
        const userId = await hexUserToDec(userHexId);
        const movieId = await hexMovieToDec(movieHexId);
        let user = await User.findById(userHexId);
        // Check if the user exists
        if (user) {
            // Add the movieHexId to the user's movies if it's not already there
            if (!user.watchedMovies.includes(movieHexId)) {
                user.watchedMovies.push(movieHexId);
            }

            // Save the updated user back to the database
            await user.save();

            // Return the appropriate command
            if (user.watchedMovies.length === 1) {
                return "POST " + userId + " " + movieId;
            } else {
                return "PATCH " + userId + " " + movieId;
            }
            } else {
                throw new Error("User not found");
            }
        } 
        catch (error) {
            console.log('Error in postToString:', error.message);
            throw error;
        }
};

const deleteToString = async (userHexId, movieHexId) => {
    try {
        const userId = await hexUserToDec(userHexId);
        const movieId = await hexMovieToDec(movieHexId);
        return "DELETE " + userId + " " + movieId;
    }
    catch (error) {
        console.log('Error in deleteToString:', error.message);
        throw error;
    }
};

const convertIntIdsToHexIds = async (intIds) => {
    const hexIds = [];
    
    for (const intId of intIds) {
        try {
            const movie = await Movie.findOne({ intId });
            if (movie) {
                hexIds.push(movie._id);
            }
        } catch (error) {
            console.warn(`Error processing movie with intId ${intId}:`, error.message);
            continue;
        }
    }
    
    return hexIds;
};

const hexUserToDec = async (hexId) => {
    try {
        // Find the user by its MongoDB ObjectId (hexId)
        const user = await User.findById(hexId);

        // If the user doesn't exist, throw an error
        if (!user) {
            throw new Error('User not found');
        }

        // Return the intId of the user
        return user.intId;
    } catch (error) {
        throw error; // Handle the error in the calling function
    }
};

// Convert a movie's hex ID to a decimal ID
const hexMovieToDec = async (hexId) => {
    try {
        // Find the movie by its MongoDB ObjectId (hexId)
        const movie = await Movie.findById(hexId);

        // If the movie doesn't exist, throw an error
        if (!movie) {
            throw new Error('Movie not found');
        }

        // Return the intId of the movie
        return movie.intId;
    } catch (error) {
        throw error; // Handle the error in the calling function
    }
};
// Path: services/movie.js
const net = require('net');
const user = require('../models/user');

const sendToServer = async (command) =>{
    try {
        return new Promise((resolve, reject) => {
            const client = new net.Socket();

            client.connect(5555, 'backend-server', () => {
                console.log('Connected to the server. sending:', command);
                client.write(command);
            });

            client.on('data', (data) => {
                console.log('Received:', data.toString());
                resolve(data.toString());
                client.destroy();
            });

            client.on('error', (err) => {
                console.error('Error:', err.message);
                reject(err);
            });

            client.on('close', () => {
                console.log('Connection closed');
            });

        });
    }
    catch (error) {
        console.log('Error in sendToServer:', error.message);
        throw error;
    }
};

// Search for movies
const searchMovies = async (headers, query) => {
    try {
        await validateUserId(headers);

        // Create a case-insensitive regex for the query
        const regex = new RegExp(query, 'i');

        // Get the schema paths dynamically
        const schemaPaths = Movie.schema.paths;

        // Dynamically build search criteria for both string and array of strings fields
        const searchCriteria = Object.keys(schemaPaths)
            .map(field => {
                const fieldType = schemaPaths[field].instance;
                if (fieldType === 'String') {
                    return { [field]: { $regex: regex } }; // Regular string field
                }
                if (fieldType === 'Array' && schemaPaths[field].caster.instance === 'String') {
                    return { [field]: { $elemMatch: { $regex: regex } } }; // Array of strings
                }
                return null;
            })
            .filter(criteria => criteria !== null);

        // Perform the search using $or to match any criteria
        const movies = await Movie.find({ $or: searchCriteria }).populate('categories');
        return movies;
    } catch (error) {
        console.log('Error searching movies:', error.message);
        throw error;
    }
};


module.exports = {
    createMovie,
    getMovieById,
    getMovies,
    updateMovie,
    deleteMovie,
    convertIntIdsToHexIds,
    getToString,
    postToString,
    sendToServer,
    addUpdateCategoriesArray,
    removeUpdateCategoriesArray,
    shuffleArray,
    searchMovies
};
