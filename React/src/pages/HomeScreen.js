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
  const [searchText, setSearchText] = useState('');

  useEffect(() => {
    document.body.className = isDarkMode ? 'dark-mode' : 'light-mode';
  }, [isDarkMode]);
  

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
          console.log('The user id is: ' + userInfo.userId);
          const response = await axios.get(BASE_URL + '/api/movies', {
              headers: { 'user-id': userInfo.userId }
          });
  
          const fetchedMoviesByCategory = [];
  
          // Fetch promoted category movies
          for (const category of response.data.promotedCategories) {
              const fetchedMovies = await fetchMoviesByIds(category.movies);
              fetchedMoviesByCategory.push({ category: category.category, movies: fetchedMovies });
          }
  
          // Fetch last watched movies
          if (response.data.lastWatched) {
              const lastWatchedMovies = await fetchMoviesByIds(response.data.lastWatched.movies);
              fetchedMoviesByCategory.push({ category: "Last Watched", movies: lastWatchedMovies });
          }
  
          setMovies(fetchedMoviesByCategory);
  
          // Set a random movie
          if (fetchedMoviesByCategory.length > 0) {
              const randomCategory = fetchedMoviesByCategory[Math.floor(Math.random() * fetchedMoviesByCategory.length)];
              if (randomCategory.movies.length > 0) {
                  const random = randomCategory.movies[Math.floor(Math.random() * randomCategory.movies.length)];
                  setRandomMovie(random);
              }
          }
      } catch (error) {
          console.error("Error fetching categories or movie details:", error);
      }
  }
  
  // Helper function to fetch movie details by IDs
  async function fetchMoviesByIds(movieIds) {
      const fetchedMovies = [];
      for (const movieId of movieIds) {
          try {
              const movieResponse = await axios.get(`${BASE_URL}/api/movies/${movieId}`, {
                  headers: { 'user-id': userInfo.userId }
              });
              console.log('movieResponse:', movieResponse.data.thumbnail);
              fetchedMovies.push(movieResponse.data);
          } catch (error) {
              console.error(`Error fetching movie details for movieId ${movieId}:`, error);
          }
      }
      return fetchedMovies;
  }
  

    fetchMovies();
  }, [loading, userInfo]);

  // Handle search results
  const handleSearchResults = (results, text) => {
    setSearchResults(results);
    setSearchText(text);
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
        isDarkMode={isDarkMode}
        setIsDarkMode={setIsDarkMode}
      /> 
      {/* Random Movie Section */}
      {randomMovie && searchResults.length === 0 && (
         <RandomMovie 
         movie={randomMovie} 
         onClick={() => setSelectedMovie(randomMovie)} 
         userInfo={userInfo}  
       />
      )}

      {/* Movie Categories Section */}
      {searchResults.length === 0 ? (
        <div className="movieCategories">
          {movies.length > 0 && movies.map((category, index) => (
            <CategoryRow key={index} categoryName={category.category} movies={category.movies} onMovieClick={handleMovieClick} />
          ))}
        </div>
      ) : (
        <SearchResults searchResults={searchResults} handleMovieClick={handleMovieClick} searchText={searchText} />
      )}

      {/* Movie Popup */}
      {selectedMovie && (
        <MoviePopup userInfo = {userInfo} initialMovie={selectedMovie} onClose={() => setSelectedMovie(null)} />
      )}
    </div>
  );
}

export default HomeScreen;
