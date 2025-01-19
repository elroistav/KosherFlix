//
// Created by Yoni Trachtenberg on 11/21/24.
//

#include "App.h"
#include <iostream>
using namespace std;
#include "Patch.h"
#include "InputValidator.h"
#include "CommandUtils.h"

void App::run() {
    CommandUtils::initCommandUtils();
    string command;
    while (true) {
        if (!getline(cin, command)) {
            break; // Exit loop on stream failure (e.g., EOF)
        }
        cout << InputValidator::checkInputValidity(command) << endl;
    }
}