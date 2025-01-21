const tokenService = require('../services/token');

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


module.exports = { login };