#include <iostream>
#include <thread>
#include <mutex>
#include <atomic>
#include <vector>
#include <queue>
#include <condition_variable>
#include <string>
#include <cstring>
#include <unistd.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include "ThreadpoolManager.h"
#include "ClientHandler.h"


#define PORT 8082
#define MAX_CLIENTS 3
#define BUFFER_SIZE 1024



    void ThreadpoolManager::workerFunction() {
        while (true) {
            int clientSocket;
            {
                std::unique_lock<std::mutex> lock(queueMutex);
                condition.wait(lock, [this]() { return stop || !tasks.empty(); });

                if (stop && tasks.empty())
                    return;

                clientSocket = tasks.front();
                tasks.pop(); 

            }

            // Process the client outside the critical section
            ClientHandler clientHandler;

            try {
                clientHandler.handleClient(clientSocket);
                } catch (const std::exception &e) {
                    std::cerr << "Error handling client: " << e.what() << std::endl;
                }
            
        }
    }


    ThreadpoolManager::ThreadpoolManager(size_t numThreads) : stop(false) {
        for (size_t i = 0; i < numThreads; ++i) {
            workers.emplace_back([this]() { workerFunction(); });
        }
    }

    ThreadpoolManager::~ThreadpoolManager() {
        {
            std::unique_lock<std::mutex> lock(queueMutex);
            stop = true;
        }

        condition.notify_all();
        for (std::thread &worker : workers) {
            if (worker.joinable()) {
                worker.join();
            }
        }
    }

    void ThreadpoolManager::manageClientThread(int clientSocket) {
        {
            std::unique_lock<std::mutex> lock(queueMutex);
            tasks.push(clientSocket);
        }
        condition.notify_one();
    }
