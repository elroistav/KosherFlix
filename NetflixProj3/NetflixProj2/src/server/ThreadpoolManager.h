#ifndef THREADPOOLSMANAGER_CPP
#define THREADPOOLSMANAGER_CPP

#include "IThreadsManager.h"
#include <thread>
#include <vector>

class ThreadpoolManager : public IThreadsManager{
public:
    ThreadpoolManager(size_t numThreads);
    ~ThreadpoolManager();
    void manageClientThread(int clientSocket);
private:
    std::vector<std::thread> workers;
    std::queue<int> tasks;
    std::mutex queueMutex;
    std::condition_variable condition;
    std::atomic<bool> stop;
    void workerFunction();

};

#endif //THREADPOOLSMANAGER_CPP