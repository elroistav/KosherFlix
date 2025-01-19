//including relevant libraries
#include <iostream>
//#include <winsock2.h>
#include <stdio.h>
//#include <winsock2.h>
//#include <ws2tcpip.h>
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
//#include <netinet/in.h>
//#include <arpa/inet.h>
#include "IThreadsManager.h"
#include "MultiThreadsManager.h"
#include <iostream>
#include <sys/socket.h>
#include <fstream>
#include "server.h"
#include "ServerLogic.h"


int main(int argc, char* argv[]) {
    if (argc != 2) { // Check if the user provided exactly one argument (the port)
        cerr << "Usage: " << argv[0] << " <server_port>" << endl;
        return 1;
    }

    // Convert the provided argument (port) to an  integer
    int server_port = atoi(argv[1]);

    if (server_port <= 0) { // Validate the port
        cerr<< "Invalid port number Please provide a valid port." << endl;
        return 1;
    }

    // Call the server logic
    ServerLogic::startServer(server_port);
    
        return 0;
}

