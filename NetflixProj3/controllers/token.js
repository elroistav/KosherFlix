const tokenService = require('../services/token');
const jwt = require('jsonwebtoken');
const key = "Some super secret key shhhhhhhhhhhhhhhhh!!!!!"



// Login
const login = async (req, res) => {
    const user = await tokenService.login(req.body.userName, req.body.password);
    if (!user) {
        return res.status(401).json({ error: 'Invalid details' });
    }
    const data = { username: req.body.username }
    const token = jwt.sign(data, key)
    res.status(201).json({ token });
};

//isLoggedIn 
const isLoggedIn = (req, res) => {
    if (req.headers.authorization) {
    const token = req.headers.authorization.split(" ")[1];
    try {
    const data = jwt.verify(token, key);
    console.log('The logged in user is: ' + data.username);
    return res.status(200).send('User is logged in');
} catch (err) {
    return res.status(401).send("Invalid Token");
    }
    }
    else
    return res.status(403).send('Token required');
    }


module.exports = { login, isLoggedIn };