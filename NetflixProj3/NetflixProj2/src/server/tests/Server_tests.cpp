#include <gtest/gtest.h>
#include <vector>
#include <cstring>
#include <unistd.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <sstream>
#include <iostream>
#include <fstream>
#include <filesystem>
#include "../server.h"
#include "../data_access/FileDataAccess.hpp"
#include "../ServerLogic.h"
#include <thread>
#include <chrono>
using namespace std;
namespace fs = std::filesystem;

TEST (server, serverCreateServerSocket) {
    server s;
    int serverSocket = ServerLogic::createServerSocket();
    ASSERT_TRUE(serverSocket > 0) << "Failed to create server socket";
}

TEST (server, serverCreateServerSockaddrIn) {
    server s;
    struct sockaddr_in sin = ServerLogic::createServerSockaddrIn(5555);
    ASSERT_TRUE(sin.sin_family == AF_INET) << "Failed to create server sockaddr_in";
    ASSERT_TRUE(sin.sin_addr.s_addr == INADDR_ANY) << "Failed to create server sockaddr_in";
    ASSERT_TRUE(sin.sin_port == htons(5555)) << "Failed to create server sockaddr_in";
}



// fake client func
void fakeClient(const std::string& serverIp, int serverPort, int clientId, std::string& response) {
    // Create client socket
    int clientSocket = socket(AF_INET, SOCK_STREAM, 0);
    ASSERT_NE(clientSocket, -1) << "Client " << clientId << ": Error creating socket";

    // Connect to server
    struct sockaddr_in serverAddr;
    memset(&serverAddr, 0, sizeof(serverAddr));
    serverAddr.sin_family = AF_INET;
    serverAddr.sin_port = htons(serverPort);
    inet_pton(AF_INET, serverIp.c_str(), &serverAddr.sin_addr);

    // Connect to server check
    ASSERT_EQ(connect(clientSocket, (struct sockaddr*)&serverAddr, sizeof(serverAddr)), 0) 
        << "Client " << clientId << ": Error connecting to server";

    // Send message to server
    std::string message = "POST " + std::to_string(clientId) + " 1 2 3\n";
    send(clientSocket, message.c_str(), message.size(), 0);

    // Receive response from server
    char buffer[1024] = {0};
    int bytesReceived = recv(clientSocket, buffer, sizeof(buffer), 0);
    ASSERT_GT(bytesReceived, 0) << "Client " << clientId << ": No response from server";

    // Save response
    response = std::string(buffer, bytesReceived);
    close(clientSocket);
}

// fake complicated client func
void fakeComplicatedClient(const std::string& serverIp, int serverPort, int clientId, std::string& response) {
    // Create client socket
    int clientSocket = socket(AF_INET, SOCK_STREAM, 0);
    ASSERT_NE(clientSocket, -1) << "Client " << clientId << ": Error creating socket";

    // Connect to server
    struct sockaddr_in serverAddr;
    memset(&serverAddr, 0, sizeof(serverAddr));
    serverAddr.sin_family = AF_INET;
    serverAddr.sin_port = htons(serverPort);
    inet_pton(AF_INET, serverIp.c_str(), &serverAddr.sin_addr);

    // Connect to server check
    ASSERT_EQ(connect(clientSocket, (struct sockaddr*)&serverAddr, sizeof(serverAddr)), 0) 
        << "Client " << clientId << ": Error connecting to server";

    // Send message to server
    //cout << "client id: " << clientId << " sent POST" << endl;
    std::string message = "POST " + std::to_string(clientId) + " 1 2 3\n";
    send(clientSocket, message.c_str(), message.size(), 0);

    // Receive response from server
    char buffer[1024] = {0};
    int bytesReceived = recv(clientSocket, buffer, sizeof(buffer), 0);
    ASSERT_GT(bytesReceived, 0) << "Client " << clientId << ": No response from server";

    // Save response
    response = std::string(buffer, bytesReceived);
    //cout << "client id: " << clientId << " received: " << response << endl;

    // Send another message to server
    message = "GET " + std::to_string(clientId) + " 2\n";
    send(clientSocket, message.c_str(), message.size(), 0);

    // Receive response from server
    bytesReceived = recv(clientSocket, buffer, sizeof(buffer), 0);
    ASSERT_GT(bytesReceived, 0) << "Client " << clientId << ": No response from server";

    // Save response
    response = std::string(buffer, bytesReceived);
    //cout << "client id: " << clientId << " received: " << response << endl;


    //send another message to server
    message = "PATCH " + std::to_string(clientId+1) + " 2 4\n";
    send(clientSocket, message.c_str(), message.size(), 0);

    // Receive response from server
    bytesReceived = recv(clientSocket, buffer, sizeof(buffer), 0);
    ASSERT_GT(bytesReceived, 0) << "Client " << clientId << ": No response from server";

    // Save response
    response = std::string(buffer, bytesReceived);
    //cout << "client id: " << clientId << " received: " << response << endl;


    close(clientSocket);
}

