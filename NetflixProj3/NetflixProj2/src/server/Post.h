//
// Created by Yoni Trachtenberg on 12/10/24.
//

#ifndef PROJ1YT_YD_ES_POST_H
#define PROJ1YT_YD_ES_POST_H


#include <set>
#include "ICommand.h"
#include <string>
using namespace std;

class Post : public ICommand{

public:
    /**
     * adds a movie to the list of movies that the user has watched
     * @param user_id the id of the user who watched the movie
     * @param ids the ids of the movies that the user watched
     */
    string execute(int user_id, const vector<int>& ids) override;
    /**
     * checks if the input is valid for the add method
     * @param user_id the id of the user who watched the movie
     * @param ids the ids of the movies that the user watched
     * @return true if the input is valid, false otherwise
     */
    bool checkInput(int user_id, const vector<int>& ids) override;
    /**
    * returns the string message for the add command
    * @return the string message for the add command
    */
    string help() override;
};


#endif //PROJ1YT_YD_ES_POST_H
