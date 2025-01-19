//
// Created by Yoni Trachtenberg on 11/23/24.
//
#include <sstream>
#include <string>
#include <iostream>
#include <set>
#include <algorithm>
#include <unordered_map>
using namespace std;
#include "InputValidator.h"
#include "ICommand.h"
#include "Commands.h"
#include "CommandUtils.h"
/**
 * makes sure that the input is generically valid meaning that it has a legal command at the
 * start and that the rest of the words are integers greater than 0.
 * @param input the string input
 * @param commands a map which maps commands to pointers of the correct object
 * @return 0 if something is wrong with the input in general, otherwise returns the int
 * corresponding to the command executed.
 * @throw runtime_error if the command is not found or if the user didnt input a command. 400 Bad Request
 * @throw invalid_argument if the input is not valid. 404 Not Found
 */
string InputValidator::checkInputValidity(string input) {
    try {
        unordered_map<string, ICommand*> commands = CommandUtils::getMap();
        string commandSTR = getCommand(input);
        int userId = getUserId(input);
        vector<int> ids = InputValidator::extractNumbers(input);
        auto mapCommand = commands.find(commandSTR);
        //Commands cmd = CommandUtils::stringToCommand(commandSTR);
        cout << commands[commandSTR] << endl;
        
        string response = mapCommand->second->execute(userId, ids);
        return response;
    } catch (std::invalid_argument& e) {
        return "404 Not Found";
    } catch (runtime_error& e) {
        return "400 Bad Request";
    }
};

/**
 * Extracts the command from the input string
 * @param input the string to extract the command from
 * @return the string representation of the command
 * @throw runtime_error if the command is not found or if the user didnt input a command
 */
string InputValidator::getCommand(string& input) {
    int spaceIndex = input.find(' ');
    // If the word starts with a space character or is empty then throw an exception
    if (input.empty() || spaceIndex == 0) throw runtime_error("Invalid input: Command not found");;
    string firstWord = input.substr(0, spaceIndex);
    if (spaceIndex == string::npos) {
        input = "";
    } else {
        input = input.substr(spaceIndex + 1, input.length());
    }

    if (CommandUtils::stringToCommand(firstWord) == Commands::Invalid) {
        throw runtime_error("Invalid input: Command not found");
    }
    return firstWord;
}

/**
 * Extracts the user id from the input string
 * @param input string to extract the user id from
 * @return -1 if there is no user id in the string, otherwise returns the user id
 * @throw runtime_error if the user id is an invalid format
 */
int InputValidator::getUserId(string& input) {
    // Remove any leading spaces
    int startId = input.find_first_not_of(' ');
    if (startId == string::npos || input.empty()) {
        return -1;
    }
    input = input.substr(startId, input.length());

    // Extract the user id from the input
    int endId = input.find(' ');
    string userIdString;
    if (endId == string::npos) {
        userIdString = input;
        input = "";
    } else {
        userIdString = input.substr(0, endId);
        input = input.substr(endId, input.length());
        // Remove any spaces after the user id
        endId = input.find_first_not_of(' ');
        if (endId != string::npos) {
            input = input.substr(endId, input.length());
        } else {
            input = "";
        }
    }

    // Makes sure all the remaining word in the input are integers
    if (!all_of(userIdString.begin(), userIdString.end(), ::isdigit)){
        throw runtime_error("Invalid input: Invalid user id");
    }
    int userId = stoi(userIdString);
    if (userId < 0) {
        throw runtime_error("Invalid input: Invalid user id");
    }
    return userId;
}

/**
 * given a string it will take all the ints in it and convert them into a set<int>
 * @param input the string of ints to convert
 * @return if the string contained any non ints then it will return and empty set
 * otherwise it return the set of all the ints
 * @throw runtime exception if the string contained anything other than just spaces and digits
 */
vector<int> InputValidator::extractNumbers(const string& input) {
    vector<int> numbers;
    istringstream stream(input);  // Create a stream from the input string
    string word;

    // Extract the numbers from the rest of the string
    while (stream >> word) {
        // Check if the word is a number
        if (all_of(word.begin(), word.end(), ::isdigit)) {
            numbers.push_back(stoi(word));  // Convert to int and store in vector
        } else {
            // We found an input which wasn't an integer
            throw runtime_error("Invalid input: Non-numeric characters found.");
        }
    }

    return numbers;
}