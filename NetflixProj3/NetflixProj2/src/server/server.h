#ifndef NETFLIX_PROJ2_SERVER_H
#define NETFLIX_PROJ2_SERVER_H

#include <mutex>
#include "IThreadsManager.h"


using namespace std;

class server {
public:
    virtual ~server() = default;  // Virtual destructor for proper cleanup
    int main();
    //void bindSocket();
};


#endif //NETFLIX_PROJ2_SERVER_H