#pragma once

#include <set>
#include <string>
#include <map>
#include "data_access/FileDataAccess.hpp"
#include <vector>
using namespace std;

/**
 * @class RecExecuter
 * @brief Executes movie recommendations for a user.
 * 
 * This class is responsible for executing movie recommendations based on user and movie IDs.
 * It provides methods to find relevant users, match levels, movie ratings, and top 10 movies.
 * 
 * @public
 * @constructor
 * @param user_id The ID of the user for whom recommendations are being generated.
 * @param movie_id The ID of the movie for which recommendations are being generated.
 * 
 * @method executeRec Executes the recommendation process.
 * 
 * @method getRelevantUsers Finds relevant users who have rated the given movie.
 * @param movie_id The ID of the movie.
 * @param user_id The ID of the user.
 * @return A set of user IDs who are relevant for the given movie.
 * 
 * @method findMatchLevel Finds the match level between the given user and relevant users.
 * @param user_id The ID of the user.
 * @param relevantUsers A set of relevant user IDs.
 * @return A map where the key is the user ID and the value is the match level.
 * 
 * @method getMovieRating Gets the movie rating based on user match levels.
 * @param userMatchLevel A map of user match levels.
 * @param movie_id The ID of the movie.
 * @return A map where the key is the movie ID and the value is the rating.
 * 
 * @method getTop10Movies Gets the top 10 movies based on ratings.
 * @param movieRating A map of movie ratings.
 * @return A vector of the top 10 movie IDs.
 * 
 * @method printTop10Movies Prints the top 10 movies.
 * @param top10Movies A vector of the top 10 movie IDs.
 * 
 * @private
 * @var user_id The ID of the user.
 * @var movie_id The ID of the movie.
 * @var relevantUsers A set of relevant user IDs.
 * @var FDAccess An instance of FileDataAccess for accessing file data.
 */

class RecExecuter {
public:
    RecExecuter(int user_id, const int movie_id);
    ~RecExecuter();
    string executeRec();

    std::set<int> getRelevantUsers(int movie_id, int user_id);
    std::map<int, int> findMatchLevel(int user_id, const std::set<int>& relevantUsers);
    std::map<int, int> getMovieRating(const std::map<int, int>& userMatchLevel, int movie_id, int user_id);
    std::vector<int> getTop10Movies(const std::map<int, int>& movieRating);
    string moviesToString(const std::vector<int>& top10Movies);

private:
    int user_id;
    int movie_id;
    std::set<int> relevantUsers;
    FileDataAccess FDAccess;
};
    
