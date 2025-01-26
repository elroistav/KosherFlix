import React, { useState, useEffect } from 'react';
import { BrowserRouter as Router, Route, Routes, useLocation } from 'react-router-dom';
import HomeScreen from './pages/HomeScreen';  
import AnotherPage from './pages/AnotherPage';  
import AdminCategoryPage from './pages/AdminCategoryPage';  
import MoviePage from './pages/MoviePage';
import MockMoviePage from "./pages/MockMoviePage"; 
import ErrorPage from './components/ErrorPage';  
import Welcome from './pages/Welcome';  
import Login from './pages/Login';  
import Register from './pages/Register';  
import Admin from './pages/Admin';  
import Categories from './pages/Categories';  
import Navbar from './components/NavBar'; 
import './styles/global.css';

function AppRoutes({ isDarkMode, setIsDarkMode }) {
  return (
    <Routes>
      <Route path="/homescreen" element={<HomeScreen isDarkMode={isDarkMode} setIsDarkMode={setIsDarkMode} />} />
      <Route path="/another" element={<AnotherPage />} />
      <Route 
        path="/admin" 
        element={
          <Admin 
            isAdmin={true} 
            isDarkMode={isDarkMode}
            setIsDarkMode={setIsDarkMode} 
          />
        }
      />
      <Route path="/admin/category" element={<AdminCategoryPage />} />
      <Route path="/movie" element={<MoviePage />} />
      <Route path="/mock-movie" element={<MockMoviePage />} />
      <Route path="/" element={<Welcome />} />
      <Route path="/register" element={<Register />} />
      <Route path="/login" element={<Login />} />
      <Route 
        path="/categories" 
        element={
          <Categories 
            isDarkMode={isDarkMode}
            setIsDarkMode={setIsDarkMode} 
          />
        } 
      />      
      <Route path="*" element={<ErrorPage />} />
    </Routes>
  );
}

function Layout({ isDarkMode, setIsDarkMode }) {
  const location = useLocation();

  // רשימת עמודים שבהם לא נרצה להציג את ה-NAVBAR
  const hiddenNavbarPaths = ['/', '/login', '/register'];
  
  // בדיקה האם ה-NAVBAR צריך להיות מוסתר
  const isNavbarHidden = hiddenNavbarPaths.includes(location.pathname);

  return (
    <>
      {!isNavbarHidden && <Navbar isDarkMode={isDarkMode} setIsDarkMode={setIsDarkMode} />}
      <AppRoutes isDarkMode={isDarkMode} setIsDarkMode={setIsDarkMode} />
    </>
  );
}

function App() {
  const [isDarkMode, setIsDarkMode] = useState(false);

  useEffect(() => {
    document.body.className = isDarkMode ? 'dark-mode' : 'light-mode';
  }, [isDarkMode]);

  return (
    <Router>
      <div className="App">
        <Layout isDarkMode={isDarkMode} setIsDarkMode={setIsDarkMode} />
      </div>
    </Router>
  );
}

export default App;
