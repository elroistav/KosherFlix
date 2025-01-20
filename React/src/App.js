import React from 'react';
import { BrowserRouter as Router, Route, Routes } from 'react-router-dom';
import HomeScreen from './pages/HomeScreen';  // Import the HomeScreen component
import AnotherPage from './pages/AnotherPage';  // Import another page component
import AdminCategoryPage from './pages/AdminCategoryPage';  // Import the AdminCategoryPage component
import MoviePage from './pages/MoviePage';
import MockMoviePage from "./pages/MockMoviePage"; // Import the mock version
import ErrorPage from './components/ErrorPage';  // Import the ErrorPage component


function App() {
  return (
    <Router>
      <div className="App">
        <Routes>
          <Route path="/" element={<HomeScreen />} />
          <Route path="/another" element={<AnotherPage />} />
          <Route path="/admin" element={<AdminCategoryPage />} />
          <Route path="/movie" element={<MoviePage />} />
          <Route path="/mock-movie" element={<MockMoviePage />} />
          <Route path="*" element={<ErrorPage />} /> {/* Catch-all route for undefined paths */}

          {/* Add more routes as needed */}
        </Routes>
      </div>
    </Router>
  );
}

export default App;