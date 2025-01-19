//
// Created by Yoni Trachtenberg on 11/26/24.
//

#ifndef PROJ1YT_YD_ES_HELPDISPLAY_H
#define PROJ1YT_YD_ES_HELPDISPLAY_H
#include <set>
#include <string>
#include "ICommand.h"



class HelpDisplay : public ICommand{
public:
    ~HelpDisplay() override;
    string execute(int user_id, const vector<int>& ids) override;
    bool checkInput(int user_id, const vector<int>& ids) override;
    string help() override;
};


#endif //PROJ1YT_YD_ES_HELPDISPLAY_H
