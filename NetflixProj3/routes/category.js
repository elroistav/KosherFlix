const categoryController = require('../controllers/category');

const express = require('express');
var router = express.Router();


router.route('/')
.get(categoryController.getCategories)
.post(categoryController.createCategory);

router.route('/:id')
.get(categoryController.getCategory)
.patch(categoryController.updateCategory)
.delete(categoryController.deleteCategory);

// Catch-all route for undefined paths within /category
router.use((req, res, next) => {
    res.status(404).json({ error: 'Category Not Found' });
  });

module.exports = router;