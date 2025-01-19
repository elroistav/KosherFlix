const userService = require('../services/user');

// Create a new user
const createUser = async (req, res) => {
    try {
        const user = await userService.createUser(
            //req.body.userName,
            req.body
        );
        return res.status(201)
                  .location(`/users/${user._id}`) // Change `/users/${user._id}` if needed
                  .json(user); // Send back the created user as JSON
    } catch (err) {
        if (err.code === 11000) {
            // userName must be unique
            return res.status(400).json({ error: 'Username must be unique' });
        } else if (err.name === 'ValidationError') {
            // Handle Mongoose validation errors
            const errors = Object.values(err.errors).map(e => e.message);
            res.status(400).json({ errors }); // Respond with 400 Bad Request
        } else {
            // Handle other errors
            console.error(err);
            res.status(500).json({ errors: ['Internal Server Error'] });
        }
    }
};


const getUser = async (req, res) => {
    const user = await userService.getUserById(req.params.id);
    if (!user) {
        return res.status(404).json({ errors: ['User not found'] });
    }
    res.json(user);
};

const login = async (req, res) => {
    const user = await userService.login(req.body.userName, req.body.password);
    if (!user) {
        return res.status(401).json({ error: 'Invalid username or password' });
    }
    res.json(user);
};


module.exports = { createUser, getUser, login };