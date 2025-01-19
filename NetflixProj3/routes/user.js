const userController = require('../controllers/user');

const express = require('express');
var router = express.Router();


router.route('/')
.post(userController.createUser);

router.route('/:id')
.get(userController.getUser);

// Catch-all route for undefined paths within /category
router.use((req, res, next) => {
    res.status(404).json({ error: 'User Not Found' });
  });

module.exports = router;