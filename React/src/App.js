import React from 'react';
import { BrowserRouter as Router, Route, Routes } from 'react-router-dom';
import HomeScreen from './pages/HomeScreen';  // Import the HomeScreen component
import AnotherPage from './pages/AnotherPage';  // Import another page component
import AdminCategoryPage from './pages/AdminCategoryPage';  // Import the AdminCategoryPage component
import MoviePage from './pages/MoviePage';
import MockMoviePage from "./pages/MockMoviePage"; // Import the mock version


function App() {
  return (
    <Router>
      <div className="App">
        <Routes>
          <Route path="/" element={<HomeScreen />} />
          <Route path="/another" element={<AnotherPage />} />
          <Route path="/admin" element={<AdminCategoryPage />} />
          <Route path="/movies/:movieId" element={<MoviePage />} />
          <Route path="/mock-movie" element={<MockMoviePage />} />
          {/* Add more routes as needed */}
        </Routes>
      </div>
    </Router>
  );
}

export default App;