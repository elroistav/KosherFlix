const e = require('cors');
const mongoose = require('mongoose');
const Schema = mongoose.Schema;

const User = new Schema({
    isAdmin: {
        type: Boolean,
        default: false
    },
    intId: {
        type: Number,
        required: false,
        unique: true
    },
    userName: {
        type: String,
        required: true,
        unique: true // Ensure user names are unique
    },
    password: {
        type: String,
        required: true
    },
    name: {
        type: String,
        required: true
    },
    email: {
        type: String,
        required: true
    },
    createDate: {
        type: Date,
        default: Date.now
    },
    watchedMovies: {
        type: [mongoose.Schema.Types.ObjectId],
        default: []
    },
    image: {
        type: String,
        default: ''
    },
    

});
module.exports = mongoose.model('User', User);