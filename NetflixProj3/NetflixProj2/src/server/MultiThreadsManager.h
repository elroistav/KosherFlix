#ifndef MULTITHREADSMANAGER_CPP
#define MULTITHREADSMANAGER_CPP

#include "IThreadsManager.h"
#include <thread>
#include <vector>

class MultiThreadsManager : public IThreadsManager{
public:
    MultiThreadsManager();
    ~MultiThreadsManager();
    void manageClientThread(int clientSocket);
private:
    std::vector<std::thread> threads;

};

#endif //MULTITHREADSMANAGER_CPP