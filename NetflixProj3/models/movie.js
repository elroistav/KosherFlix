const mongoose = require('mongoose');
const Schema = mongoose.Schema;

const MovieSchema = new Schema({
    title: {
        type: String,
        required: true,
        unique: true
    },
    description: { // Brief overview of the movie
        type: String,
        required: false
    },
    rating: { // IMDb-like rating in the range 0-10
        type: Number,
        required: false,
        min: 0,
        max: 10
    },
    length: { // Length in minutes
        type: Number,
        required: true
    },
    director: { 
        type: String,
        required: true
    },
    intId: {
        type: Number,
        required: false,
        unique: true
    },
    cast: [{ // Array of main cast members
        type: String,
        required: false
    }],
    ageRestriction: { // Age restriction for viewers
        type: Number,
        required: false
    },
    categories: [{ // Array of category IDs
        type: mongoose.Schema.Types.ObjectId,
        ref: 'Category',
        default: [],
        required: false
    }],
    
    releaseDate: { // Release date of the movie
        type: Date,
        required: true
    },
    language: { // Primary language of the movie
        type: String,
        required: true
    },
    subtitles: [{ // Array of available subtitle languages
        type: String,
        required: false
    }],
   
    thumbnail: { // URL to the movie's thumbnail image
        type: String,
        required: true
    },
    videoUrl: { // URL to stream the movie
        type: String,
        required: true
    },
    createdAt: { // Automatically set when the movie is added
        type: Date,
        default: Date.now
    },
    updatedAt: { // Automatically updated when the movie is modified
        type: Date,
        default: Date.now
    }
});

// Pre-save middleware to update the updatedAt field
MovieSchema.pre('save', function (next) {
    this.updatedAt = Date.now();
    next();
});

module.exports = mongoose.model('Movie', MovieSchema);
