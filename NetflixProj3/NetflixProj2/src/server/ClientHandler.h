#include <mutex>
#ifndef NETFLIX_PROJ2_CLIENTHANDLER_H
#define NETFLIX_PROJ2_CLIENTHANDLER_H

using namespace std;

class ClientHandler {
public:
    virtual ~ClientHandler() = default;  // Virtual destructor for proper cleanup
    virtual void handleClient(int clientSocket);     //virtual method (abstract)
private:
    std::mutex mtx;
};


#endif //NETFLIX_PROJ2_CLIENTHANDLER_H
