//
// Created by Yoni Trachtenberg on 11/26/24.
//
#include <string>
#include <set>
#include "HelpDisplay.h"
#include "CommandUtils.h"
#include <iostream>
using namespace std;

HelpDisplay::~HelpDisplay() = default;
string HelpDisplay::execute(int user_id, const vector<int> &ids) {
    std::set<int> idsSet(ids.begin(), ids.end());
    string output = "200 Ok\n\n";
    if (!checkInput(user_id, ids)) {
        throw runtime_error("Invalid input");
    }
    output += CommandUtils::allCommands();
    return output;
}

bool HelpDisplay::checkInput(int user_id, const vector<int> &ids) {
    return user_id==-1 && ids.empty();
}

string HelpDisplay::help() {
    return "help";
}
