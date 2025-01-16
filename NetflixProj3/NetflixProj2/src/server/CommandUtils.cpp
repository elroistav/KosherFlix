//
// Created by Yoni Trachtenberg on 11/24/24.
//
#include <sstream>
#include <iostream>
#include "CommandUtils.h"
#include "Patch.h"
#include "Post.h"
#include "HelpDisplay.h"
#include "Delete.h"
#include "Get.h"
#include <vector>
#include <unordered_map>

vector<ICommand*> CommandUtils::commands;
unordered_map<string, ICommand*> CommandUtils::map;
bool CommandUtils::isInitialized = false;

/**
 * Destructor to delete all the commands
 */
CommandUtils::~CommandUtils() {
    for (ICommand* command : commands) {
        delete command;
    }
}

/**
 * converts the given string to the correct command in the enum
 * @param commandStr the string to convert
 * @return the correct command for the given string
 */
Commands CommandUtils::stringToCommand(const string &commandStr) {
    if (commandStr == "GET") {
        return Commands::GET;
    } else if (commandStr == "DELETE") {
        return Commands::DELETE;
    } else if (commandStr == "PATCH") {
        return Commands::PATCH;
    } else if (commandStr == "POST") {
        return Commands::POST;
    } else if (commandStr == "help") {
        return Commands::help;
    } else {
        return Commands::Invalid;
    }
}

/**
* displays all the commands that the user can use
*/
string CommandUtils::allCommands() {
    string allCommands;
    for (ICommand* command : commands) {
        allCommands.append(command->help() + "\n");
    }
    return allCommands;
}

void CommandUtils::initCommandUtils() {
    if (isInitialized) {
        return;
    }
    // Initialize commands and map with shared objects
    ICommand* deleteCommand = new Delete();
    ICommand* getCommand = new Get();
    ICommand* patchCommand = new Patch();
    ICommand* postCommand = new Post();
    ICommand* helpCommand = new HelpDisplay();

    // Add to vector
    commands.push_back(deleteCommand);
    commands.push_back(getCommand);
    commands.push_back(patchCommand);
    commands.push_back(postCommand);
    commands.push_back(helpCommand);

    // Add to map
    map["DELETE"] = deleteCommand;
    map["GET"] = getCommand;
    map["PATCH"] = patchCommand;
    map["POST"] = postCommand;
    map["help"] = helpCommand;
    isInitialized = true;
}

/**
 * returns the map of commands
 * @return the map of commands
 */
unordered_map<string, ICommand*>& CommandUtils::getMap(){
    return map;
}