//  basic test server connection
TEST(ServerTest, SingleClientConnection) {

    // Create server (different thread)
    std::thread serverThread([]() {
        try {
            ServerLogic::startServer(5555);
        } catch (const std::exception& ex) {
            std::cerr << "Server encountered an error: " << ex.what() << std::endl;
        }
    });

    // Wait for server to start
    std::this_thread::sleep_for(std::chrono::seconds(1));

    // Create client
    const std::string serverIp = "127.0.0.1";
    const int serverPort = 5555;

    std::string response;
    fakeClient(serverIp, serverPort, 1, response);

    EXPECT_NE(response.find("201 Created"), std::string::npos) << "Expected '201 Created' in server response";

    // Check if the file was created
    ifstream file("data/Users/user_" + std::to_string(1) + ".txt");

    EXPECT_TRUE(file.is_open()) << "Failed to open file user_1.txt";



    string out;
    string line;

    while (getline(file, line)) {
        out.append(line);
        out.append("\n");
    }

    EXPECT_EQ(out, "1\n2\n3\n") << "Expected: 1\n2\n3\n, got: " << out;
    file.close();
    fs::remove("data/Users/user_" + std::to_string(1) + ".txt");

    ServerLogic::stopServer();
    serverThread.join();
    
}

TEST(ServerTest, MultipleClientConnections) {

    // Create server (different thread)
    std::thread serverThread([]() {
        try {
            ServerLogic::startServer(5555);
        } catch (const std::exception& ex) {
            std::cerr << "Server encountered an error: " << ex.what() << std::endl;
        }
    });

    // Wait for server to start
    std::this_thread::sleep_for(std::chrono::seconds(1));

    const std::string serverIp = "127.0.0.1";
    const int serverPort = 5555;
    const int numberOfClients = 5;

    std::vector<std::thread> clientThreads;
    std::vector<std::string> responses(numberOfClients);

    for (int i = 0; i < numberOfClients; ++i) {
        clientThreads.emplace_back(fakeClient, serverIp, serverPort, i + 1, std::ref(responses[i]));
    }

    for (auto& thread : clientThreads) {
        thread.join();
    }


    for (const auto& response : responses) {
        //cout << response << endl;
    }

    for (const auto& response : responses) {
        EXPECT_NE(response.find("201 Created"), std::string::npos) << "Expected 'Hello' in server response";
    }

    ServerLogic::stopServer();
    serverThread.join();
}

TEST(ServerTest, MultipleComplicatedClientConnections) {

    // Create server (different thread)
    std::thread serverThread([]() {
        try {
            ServerLogic::startServer(5555);
        } catch (const std::exception& ex) {
            std::cerr << "Server encountered an error: " << ex.what() << std::endl;
        }
    });

    // Wait for server to start
    std::this_thread::sleep_for(std::chrono::seconds(1));


    const std::string serverIp = "127.0.0.1";
    const int serverPort = 5555;
    const int numberOfClients = 5;

    std::vector<std::thread> clientThreads;
    std::vector<std::string> responses(numberOfClients);

    for (int i = 0; i < numberOfClients; ++i) {
        clientThreads.emplace_back(fakeComplicatedClient, serverIp, serverPort, i + 1, std::ref(responses[i]));
    }


    for (auto& thread : clientThreads) {
        thread.join();
    }

    for (int i = 1; i <= numberOfClients; ++i) {
        EXPECT_TRUE(fs::exists("data/Users/user_" + std::to_string(i) + ".txt")) << "Failed to create file user_" + std::to_string(i) + ".txt";
    }


    ServerLogic::stopServer();
    serverThread.join();
}

