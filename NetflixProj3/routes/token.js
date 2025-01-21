const tokenController = require('../controllers/token');

const express = require('express');
var router = express.Router();


router.route('/')
.post(tokenController.login)
.get(tokenController.isLoggedIn);

// Catch-all route for undefined paths within /category
router.use((req, res, next) => {
    res.status(404).json({ error: 'Login eror' });
  });

module.exports = router;