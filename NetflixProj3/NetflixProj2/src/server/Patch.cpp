//
// Created by Yoni Trachtenberg on 11/22/24.
//

#include "Patch.h"
#include <stdexcept>
#include "data_access/FileDataAccess.hpp"
#include <stdexcept>

using namespace std;

string Patch::execute(int user_id, const vector<int>& ids) {
    if (user_id < 0 || ids.empty()) {
        throw runtime_error("Invalid input");
    }
    std::set<int> idsSet(ids.begin(), ids.end());
    if (!checkInput(user_id, ids)) {
        throw invalid_argument("Invalid input");
    }
    FileDataAccess fileDataAccess;
    fileDataAccess.addMovies(user_id, idsSet);
    return "204 No Content";
}

bool Patch::checkInput(int user_id, const vector<int>& ids) {
    // Check to make sure the userId already exists
    FileDataAccess fda = FileDataAccess();
    set<int> allIds = fda.getUserIds();
    if (allIds.find(user_id) == allIds.end()) return false;

    return true;
}

string Patch::help() {
    return "PATCH, arguments: [userid] [movieid1] [movieid2] ...";
}
