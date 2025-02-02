const movieService = require('../services/movie');
const multer = require('multer');
const fs = require('fs');
const path = require('path');

const uploadDir = path.join(__dirname, 'uploads');

if (!fs.existsSync(uploadDir)) {
    fs.mkdirSync(uploadDir);
}

const storage = multer.diskStorage({
    destination: (req, file, cb) => {
        cb(null, 'uploads/');
    },
    filename: (req, file, cb) => {
        const uniqueSuffix = Date.now() + '-' + Math.round(Math.random() * 1E9);
        const sanitizedOriginalName = file.originalname.replace(/\s/g, '-');
        cb(null, `${file.fieldname}-${uniqueSuffix}-${sanitizedOriginalName}`);
    },
});

// **הוסף את הקונפיגורציה של multer**
const upload = multer({ storage }).fields([
    { name: 'videoUrl', maxCount: 1 }, // עבור קובץ הסרט
    { name: 'thumbnail', maxCount: 1 } // עבור תמונת העטיפה
]);

// Create a new movie
const createMovie = async (req, res) => {
    console.log('Creating movie...');
    upload(req, res, async (err) => {
        if (err) {
            console.error('File upload failed:', err);
            return res.status(400).json({ error: 'File upload failed' });
        }

        const { title, description, categories, length, director, releaseDate, language } = req.body;
        console.log('Received body data:', { title, description, categories, length, director, releaseDate, language });

        console.log('Files in request:', req.files);


        // const videoUrl = req.files?.videoUrl ? `http://localhost:4000/uploads/${req.files.videoUrl[0].filename}` : null;
        // const thumbnail = req.files?.thumbnail ? `http://localhost:4000/uploads/${req.files.thumbnail[0].filename}` : null;
        // const videoUrl = req.body.videoUrl;
        // const thumbnail = req.body.thumbnail;
        const videoUrl = req.files?.videoUrl ? `uploads/${req.files.videoUrl[0].filename}` : null;
        const thumbnail = req.files?.thumbnail ? `uploads/${req.files.thumbnail[0].filename}` : null;


        console.log('File paths:', { videoUrl, thumbnail });

        try {
            console.log('Calling movieService.createMovie...');
            const movie = await movieService.createMovie(req.headers, {
                title,
                description,
                categories,
                length, // אורך הסרט
                director, // במאי
                releaseDate, // תאריך יציאה
                language, // שפה
                videoUrl, // נתיב קובץ הסרט
                thumbnail // נתיב תמונת העטיפה
            });


            console.log('Movie created successfully:', movie);

            return res.status(201)
                .location(`/movies/${movie._id}`)
                .json(movie);
        } catch (error) {
            console.error('Error occurred during movie creation:', error);
            if (error.message === 'No user ID found in headers' || error.message === 'Invalid user ID') {
                return res.status(401).json({ error: error.message });
            }
            if (error.message.includes("Missing required fields")) {
                return res.status(400).json({ error: error.message });
            }
            if (error.message.includes("Category with ID")) {
                return res.status(404).json({ error: error.message });
            } else {
                console.error('Unexpected error:', error);
                return res.status(500).json({ error: 'An unexpected error occurred.' });
            }
        }
    });
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
    console.log('Updating movie...');
    upload(req, res, async (err) => {
        if (err) {
            console.error('File upload failed:', err);
            return res.status(400).json({ error: 'File upload failed' });
        }

        const { title, description, categories, length, director, releaseDate, language } = req.body;
        console.log('Received body data:', { title, description, categories, length, director, releaseDate, language });

        console.log('Files in request:', req.files);

        // Generate file paths similar to create movie
        const videoUrl = req.files?.videoUrl ? `uploads/${req.files.videoUrl[0].filename}` : null;
        const thumbnail = req.files?.thumbnail ? `uploads/${req.files.thumbnail[0].filename}` : null;

        console.log('File paths:', { videoUrl, thumbnail });

        try {
            console.log('Calling movieService.updateMovie...');
            const movie = await movieService.updateMovie(req.headers, req.params.id, {
                title,
                description,
                categories,
                length,
                director,
                releaseDate,
                language,
                videoUrl, // movie file path
                thumbnail // cover image path
            });

            console.log('Movie updated successfully:', movie);

            return res.status(204).send(); // No content for successful update
        } catch (error) {
            console.error('Error occurred during movie update:', error);
            if (error.message === 'No user ID found in headers' || error.message === 'Invalid user ID') {
                return res.status(401).json({ error: error.message });
            }
            if (error.message.includes("Missing required fields")) {
                return res.status(400).json({ error: error.message });
            }
            if (error.message.includes("Category with ID")) {
                return res.status(404).json({ error: error.message });
            }
            if (error.message.includes("Movie not found")) {
                return res.status(404).json({ error: error.message });
            } else {
                console.error('Unexpected error:', error);
                return res.status(500).json({ error: 'An unexpected error occurred.' });
            }
        }
    });
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
        console.log('Handling POST recommend...');
        const userHexId = req.headers['user-id'];
        const movieHexId = req.params.id;
        console.log('User hex ID:', userHexId);
        console.log('Movie hex ID:', movieHexId);
        const command = await movieService.postToString(userHexId, movieHexId);
        console.log('Command:', command);
        await movieService.sendToServer(command);
        console.log('Recommendation sent successfully');
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
