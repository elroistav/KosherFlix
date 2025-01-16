const mongoose = require('mongoose');
const Schema = mongoose.Schema;

const Category = new Schema({
    name: {
        type: String,
        required: true,
        unique: true // Ensure category names are unique
    },
    description: {
        type: String,
        default: '' // Optional description of the category
    },
    promoted : {
        type: Boolean,
        default: false
    },
    movies: [{
        type: mongoose.Schema.Types.ObjectId,
        ref: 'Movie',
        default: []
    }]
});
module.exports = mongoose.model('Category', Category);