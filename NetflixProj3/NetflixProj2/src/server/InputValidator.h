//
// Created by Yoni Trachtenberg on 11/23/24.
//


#ifndef PROJ1YT_YD_ES_INPUTVALIDATOR_H
#define PROJ1YT_YD_ES_INPUTVALIDATOR_H
#include <unordered_map>
#include <string>
#include "ICommand.h"
#include "Commands.h"
#include "CommandUtils.h"
using namespace std;

class InputValidator {
public:
    static string checkInputValidity(string input);
    static string getCommand(string& input);
    static int getUserId(string& input);
    static vector<int> extractNumbers(const string& input);
};

#endif //PROJ1YT_YD_ES_INPUTVALIDATOR_H
