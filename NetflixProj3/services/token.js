const User = require('../models/user');

// Login
const login = async (userName, password) => {
    try {
        const user = await User.findOne({ userName }).lean();
        if (!user) {
            return null;
        }
        if (user.password !== password) {
            return null;
        }

        delete user.password; // Remove password from user object

        return user;
    } catch (e) {
        return null;
    }
}


module.exports = {login};