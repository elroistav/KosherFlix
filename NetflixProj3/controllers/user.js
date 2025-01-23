const userService = require('../services/user');
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

const upload = multer({ storage }).single('profilePicture'); 


// Create a new user
const createUser = async (req, res) => {
    try {
        upload(req, res, async (err) => {
            if (err) {
                return res.status(400).json({ error: 'File upload failed' });
            }

            const { userName, name, email, password } = req.body;
            const profilePicture = req.file ? req.file.path : null;

            try {
                // Try to create the user
                const user = await userService.createUser({
                    userName,
                    name,
                    email,
                    password,
                    image: profilePicture,
                });

                // Respond with created user
                return res.status(201)
                          .location(`/users/${user._id}`) 
                          .json(user); 
            } catch (err) {
                if (err.code === 11000) {
                    // Duplicate username error
                    return res.status(400).json({ error: 'Username must be unique' });
                } else if (err.name === 'ValidationError') {
                    // Handle Mongoose validation errors
                    const errors = Object.values(err.errors).map(e => e.message);
                    return res.status(400).json({ errors });
                } else {
                    // Handle other errors
                    console.error(err);
                    return res.status(500).json({ errors: ['Internal Server Error'] });
                }
            }
        });
    } catch (err) {
        console.error(err);
        return res.status(500).json({ errors: ['Internal Server Error'] });
    }
};


const getUser = async (req, res) => {
    const user = await userService.getUserById(req.params.id);
    if (!user) {
        return res.status(404).json({ errors: ['User not found'] });
    }

    if (user.image) {
        user.image = `http://localhost:4000/${user.image}`;
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