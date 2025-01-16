//
// Created by Yoni Trachtenberg on 11/25/24.
//

#include "Get.h"
#include <iostream>
#include "RecExecuter.h"
using namespace std;

string Get::execute(int user_id, const vector<int>& ids) {
    if (user_id < 0 || ids.size() != 1) {
        throw runtime_error("Invalid input");
    }
    if (!checkInput(user_id, ids)) {
        throw invalid_argument("Invalid input");
    }
    int movieId = *ids.begin();
    RecExecuter myRecExecuter(user_id, movieId);

    string response = "200 Ok\n\n";
    return response.append(myRecExecuter.executeRec());
}

bool Get::checkInput(int user_id, const vector<int>& ids) {
    // Check that there arnt any repetitions in the ids
    set<int> idsSet(ids.begin(), ids.end());
    if (idsSet.size() != ids.size()) return false;
    // Check if the userId is valid
    FileDataAccess fda = FileDataAccess();
    set<int> allIds = fda.getUserIds();

    if (allIds.find(user_id) == allIds.end()) return false;
    return true;
}

string Get::help() {
    return "GET, arguments: [userid] [movieid]";
}

