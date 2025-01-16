//
// Created by Yoni Trachtenberg on 11/24/24.
//

#ifndef PROJ1YT_YD_ES_COMMANDUTILS_H
#define PROJ1YT_YD_ES_COMMANDUTILS_H
#include <sstream>
#include <vector>
#include "Commands.h"
#include "ICommand.h"
#include <unordered_map>
using namespace std;

/**
 * @class CommandUtils
 * @brief Utility class for managing and executing commands.
 *
 * This class provides static methods to initialize, map, and execute commands.
 * It maintains a list of commands and a map for quick lookup.
 */
 
/**
 * @brief Constructor to initialize the commands and map.
 */
 
/**
 * @brief Destructor to clean up resources.
 */
 
/**
 * @brief Converts a string to a command.
 * 
 * @param commandStr The string representation of the command.
 * @return The corresponding command.
 */
 
/**
 * @brief Displays the list of available commands.
 */
 
/**
 * @brief Initializes the command utilities.
 * 
 * This method sets up the commands and the map for quick lookup.
 */
 
/**
 * @brief Gets the map of command strings to ICommand pointers.
 * 
 * @return A reference to the map of command strings to ICommand pointers.
 */
class CommandUtils {
private:
    static vector<ICommand*> commands;
    static unordered_map<string, ICommand*> map;
    static bool isInitialized;
public:
    // Constructor to initialize the commands and map
    CommandUtils() = default;
    ~CommandUtils();
    static Commands stringToCommand(const string &commandStr);
    static string allCommands();
    static void initCommandUtils();
    static unordered_map<string, ICommand*>& getMap();
};


#endif //PROJ1YT_YD_ES_COMMANDUTILS_H
