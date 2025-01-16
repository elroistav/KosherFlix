const tokenService = require('../services/token');

// Login
const login = async (req, res) => {
    const user = await tokenService.login(req.body.userName, req.body.password);
    if (!user) {
        return res.status(401).json({ error: 'Invalid details' });
    }
    res.status(200).json(user._id);
};


module.exports = { login };