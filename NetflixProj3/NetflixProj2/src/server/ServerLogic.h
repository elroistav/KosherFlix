//
// Created by Yoni Trachtenberg on 12/16/24.
//

#ifndef NETFLIX_PROJ2_SERVERLOGIC_H
#define NETFLIX_PROJ2_SERVERLOGIC_H


class ServerLogic {
public:
    static void checkFileAccess();
    static int createServerSocket();
    static struct sockaddr_in createServerSockaddrIn(int server_port);
    static void startServer(int server_port);
    void acceptAndHandleClients(int serverSocket, IThreadsManager* threadsManager);
    static void stopServer();
    static void fakeClientClosing();
private:
    static std::atomic<bool> keepRunning;
};


#endif //NETFLIX_PROJ2_SERVERLOGIC_H
