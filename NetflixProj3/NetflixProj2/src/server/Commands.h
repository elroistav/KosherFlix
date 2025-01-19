/**
 * @file Commands.h
 * @brief Defines the Commands enum class for various command types.
 *
 * This file contains the definition of the Commands enum class, which
 * enumerates different types of commands that can be used in the application.
 *
 * @enum Commands
 * @brief Enumeration of command types.
 * 
 * @var Commands::Invalid
 * Represents an invalid command.
 * 
 * @var Commands::add
 * Represents the 'add' command.
 * 
 * @var Commands::recommend
 * Represents the 'recommend' command.
 * 
 * @var Commands::help
 * Represents the 'help' command.
 * 
 * @date Created on 11/23/24 by Yoni Trachtenberg
 */
//
// Created by Yoni Trachtenberg on 11/23/24.
//

#ifndef PROJ1YT_YD_ES_COMMANDS_H
#define PROJ1YT_YD_ES_COMMANDS_H


enum class Commands {
    Invalid = 0,
    PATCH = 1,
    GET = 2,
    help = 3,
    DELETE = 4,
    POST = 5
};


#endif //PROJ1YT_YD_ES_COMMANDS_H
