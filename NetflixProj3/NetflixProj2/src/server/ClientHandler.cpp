#include "ClientHandler.h"
#include "InputValidator.h"
#include <unistd.h>
#include <iostream>
#include <cstring>


#define BUFFER_SIZE 2048

using namespace std;

void ClientHandler::handleClient(int clientSocket) {
    char buffer[BUFFER_SIZE] = {0};

    while (true) {
        int bytesRead = read(clientSocket, buffer, BUFFER_SIZE);
        if (bytesRead <= 0) {
            std::cout << "Client " << clientSocket << " disconnected.\n";
            close(clientSocket);
            break;
        }

        // Trim leading whitespace
        std::string input(buffer, bytesRead);
        size_t firstNonSpace = input.find_first_not_of(" \t\n\r");
        if (firstNonSpace == std::string::npos || input[firstNonSpace] == '\0') {
            // If all input is whitespace or empty, return 400
            write(clientSocket, "400 Not Found\n", 14);
            continue;
        }

        // Handle valid input
        mtx.lock();
        std::string clientMessage = InputValidator::checkInputValidity(input.c_str());
        mtx.unlock();

        // Echo the message back to the client
        write(clientSocket, clientMessage.c_str(), clientMessage.length());

        // Clear the buffer for the next message
        memset(buffer, 0, BUFFER_SIZE);
    }
}