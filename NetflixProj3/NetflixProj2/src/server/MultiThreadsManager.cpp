
#include "MultiThreadsManager.h"
#include "ClientHandler.h"
#include <thread>

MultiThreadsManager::MultiThreadsManager() {}

void MultiThreadsManager::manageClientThread(int clientSocket) {
    try {
        threads.emplace_back([clientSocket]() {
            ClientHandler clientHandler;
            clientHandler.handleClient(clientSocket);
        });
    } catch (const std::exception& e) {
       // std::cerr << "Failed to create thread: " << e.what() << std::endl;
    }
}

MultiThreadsManager::~MultiThreadsManager() {
    for (std::thread& t : threads) {
        if (t.joinable()) {
            t.join();
        }
    }
}