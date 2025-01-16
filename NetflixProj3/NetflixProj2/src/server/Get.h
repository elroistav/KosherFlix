//
// Created by Yoni Trachtenberg on 11/25/24.
//

#ifndef PROJ1YT_YD_ES_RECOMMENDMOVIE_H
#define PROJ1YT_YD_ES_RECOMMENDMOVIE_H
#include "ICommand.h"
#include <set>
using namespace std;

class Get : public ICommand{
public:
    string execute(int user_id, const vector<int>& ids) override;
    bool checkInput(int user_id, const vector<int>& ids) override;
    string help() override;

};


#endif //PROJ1YT_YD_ES_RECOMMENDMOVIE_H
