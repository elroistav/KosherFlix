//
// Created by Yoni Trachtenberg on 12/10/24.
//

#include "Post.h"
#include "data_access/FileDataAccess.hpp"
#include <stdexcept>

using namespace std;

string Post::execute(int user_id, const vector<int>& ids) {
    if (user_id < 0 || ids.empty()) {
        throw runtime_error("Invalid input");
    }
    std::set<int> idsSet(ids.begin(), ids.end());
    if (!checkInput(user_id, ids)) {
        throw invalid_argument("Invalid input");
    }
    FileDataAccess fileDataAccess;
    fileDataAccess.addMovies(user_id, idsSet);
    return "201 Created";
}

bool Post::checkInput(int user_id, const vector<int>& ids) {
    // Check to make sure the userId doesn't already exists
    FileDataAccess fda = FileDataAccess();
    set<int> allIds = fda.getUserIds();
    if (allIds.find(user_id) == allIds.end()) return true;

    return false;
}

string Post::help() {
    return "POST, arguments: [userid] [movieid1] [movieid2] ...";
}