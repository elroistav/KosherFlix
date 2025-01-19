#pragma once

#include <set>

/**
 * @class DataAccess
 * @brief Abstract base class for data access operations related to users and movies.
 *
 * This class provides an interface for adding movies to a user's list, retrieving user IDs,
 * retrieving movies associated with a user, and retrieving users associated with a movie.
 */
 
/**
 * @brief Virtual destructor for DataAccess.
 */
 
/**
 * @brief Adds a set of movies to a user's list.
 * @param userID The ID of the user.
 * @param movieIDs A set of movie IDs to be added to the user's list.
 */
 
/**
 * @brief Retrieves a set of all user IDs.
 * @return A set of user IDs.
 */
 
/**
 * @brief Retrieves a set of movies associated with a specific user.
 * @param userID The ID of the user.
 * @return A set of movie IDs associated with the user.
 */
 
/**
 * @brief Retrieves a set of users associated with a specific movie.
 * @param movieID The ID of the movie.
 * @return A set of user IDs associated with the movie.
 */
class DataAccess {
public:
    virtual ~DataAccess() = default;
    virtual void addMovies(const int userID, const std::set<int>& movieIDs) = 0;
    virtual std::set<int> getUserIds() = 0;
    virtual std::set<int> getMoviesForUser(const int userID) = 0;
    virtual std::set<int> getUsersForMovie(const int movieID) = 0;
    virtual void deleteMovies(const int userID, const std::set<int>& movieIDs) = 0;
};
