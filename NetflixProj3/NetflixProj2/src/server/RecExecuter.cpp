#include <iostream>
#include <fstream>
#include <filesystem>
#include <map>
#include "RecExecuter.h"
#include "data_access/FileDataAccess.hpp"
#include <vector>
#include <algorithm>

using namespace std;
namespace fs = std::filesystem;


RecExecuter::RecExecuter(int user_id, int movie_id)
    : user_id(user_id), movie_id(movie_id) {
    relevantUsers = set<int>();
    FDAccess = FileDataAccess();
}

RecExecuter::~RecExecuter() {
    
}

string RecExecuter::executeRec() {

    relevantUsers = getRelevantUsers(movie_id, user_id);

    map<int, int> userMatchLevel = findMatchLevel(user_id, relevantUsers);

    std::map<int, int> movieRating = getMovieRating(userMatchLevel, movie_id, user_id);

    std::vector<int> top10recommendedMovies = getTop10Movies(movieRating);

    return moviesToString(top10recommendedMovies);
}

std::set<int> RecExecuter::getRelevantUsers(int movie_id, int user_id) {
    set<int> userIDs;
    for (const auto& entry : fs::directory_iterator("data/Users")) {
        string filename = entry.path().filename().string();

        int otherUserID = FDAccess.getIDFromFilename(filename);

        set<int> movieIDs = FDAccess.getMoviesForUser(otherUserID);
        if (movieIDs.find(movie_id) != movieIDs.end()
                    && otherUserID != user_id) { // Skip current user
            userIDs.insert(otherUserID);
        }
    }
    return userIDs;
}

std::map<int, int> RecExecuter::findMatchLevel(int user_id, const std::set<int>& relevantUsers) {
    map<int, int> userMatchLevel; // Map to store the match level for each user <userID, matchLevel>
    set<int> myMovieIDs = FDAccess.getMoviesForUser(user_id);
    for (int otherUserId : relevantUsers) { // Iterate over all relevant users
        set<int> movieIDs = FDAccess.getMoviesForUser(otherUserId); // Get the movies for the other user
        int matchLevel = 0;
        for (int movieID : movieIDs) {
            if (myMovieIDs.find(movieID) != myMovieIDs.end()) { // If the movie is in my list
                matchLevel++;
            }
        }
        userMatchLevel[otherUserId] = matchLevel; // Store the match level for the user
    }
    return userMatchLevel;
}

/*Calculate the rating for each movie based on the match level,
 avoiding the movie that we are recommending and the movies that the user already has.
 */
std::map<int, int> RecExecuter::getMovieRating(const std::map<int, int>& userMatchLevel, int movie_id, int user_id) { 
    map<int, int> movieRating; // Map to store the rating for each movie <movieID, rating>
    for (const auto& [otherUserId, matchScore] : userMatchLevel) {
        set<int> movieIDs = FDAccess.getMoviesForUser(otherUserId);
        for (int movieID : movieIDs) {
            if(movieID != movie_id) { // Skip the movie that we are recommending
                if (!FDAccess.getMoviesForUser(user_id).count(movieID)) { // If the movie is not in the user's list
                // Add the match score to the movie rating, if doesn't exist, it will be initialized with matchScore
                    movieRating[movieID] += matchScore; 
                }
            }
        }
    }
    return movieRating;
}

std::vector<int> RecExecuter::getTop10Movies(const std::map<int, int>& movieRating) {
    // Create a vector of pairs and copy the map contents to it
    std::vector<std::pair<int, int>> movieRatingVector(movieRating.begin(), movieRating.end());

    // Sort the vector by rating in descending order
    std::sort(movieRatingVector.begin(), movieRatingVector.end(), 
              [](const std::pair<int, int>& a, const std::pair<int, int>& b) {
                  return b.second < a.second; // Sort by rating descending
              });

    // Extract the top 10 movies
    std::vector<int> top10Movies;
    for (size_t i = 0; i < 10 && i < movieRatingVector.size(); ++i) {
        top10Movies.push_back(movieRatingVector[i].first); // Add the movie ID to the top 10 list
    }

    return top10Movies;
}

string RecExecuter::moviesToString(const std::vector<int>& top10Movies) {
    string recommendations;
    for (size_t i = 0; i < top10Movies.size(); ++i) {
        if (i != 0) {
            recommendations += " ";
        }
        recommendations += std::to_string(top10Movies[i]);
    }
    return recommendations;
}