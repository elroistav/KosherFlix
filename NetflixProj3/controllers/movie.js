const movieService = require('../services/movie');

// Create a new movie
const createMovie = async (req, res) => {
    try {
        const movie = await movieService.createMovie(req.headers, req.body);
        return res.status(201)
                .location(`/movies/${movie._id}`) 
                  .json(movie); 
    } catch (error) {
        if (error.message === 'No user ID found in headers' || error.message === 'Invalid user ID') {
            return res.status(401).json({ error: error.message });
        }
        if(error.message.includes("Missing required fields")) {
            return res.status(400).json({ error: error.message });
        }
        if(error.message.includes("Category with ID")) {
            return res.status(404).json({ error: error.message });
        }
        else {
            // Default error handler for unexpected errors
            console.error('Unexpected error:', error);
            res.status(500).json({ error: 'An unexpected error occurred.' });
        }
    }
};

// Get a movie by ID
const getMovieById = async (req, res) => {
    try {
        const movie = await movieService.getMovieById(req.headers, req.params.id);
        res.status(200).json(movie);
    } 
    catch(error) {
        if (error.message === 'No user ID found in headers' || error.message === 'Invalid user ID') {
        return res.status(401).json({ error: error.message });
        }
        if(error.message.includes("Movie ID is required")) {
            return res.status(400).json({ error: error.message });
        }
        if(error.message.includes("Movie not found")) {
            return res.status(404).json({ error: error.message });
        }
        else {
            // Default error handler for unexpected errors
            console.error('Unexpected error:', error);
            res.status(500).json({ error: 'An unexpected error occurred.' });
        }
    }
};

// Get all movies
const getMovies = async (req, res) => {
    try {
        const movies = await movieService.getMovies(req.headers['user-id'], req.headers);
        console.log('Movies in controller:', movies);
        res.status(200).json(movies);
    } catch (error) {
        if (error.message === 'No user ID found in headers' || error.message === 'Invalid user ID') {
            return res.status(401).json({ error: error.message });
        }
        if(error.message.includes("User ID is required")) {
            return res.status(400).json({ error: error.message });
        }
        if(error.message.includes("User not found")) {
            return res.status(404).json({ error: error.message });
        }
        else {
            // Default error handler for unexpected errors
            console.error('Unexpected error:', error);
            res.status(500).json({ error: 'An unexpected error occurred.' });
        }
    }
};
// Update a movie by ID
const updateMovie = async (req, res) => {
    try {
        const movie = await movieService.updateMovie(req.headers, req.params.id, req.body);
        res.status(204).send();
    } catch (error) {
        if (error.message === 'No user ID found in headers' || error.message === 'Invalid user ID') {
            return res.status(401).json({ error: error.message });
        }
        if(error.message.includes("Missing required fields") || error.message.includes("Movie ID and complete movie data are required") || error.message.includes("Update failed")) {
            return res.status(400).json({ error: error.message });
        }
        if(error.message.includes("Movie not found") || error.message.includes("Category with ID")) {
            return res.status(404).json({ error: error.message });
        }
        else {
            // Default error handler for unexpected errors
            console.error('Unexpected error:', error);
            res.status(500).json({ error: 'An unexpected error occurred.' });
        }
    }
};

// Delete a movie by ID 
const deleteMovie = async (req, res) => {
    try {
        await movieService.deleteMovie(req.headers, req.params.id);
        res.status(204).send();
    } catch (error) {
        if (error.message === 'No user ID found in headers' || error.message === 'Invalid user ID') {
            return res.status(401).json({ error: error.message });
        }
        if(error.message.includes("Movie ID is required")) {
            return res.status(400).json({ error: error.message });
        }
        if(error.message.includes("Movie not found")) {
            return res.status(404).json({ error: error.message });
        }
        else {
            // Default error handler for unexpected errors
            console.error('Unexpected error:', error);
            res.status(500).json({ error: 'An unexpected error occurred.' });
        }
    }
};

// Get a movie by ID
const handleGetrecommend = async (req, res) => {
    try {
        const userHexId = req.headers['user-id'];
        console.log('User hex ID:', userHexId);
        const movieHexId = req.params.id;
        console.log('Movie hex ID:', movieHexId);
        const command = await movieService.getToString(req.headers, userHexId, movieHexId);
        console.log('Command:', command);
        const responseString = await movieService.sendToServer(command);
        console.log('Response string:', responseString);
        const [statusLine, ...recommendedIntIds] = responseString.split('\n');
        const intIdsArray = recommendedIntIds[1].split(' ').map(Number);
        console.log('Recommended int IDs:', intIdsArray);
        const status = parseInt(statusLine.split(' ')[0], 10);

        if (status === 200) {
            const recommendedHexIds = await movieService.convertIntIdsToHexIds(intIdsArray);
            res.status(200).json(recommendedHexIds);
        } else {
            res.status(status).send();
        }
    } catch (error) {
        if (error.message === 'No user ID found in headers' || error.message === 'Invalid user ID') {
            return res.status(401).json({ error: error.message });
        } 
        if (error.message.includes("Movie with intId")) {
            return res.status(404).json({ error: error.message });
        } 
        if (error.message.includes("Error converting intIds to hexIds")) {
            return res.status(500).json({ error: error.message });
        } 
        else {
            // Default error handler for unexpected errors
            console.error('Unexpected error:', error);
            res.status(500).json({ error: 'An unexpected error occurred.' });
        }
    }
}

// Get a movie by ID
const handlePostrecommend = async (req, res) => {
    try {
        const userHexId = req.headers['user-id'];
        const movieHexId = req.params.id;

        const command = await movieService.postToString(userHexId, movieHexId);

        await movieService.sendToServer(command);

        res.status(204).send();
        
    } catch (error) {
        if (error.message === 'No user ID found in headers' || error.message === 'Invalid user ID') {
            return res.status(401).json({ error: error.message });
        }
        if(error.message.includes("User not found")) {
            return res.status(400).json({ error: error.message });
        }
        if(error.message.includes("Movie not found")) {
            return res.status(404).json({ error: error.message });
        }
        else {
            // Default error handler for unexpected errors
            console.error('Unexpected error:', error);
            res.status(500).json({ error: 'An unexpected error occurred.' });
        }
    }
}

// Search for movies by a query
const searchMovies = async (req, res) => {
    try {
        const query = req.params.query; // Get the search query from the route parameter
        const movies = await movieService.searchMovies(req.headers, query);
        res.status(200).json(movies); // Return the matching movies
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

module.exports = {
    createMovie,
    getMovieById,
    getMovies,
    updateMovie,
    deleteMovie,
    handleGetrecommend,
    handlePostrecommend,
    searchMovies
};
