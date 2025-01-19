//#include "data_access/files.hpp"
#include <fstream>
#include <filesystem>
#include <iostream>
#include "FileDataAccess.hpp"

using namespace std;
namespace fs = std::filesystem;

const string FileDataAccess::prefix = "user_";
const string FileDataAccess::suffix = ".txt";
const string FileDataAccess::directory = "data/Users";

void FileDataAccess::addMovies(const int userID, const set<int>& movieIDs) {
    set<int> allMoviesForThisUser = this->getMoviesForUser(userID); // Load existing movies
    allMoviesForThisUser.insert(movieIDs.begin(), movieIDs.end()); // Add new movies using iterators

    storeMovies(userID, allMoviesForThisUser); // Store all movies
}

set<int> FileDataAccess::getUserIds() {
    set<int> userIDs;
    for (const auto& entry : fs::directory_iterator(FileDataAccess::directory)) {
        string filename = entry.path().filename().string();
        userIDs.insert(getIDFromFilename(filename));
    }
    return userIDs;
}

set<int> FileDataAccess::getMoviesForUser(const int userID) {
    set<int> movieIDs;
    string filename = getFilename(userID);
    ifstream file(filename);
    if (!file.is_open()) {
        return movieIDs;
    }

    int movieID;
    string line;
    while (getline(file, line)) {
        movieID = stoi(line);
        movieIDs.insert(movieID);
    }
    file.close();
    return movieIDs;
}


set<int> FileDataAccess::getUsersForMovie(const int movieID) {
    set<int> userIDs;
    for (const auto& entry : fs::directory_iterator("data/Users")) {
        string filename = entry.path().filename().string();
        int userID = getIDFromFilename(filename);

        set<int> movieIDs = getMoviesForUser(userID);
        if (movieIDs.find(movieID) != movieIDs.end()) {
            userIDs.insert(userID);
        }
    }

    return userIDs;
}

// Static method
void FileDataAccess::storeMovies(const int userID, const set<int>& movieIDs) {
    // Create Users directory if it doesn't exist in the path near the CMakeFile
    fs::create_directory(fs::current_path().string() + "/" + FileDataAccess::directory);

    // Looks in the parent folder for Users and put the file there
    ofstream file(FileDataAccess::getFilename(userID), ios::trunc); // Open file in trunc mode - overwrites existing file

    for (int movieID : movieIDs) {
        file << movieID << endl;
    }

    file.close();
}

string FileDataAccess::getFilename(const int userID) {
    return FileDataAccess::directory + "/" + FileDataAccess::prefix + to_string(userID) + FileDataAccess::suffix;
}

int FileDataAccess::getIDFromFilename(const string& filename) {
    const size_t prefixLength = FileDataAccess::prefix.size();
    const size_t suffixLength = FileDataAccess::suffix.size();
    return stoi(filename.substr(prefixLength, filename.size() - prefixLength - suffixLength));
}

void FileDataAccess::deleteMovies(const int userID, const set<int>& movieIDs) {
    set<int> allMoviesForThisUser = this->getMoviesForUser(userID); // Load existing movies
    for (int movieID : movieIDs) {
        allMoviesForThisUser.erase(movieID);
    }
    storeMovies(userID, allMoviesForThisUser); // Store all movies
}
