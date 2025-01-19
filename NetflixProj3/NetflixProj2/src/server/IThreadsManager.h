#ifndef NETFLIX_PROJ2_ITHREADSMANAGER_H
#define NETFLIX_PROJ2_ITHREADSMANAGER_H

#include <vector>
#include <set>
#include <string>
using namespace std;

class IThreadsManager {
public:
    virtual ~IThreadsManager() = default;  // Virtual destructor for proper cleanup
    virtual void manageClientThread(int clientSocket) = 0;     //virtual method (abstract)
};


#endif //NETFLIX_PROJ2_ITHREADSMANAGER_H