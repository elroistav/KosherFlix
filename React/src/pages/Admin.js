import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { useNavigate } from 'react-router-dom'; 
import { Link } from 'react-router-dom';
import RandomMovie from '../components/RandomMovie';
import CategoryRow from '../components/CategoryRow';
import Navbar from '../components/NavBar';  // Importing the Navbar component
import MovieCard from '../components/MovieCard';
import MoviePopup from '../components/MoviePopup';  // assuming you already have the modal component
import SearchResults from '../components/SearchResults';  // assuming you already have the search results component
import '../styles/HomeScreen.css';
import { useLocation } from 'react-router-dom';
import AdminCategoryRow from '../components/AdminCategoryRow';  // Importing the AdminCategoryRow component
import AdminBar from '../components/AdminBar';
import MovieAddModal from '../components/MovieAddModal';  
import CategoryAddModal from '../components/CategoryAddModal';

//import user from '../../../NetflixProj3/models/user';
//import user from '../../../NetflixProj3/models/user';


function HomeScreen({ isDarkMode, setIsDarkMode }) {
  const BASE_URL = process.env.REACT_APP_BASE_URL;
  const [movies, setMovies] = useState([]);
  const [randomMovie, setRandomMovie] = useState(null);
  const [searchResults, setSearchResults] = useState([]);
  const [selectedMovie, setSelectedMovie] = useState(null);
  const navigate = useNavigate();
  const location = useLocation();
  const token = location.state?.token;
  const [loading, setLoading] = useState(true);
  const [userInfo, setUserInfo] = useState(null);

  const [isMovieAddModalOpen, setIsMovieAddModalOpen] = useState(false);
  const [isCategoryAddModalOpen, setIsCategoryAddModalOpen] = useState(false);
  const [selectedMovieForModal, setSelectedMovieForModal] = useState(null);
  const [selectedCategoryForModal, setSelectedCategoryForModal] = useState(null);
  const [isSaving, setIsSaving] = useState(false);

  useEffect(() => {
    async function checkToken() {
      try {
        if (!token) throw new Error('Token not found');
        const response = await axios.get(BASE_URL + '/api/tokens', {
          headers: { Authorization: `Bearer ${token}` },
        });

        if (response.status === 200) {
          console.log('User is logged in');
          const avatarUrl = `${BASE_URL}/${response.data.avatar}`;
          console.log('the returned data is ' + JSON.stringify(response.data));

          setUserInfo({
            name: response.data.name,
            avatar: avatarUrl, 
            userId: response.data.userId,
            token: token,
            isAdmin: response.data.isAdmin
          });

          if (response.data.isAdmin === false) {
            navigate('/'); 
          }
          console.log('the userInfo ' + JSON.stringify(userInfo));

        }
      } catch (error) {
        console.error('Token validation failed:', error);
        navigate('/'); 
      } finally {
        setLoading(false); 
      }
    }

    checkToken();
  }, [navigate]);

  console.log('userInfo:', userInfo);


  // Fetch movies data from API and details for each movie
  useEffect(() => {
    if (loading || !userInfo) {
      console.log('Waiting for userInfo to load...');
      return; 
    }

    async function fetchMovies() {
      try {
        //get user-id from the tokes first
        console.log('The user id is: ' + userInfo.userId);
        const response = await axios.get(BASE_URL + '/api/categories', {
          headers: { 'user-id': userInfo.userId }
        });

        // Fetch the details of each movie in each category
        const fetchedMoviesByCategory = [];
        console.log('The response is: ' + JSON.stringify(response.data));
        for (const category of response.data.categories) {
          const fetchedMovies = [];
          for (const movieId of category.movies) {
            try {
              const movieResponse = await axios.get(`${BASE_URL}/api/movies/${movieId}`, {
                  headers: { 'user-id': userInfo.userId }
              });
              fetchedMovies.push(movieResponse.data);
            } catch (error) {
              console.error(`Error fetching movie details for movieId ${movieId}:`, error);
            }
          }
          fetchedMoviesByCategory.push({ category: category.name, movies: fetchedMovies, _id: category._id });
        }

        setMovies(fetchedMoviesByCategory); // Set all the fetched movies by categories

        // Get a random movie from the response (for the random movie section)
        if (fetchedMoviesByCategory.length > 0) {
          const randomCategory = fetchedMoviesByCategory[Math.floor(Math.random() * fetchedMoviesByCategory.length)];
          const random = randomCategory.movies[Math.floor(Math.random() * randomCategory.movies.length)];
          setRandomMovie(random);
        }
      } catch (error) {
        console.error("Error fetching categories or movie details:", error);
      }
    }

    fetchMovies();
  }, [loading, userInfo]);

  const handleCategoryDelete = async (categoryId) => {

    if (loading || !userInfo) { 
        console.error('Cannot delete category: userInfo is not ready yet.');
        return;
      }

    try {
        // Update UI state first
        const updatedMovies = movies.filter(category => 
            category._id !== categoryId
        );
        setMovies(updatedMovies);

        // Delete category in backend
        await axios.delete(
            `${BASE_URL}/api/categories/${categoryId}`,
            {headers: { 'user-id': userInfo.userId }
          }
        );
    } catch (error) {
        console.error('Error deleting category:', error);
        // Optionally restore the previous state if delete fails
    }
};

    const handleCategoryUpdate = async (updatedCategory) => {

        if (loading || !userInfo) {
            console.error('Cannot save category: userInfo is not ready yet.');
            return;
        }

        try {
            const response = await axios.patch(
                `${BASE_URL}/api/categories/${updatedCategory._id}`,
                updatedCategory,
                { headers: { 'user-id': userInfo.userId }}
            );

            const updatedMovies = movies.map(category =>
                category._id === updatedCategory._id ? response.data : category
            );
            setMovies(updatedMovies);
        } catch (error) {
            console.error('Error saving category:', error);
        }
    };


  const handleMovieDelete = async (movieId) => {

    if (loading || !userInfo) {
        console.error('Cannot delete movie: userInfo is not ready yet.');
        return;
      }

    try {
        // Update UI state first
        const updatedMovies = movies.map(category => ({
            ...category,
            movies: category.movies.filter(movie => movie._id !== movieId)
        }));
        
        setMovies(updatedMovies);
        
        // Update categories in backend
        console.log('The user id is: ' + userInfo.userId);
        await axios.delete(`${BASE_URL}/api/movies/${movieId}`, {
            headers: { 'user-id': userInfo.userId }
        });
    } catch (error) {
        console.error('Error deleting movie:', error);
        // Optionally revert the UI state if delete fails
    }
  };

  // Handle search results
  const handleSearchResults = (results) => {
    setSearchResults(results);
  };



  const handleMovieUpdate = (updatedMovie) => {

    if (loading || !userInfo) { 
        console.error('Cannot delete movie: userInfo is not ready yet.');
        return;
      }

    const updatedMovies = movies.map(category => ({
        ...category,
        movies: category.movies.map(movie => 
            movie._id === updatedMovie._id ? updatedMovie : movie
        )
    }));
    setMovies(updatedMovies);
};



  // Clear search results
  const clearSearchResults = () => {
    setSearchResults([]);
  };

  // Handle click on a movie card to open the modal
  const handleMovieClick = (movieId) => {
    const movie = searchResults.length > 0
      ? searchResults.find(m => m._id === movieId)
      : movies.flatMap(category => category.movies).find(m => m._id === movieId);
    setSelectedMovie(movie);
  };

  const handleAddMovie = async (movieData) => {
    setIsSaving(true);
    try {
        console.log('The user id is: ' + userInfo.userId);
        console.log('The movie data is: ' + JSON.stringify(movieData));
        const response = await axios.post(BASE_URL + '/api/movies', movieData, {
            headers: { 'user-id': userInfo.userId },
        });
        setMovies(prevMovies => prevMovies.map(category =>
            category._id === response.data.categoryId
            ? { ...category, movies: [...category.movies, response.data] }
            : category
        ));
    } catch (error) {
        console.error('Error adding movie:', error);
    } finally {
        setIsSaving(false);
        setIsMovieAddModalOpen(false);
    }
};

  const handleAddCategory = async (categoryData) => {
    setIsSaving(true); // הצג את מצב השמירה
    try {
      const response = await axios.post(BASE_URL + '/api/categories', categoryData, {
        headers: { 'user-id': userInfo.userId },
      });
      // עדכון ה-state אחרי שמתקבל התשובה מהשרת
      const newCategory = response.data;
  
      // עדכון movies עם הקטגוריה החדשה
      setMovies(prevMovies => [...prevMovies, { category: newCategory.name, movies: [], _id: newCategory._id }]);
  
      // סגור את המודאל
      setIsCategoryAddModalOpen(false);
    } catch (error) {
      console.error('Error adding category:', error);
    } finally {
      setIsSaving(false);
    }
  };

  const handleAddMovieClick = () => {
    setSelectedMovieForModal(null); // עבור הוספה נוודא שהמודאל לא יתמלא בסרט קיים
    setIsMovieAddModalOpen(true);
  };

  const handleAddCategoryClick = () => {
    setSelectedCategoryForModal(null); // עבור הוספה נוודא שהמודאל לא יתמלא בקטגוריה קיימת
    setIsCategoryAddModalOpen(true);
  };

  

  return (
    <div className="homeScreenBody">
        <Navbar 
        onSearchResults={handleSearchResults} 
        clearSearchResults={clearSearchResults}
        userInfo={userInfo} 
        loading={loading}
        isDarkMode={isDarkMode}
        setIsDarkMode={setIsDarkMode}
      /> 

        <AdminBar 
        onAddMovie={handleAddMovieClick} 
        onAddCategory={handleAddCategoryClick}
      />

        <MovieAddModal 
        isOpen={isMovieAddModalOpen} 
        onClose={() => setIsMovieAddModalOpen(false)} 
        onSave={handleAddMovie} 
        isSaving={isSaving} 
        movie={selectedMovieForModal} 
        userInfo={userInfo}
      />

        <CategoryAddModal 
        isOpen={isCategoryAddModalOpen} 
        onClose={() => setIsCategoryAddModalOpen(false)} 
        onSave={handleAddCategory} 
        isSaving={isSaving} 
        category={selectedCategoryForModal} 
      />

      {/* Random Movie Section */}

      {/* {randomMovie && searchResults.length === 0 && (
        <RandomMovie movie={randomMovie} onClick={() => setSelectedMovie(randomMovie)} />
      )} */}

      {/* Movie Categories Section */}
      {searchResults.length === 0 ? (
        <div className="movieCategories">
          {movies.length > 0 && movies.map((category, index) => (
            <AdminCategoryRow 
                    key={index} 
                    category={category}  // Remove movies prop since it's included in category
                    onMovieClick={handleMovieClick}
                    onMovieUpdate={handleMovieUpdate} 
                    onMovieDelete={handleMovieDelete}
                    onCategoryDelete={handleCategoryDelete}
                    onCategoryUpdate={handleCategoryUpdate}
                    userInfo={userInfo}
                    loading={loading}

                />          ))}
        </div>
      ) : (
        <SearchResults searchResults={searchResults} handleMovieClick={handleMovieClick} />
      )}

      {/* Movie Popup */}
      {selectedMovie && (
        <MoviePopup userInfo = {userInfo} initialMovie={selectedMovie} onClose={() => setSelectedMovie(null)} />
    )}
      <Link to="/another">Go to Another Page</Link>
    </div>
  );
}

export default HomeScreen;
