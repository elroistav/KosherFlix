const tokenService = require('../services/token');
const jwt = require('jsonwebtoken');
const key = "Some super secret key shhhhhhhhhhhhhhhhh!!!!!"



// Login
const login = async (req, res) => {
    console.log('start login try. The request body is: ' + JSON.stringify(req.body));
    const user = await tokenService.login(req.body.userName, req.body.password);
    if (!user) {
        return res.status(401).json({ error: 'Invalid details' });
    }
    const data = { username: req.body.username,
                   name: user.name,
                   image: user.image,
                   userId: user._id,
                    isAdmin: user.isAdmin
     }
     console.log('LOGIN The data is: ' + JSON.stringify(data));
     const token = jwt.sign(data, key)
    res.status(201).json({ token });
};

//isLoggedIn 
const isLoggedIn = async (req, res) => {
    if (req.headers.authorization) {
        const token = req.headers.authorization.split(" ")[1];
        try {
            const data = jwt.verify(token, key);
            console.log('The logged in user is: ' + data.name);
            const user = {
                name: data.name,
                avatar: data.image ? `${data.image}` : null,
                userId: data.userId,
                isAdmin: data.isAdmin
            };
            console.log('The user is: ' + JSON.stringify(user));
            return res.status(200).json(user);
        } catch (err) {
            return res.status(401).send("Invalid Token");
        }
    }
};


module.exports = { login, isLoggedIn };