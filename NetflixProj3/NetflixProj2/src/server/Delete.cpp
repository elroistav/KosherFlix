//
// Created by Yoni Trachtenberg on 12/11/24.
//

#include "Delete.h"
#include "data_access/FileDataAccess.hpp"
#include <stdexcept>

using namespace std;

string Delete::execute(int user_id, const vector<int>& ids) {
    if (user_id < 0 || ids.empty()) {
       throw runtime_error("Invalid input");
    }
    std::set<int> idsSet(ids.begin(), ids.end());
    if (!checkInput(user_id, ids)) {
        throw invalid_argument("Invalid input");
    }
    FileDataAccess fileDataAccess;
    fileDataAccess.deleteMovies(user_id, idsSet);
    return "204 No Content";
}

bool Delete::checkInput(int user_id, const vector<int>& ids) {
    if (user_id < 0 || ids.empty()) {
        return false;
    }
    // Check to make sure the userId already exists
    FileDataAccess fda = FileDataAccess();
    set<int> allIds = fda.getUserIds();
    if (allIds.find(user_id) == allIds.end()) return false;

    // Makes sure that all the movies to delete are in the user's list
    set<int> userMovies = fda.getMoviesForUser(user_id);
    for (int movieId : ids) {
        if (userMovies.find(movieId) == userMovies.end()) {
            return false;
        }
    }

    return true;
}

string Delete::help() {
    return "DELETE, arguments: [userid] [movieid1] [movieid2] ...";
}
