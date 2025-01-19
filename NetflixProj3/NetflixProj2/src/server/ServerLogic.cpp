//
// Created by Yoni Trachtenberg on 12/16/24.
//
#include <iostream>
#include <stdio.h>
#include "CommandUtils.h"
#include <unistd.h>
#include <string.h>
#include <thread>
#include <mutex>
#include <atomic>
#include <vector>
#include <queue>
#include <condition_variable>
#include <cstring>
#include <unistd.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include "IThreadsManager.h"
#include "MultiThreadsManager.h"
#include "ThreadpoolManager.h"
#include <iostream>
#include <sys/socket.h>
#include <fstream>
#include "server.h"
#include "ServerLogic.h"

std::atomic<bool> ServerLogic::keepRunning{true};

void ServerLogic::fakeClientClosing() {
    // Create client socket
    int clientSocket = socket(AF_INET, SOCK_STREAM, 0);
    if (clientSocket < 0) {
        perror("error creating socket");
    }
    // Connect to server
    struct sockaddr_in serverAddr;
    memset(&serverAddr, 0, sizeof(serverAddr));
    serverAddr.sin_family = AF_INET;
    serverAddr.sin_port = htons(5555);
    const std::string serverIp = "127.0.0.1";
    inet_pton(AF_INET, serverIp.c_str(), &serverAddr.sin_addr);

    // Connect to server check
    connect(clientSocket, (struct sockaddr*)&serverAddr, sizeof(serverAddr));
       cout << "Client closing: Error connecting to server";

}

int ServerLogic::createServerSocket() {
    int serverSocket = socket(AF_INET, SOCK_STREAM, 0);
    if (serverSocket < 0) {
        perror("error creating socket");
    }
    return serverSocket;
}

struct sockaddr_in ServerLogic::createServerSockaddrIn(int server_port) {
    struct sockaddr_in sin;
    sin.sin_family = AF_INET;
    sin.sin_addr.s_addr = INADDR_ANY;
    sin.sin_port = htons(server_port);
    return sin;
}


void ServerLogic::startServer(int server_port_arg) {
        keepRunning = true;
        const int server_port = server_port_arg;
        CommandUtils::initCommandUtils();

        int serverSocket = ServerLogic::createServerSocket();
        struct sockaddr_in sin = ServerLogic::createServerSockaddrIn(server_port);

        // Set SO_REUSEADDR to reuse the address immediately after shutdown
        int opt = 1;
        if (setsockopt(serverSocket, SOL_SOCKET, SO_REUSEADDR, &opt, sizeof(opt)) < 0) {
            perror("Error setting socket options");
            throw std::runtime_error("Setting SO_REUSEADDR failed");
    }

        if (bind(serverSocket, (struct sockaddr *) &sin, sizeof(sin)) < 0) {
            perror("Error binding socket!!!!!!!!!");
            throw std::runtime_error("Binding failed");
        }

        if (listen(serverSocket, SOMAXCONN) < 0) {
            perror("Error listening on socket");
            throw std::runtime_error("Listen failed");
        }

        // Initialize thread manager
        IThreadsManager* threadsManager = new ThreadpoolManager(20);
        
        int clientSocket;
        struct sockaddr_in clientAddr;
        socklen_t clientAddrLen = sizeof(clientAddr);
        

        while (keepRunning) {
            
            if ((clientSocket = accept(serverSocket, (struct sockaddr *)&clientAddr, &clientAddrLen)) < 0) {
                perror("Accept failed");
                continue;
            }

            if (!keepRunning) {
                break;
            }
            threadsManager->manageClientThread(clientSocket);
        }
        shutdown(serverSocket, SHUT_RDWR);
        close(serverSocket);
        cout << "Server is shutting down" << endl;
        std::this_thread::sleep_for(std::chrono::milliseconds(500));

    }

    void ServerLogic::stopServer() {
    keepRunning = false;
    ServerLogic::fakeClientClosing();
    }
    
