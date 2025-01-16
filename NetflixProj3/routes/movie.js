const express = require('express');
const router = express.Router();
const movieController = require('../controllers/movie');

// Route to handle all movies
router.route('/')
    .get(movieController.getMovies) // Get all movies, optionally filtered by query parameters
    .post(movieController.createMovie); // Create a new movie

// Route to handle a specific movie by ID
router.route('/:id')
    .get(movieController.getMovieById) // Get a movie by ID
    .put(movieController.updateMovie) // Update a movie by ID
    .delete(movieController.deleteMovie); // Delete a movie by ID

router.route('/:id/recommend')
    .get(movieController.handleGetrecommend) // Get a recommended movie
    .post(movieController.handlePostrecommend); // Post a recommended movie

router.get('/search/:query', movieController.searchMovies);
module.exports = router;
