const express = require('express');
const bodyParser = require('body-parser');
const cors = require('cors');
const mongoose = require('mongoose');
const categories = require('./routes/category');
const users = require('./routes/user');
const tokens = require('./routes/token');
const movies = require('./routes/movie');
var app = express();
const path = require('path');



require('custom-env').env(process.env.NODE_ENV, './config');
mongoose.connect(process.env.CONNECTION_STRING);

console.log(process.env.PORT);
console.log(process.env.CONNECTION_STRING);

app.use(cors());
app.use(bodyParser.urlencoded({extended : true}));
app.use(express.static('public'));
app.use(express.json());

app.use('/uploads', express.static(path.join(__dirname, 'uploads')));


app.use('/api/categories', categories);
app.use('/api/users', users);
app.use('/api/tokens', tokens);
app.use('/api/movies', movies);


// Catch-all route for undefined paths .
app.use((req, res, next) => {
    res.status(404).json({ error: 'Not Found' });
  });

app.listen(process.env.PORT);