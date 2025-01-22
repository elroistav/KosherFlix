const tokenService = require('../services/token');
const jwt = require('jsonwebtoken');
const key = "Some super secret key shhhhhhhhhhhhhhhhh!!!!!"



// Login
const login = async (req, res) => {
    const user = await tokenService.login(req.body.userName, req.body.password);
    if (!user) {
        return res.status(401).json({ error: 'Invalid details' });
    }
    const data = { username: req.body.username,
                   name: user.name,
                   image: user.image,
     }
    const token = jwt.sign(data, key)
    res.status(201).json({ token });
};

//isLoggedIn 
const isLoggedIn = async (req, res) => {
    if (req.headers.authorization) {
        const token = req.headers.authorization.split(" ")[1];
        try {
            const data = jwt.verify(token, key);
            console.log('The logged in user is: ' + data.username);
            const user = {
                username: data.username,
                avatar: data.image ? `${user.image}` : null,
            };
            return res.status(200).json(user);
        } catch (err) {
            return res.status(401).send("Invalid Token");
        }
    }
};


module.exports = { login, isLoggedIn };