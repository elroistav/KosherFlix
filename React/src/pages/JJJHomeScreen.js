import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { useNavigate } from 'react-router-dom'; 
import { Link } from 'react-router-dom';
import RandomMovie from '../components/RandomMovie';
import CategoryRow from '../components/CategoryRow';
import Navbar from '../components/NavBar';  // Importing the Navbar component
import AdminCategoryRow from '../components/AdminCategoryRow';  // Importing the AdminCategoryRow component
import AdminCategoryRow from '../components/AdminCategoryRow';  // Importing the AdminCategoryRow component
import MoviePopup from '../components/MoviePopup';  // assuming you already have the modal component
import SearchResults from '../components/SearchResults';  // assuming you already have the search results component
import '../styles/HomeScreen.css';
import { useLocation } from 'react-router-dom';
//import user from '../../../NetflixProj3/models/user';
//import user from '../../../NetflixProj3/models/user';


function HomeScreen( {isAdmin} ) {
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
          });

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
      return; // חכה עד שה- loading יסתיים ו- userInfo יהיה זמין
    }

    async function fetchMovies() {
      try {
        //get user-id from the tokes first
        console.log('The user id is: ' + userInfo.userId);
        const response = await axios.get(BASE_URL + '/api/movies', {
          headers: { 'user-id': userInfo.userId }
        });

        // Fetch the details of each movie in each category
        const fetchedMoviesByCategory = [];
        for (const category of response.data.promotedCategories) {
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
          fetchedMoviesByCategory.push({ category: category.category, movies: fetchedMovies });
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
  }, []);

  const handleCategoryDelete = async (categoryId) => {
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

  const handleMovieDelete = async (movieId) => {
    try {
        // Update UI state first
        const updatedMovies = movies.map(category => ({
            ...category,
            movies: category.movies.filter(movie => movie._id !== movieId)
        }));
        
        setMovies(updatedMovies);
        
        // Update categories in backend
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

  const handleMovieUpdate = (updatedMovie) => {
    const updatedMovies = movies.map(category => ({
        ...category,
        movies: category.movies.map(movie => 
            movie._id === updatedMovie._id ? updatedMovie : movie
        )
    }));
    setMovies(updatedMovies);
};

  if (loading) {
    return <div>Loading...</div>; // Show a loading spinner or message
  }

  return (
    <div className="homeScreenBody">
        <Navbar 
        onSearchResults={handleSearchResults} 
        clearSearchResults={clearSearchResults}
        userInfo={userInfo} 
        loading={loading}
      /> 
      {/* Random Movie Section */}
      {randomMovie && searchResults.length === 0 && (
        <RandomMovie movie={randomMovie} onClick={() => setSelectedMovie(randomMovie)} />
      )}

      {/* Movie Categories Section */}
        {searchResults.length === 0 ? (
          <div className="movieCategories">
          {movies.length > 0 && movies.map((category, index) => (
            isAdmin ? (
                <AdminCategoryRow 
                    key={index} 
                    category={category}  // Remove movies prop since it's included in category
                    onMovieClick={handleMovieClick}
                    onMovieUpdate={handleMovieUpdate} 
                    onMovieDelete={handleMovieDelete}
                    onCategoryDelete={handleCategoryDelete}
                />
            ) : (
                <CategoryRow 
                    key={index} 
                    categoryName={category.category} 
                    movies={category.movies} 
                    onMovieClick={handleMovieClick} 
                />
            )
        ))}
          </div>
        ) : (
          <SearchResults searchResults={searchResults} handleMovieClick={handleMovieClick} isAdmin={isAdmin} />
        )}

        {/* Movie Popup */}
        {/* Movie Popup */}
      {selectedMovie && (
        <MoviePopup initialMovie={selectedMovie} onClose={() => setSelectedMovie(null)} />
      )}
      <Link to="/another">Go to Another Page</Link>
    </div>
  );
}

export default HomeScreen;
