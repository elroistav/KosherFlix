
#pragma once

#include <set>
#include <string>
//#include <data_access/DataAccess.hpp>
#include "DataAccess.hpp"

/**
 * @brief A class that handles file-based data access operations for user-movie relationships
 * 
 * This class implements the DataAccess interface to provide file-based storage and retrieval
 * of movie data associated with users. Each user's movie data is stored in a separate file
 * with a standardized naming convention.
 *
 * @note Files are stored using prefix and suffix conventions in a specified directory
 *
 * @see DataAccess
 */
/**
 * @brief A class that handles file-based data access operations for user-movie relationships
 * 
 * This class implements the DataAccess interface to provide file-based storage and retrieval
 * of movie data associated with users. Each user's movie data is stored in a separate file
 * with a standardized naming convention.
 *
 * @note Files are stored using prefix and suffix conventions in a specified directory
 *
 * @see DataAccess
 */
class FileDataAccess : public DataAccess {
public:
    FileDataAccess() = default;
    ~FileDataAccess() override = default;

    void addMovies(const int userID, const std::set<int>& movieIDs) override;
    std::set<int> getUserIds() override;
    std::set<int> getMoviesForUser(const int userID) override;
    std::set<int> getUsersForMovie(const int movieID) override;
    static void storeMovies(const int userID, const std::set<int>& movieIDs);
    static std::string getFilename(const int userID);
    static int getIDFromFilename(const std::string& filename);
    void deleteMovies(const int userID, const std::set<int>& movieIDs) override;


private:
    static const std::string prefix;
    static const std::string suffix;
    static const std::string directory;    
};
