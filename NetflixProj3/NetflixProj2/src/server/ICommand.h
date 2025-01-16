//
// Created by Yoni Trachtenberg on 11/22/24.
//


#ifndef PROJ1YT_YD_ES_ICOMMAND_H
#define PROJ1YT_YD_ES_ICOMMAND_H

#include <vector>
#include <set>
#include <string>
using namespace std;

class ICommand{
public:
    virtual ~ICommand() = default;  // Virtual destructor for proper cleanup
    /**
     * Executes the command
     * @param user_id the id of the user who is executing the command
     * @param ids the ids of the movies that the user watched
     * @return if successful returns the string of the code returned back (e.g. 204 No Content)
     * @throw runtime_error if the command is not found or if the user didnt input a command. 400 Bad Request
     * @throw invalid_argument if the input is not valid. 404 Not Found
     */
    virtual string execute(int user_id, const vector<int>& ids) = 0;     // Pure virtual method (abstract)
    virtual bool checkInput(int user_id, const vector<int>& ids) = 0;
    virtual string help() = 0;
};


#endif //PROJ1YT_YD_ES_ICOMMAND_H
