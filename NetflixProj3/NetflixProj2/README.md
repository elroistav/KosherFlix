# Project 1 - Yoni, Elroi, and Yedidya

## Table of Contents
1. [Welcome](#welcome)
2. [Instructions for Running the App](#instructions-for-running-the-app)
   - [Clone the Repository](#clone-the-repository)
   - [Build the Docker Image](#build-the-docker-image)
   - [Running the Tests](#3a-to-run-the-tests)
   - [Running the Client/Server Without Tests](#3b-running-clientserver-without-tests)
3. [About the App](#about-the-app)
   - [Command Descriptions](#command-descriptions)
     - [POST](#command-1---post)
     - [PATCH](#command-2---patch)
     - [GET](#command-3---get)
     - [DELETE](#command-4---delete)
     - [Help](#command-5---help)
   - [Example Run of the App](#example-run-of-the-app)
4. [Personal Notes](#personal-notes)
   - [Project Improvements](#project-improvements)
   - [What We Changed](#what-we-changed)
   - [Outcome](#outcome)


## Welcome to out project. You can find here all the info that you need to run the tests or app using the docker including storing the data locally.

## Instructions for running the app:

1. Clone the repository:
   ```bash
   git clone https://github.com/Yedidya-Darshan-code/Netflix-proj2.git
   ```
2. Build the docker image:
   ```bash
   docker-compose build
   ```

### 3a. Running the tests:
4. Run the tests
   ```bash
   docker-compose up test
   ```

### 3b. Running the client/server without tests:
4. In one terminal run start the client and sevrer detached (it will also initialize the server)
   ```bash
   docker-compose up -d client
   ```
5. Start the server:
   Note: Replace <port-number> with the correct port number
   ```bash
   docker exec -it netflix-proj2-server-1 ./build/runApp <port-number>
   ```
7. Now you can run this command in any number of other terminals to create any number of clients
   Note: Replace <port-number> with the correct port number
   ```bash
   docker exec -it netflix-proj2-client-1 python3 /usr/src/myapp/Client.py --ip server --port <port-number>
   ```
    

## About the app:

### This is a terminal based app with multiple built in commands:

### Command 1 - POST:

#### Usage: POST [userid] [movieid1] [movieid2] ...

#### Description: If the user doesn't exist add the user and the movies to the list of movies they watched. if the user already exists, an error will be returned.

### Command 2 - PATCH:

#### Usage: PATCH [userid] [movieid1] [movieid2] ...

#### Description: If the user exists add the given movies to the list of movies that they watched. If they dont exist, an error will be returned to the user indicating they need to register first.

### Command 3 - GET:

#### Usage: GET [userid] [movieid]

#### Description: Recommends the given user up to 10 movies that are similar to the movie inputted based on what other users with similar taste have watched.

### Command 4 - DELETE

#### Usage: DELETE [userid] [movieid1] [movieid2]

#### Description: Removes the specified movie from the system. Returns an error if the user or movie don't exist.

### Command 5 - Help:

#### Usage: help

#### Description: Prints out a list of all the available command and their syntax

### Example Run of the App:

```bash
   hi
   400 Bad Request
   POST 1 2 3 4 5
   201 Created
   POST 2 3 4 5 6
   201 Created
   POST 3 4 5 6 7
   201 Created
   PATCH 1 10 11 12
   204 No Content
   PATCH 2 20 23
   204 No Content
   DELETE 2 23 6
   204 No Content
   POST 1 3
   404 Not Found
   GET 3 4
   200 Ok
   
   3 2 10 11 12 20
   help
   200 Ok
   
   DELETE, arguments: [userid] [movieid1] [movieid2] ...
   GET, arguments: [userid] [movieid]
   PATCH, arguments: [userid] [movieid1] [movieid2] ...
   POST, arguments: [userid] [movieid1] [movieid2] ...
   help
```

## Personal notes:

#### Project Improvements: Loose Coupling and SOLID Principles We realized there were some issues in our code with tight coupling and not fully following SOLID principles. Here’s what we noticed: 
#### Interfaces: We should have used more interface-style structures in C++ to make the code more flexible.
#### Class Separation: Some classes were doing way too much, so they needed to be split into smaller, more focused classes.
### Strong elements we had:
#### - The fact that the names of the commands changed did not require from us to change our base code rather only change one specific class just as we had planned for.
#### - Adding comamnds did not equire from us to change our base code rather only add a new class and update a utils class because we used the command design pattern.
#### - The fact that the input/output now comes from and to sockets did not rquire any change of our code rather just rquired us to create a new class for the socket handling also due mostly to the use of the command design pattern

### What We Changed:
#### Took note of the importance of using interface-style structures to reduce dependencies between different components, which will now hopefully help us in future designs.
#### Broke down large classes into smaller ones, making everything cleaner and easier to manage.
#### Improved the structure to get ready for things like servers, clients, and multi-threading.
#### Changed the execute function in the command so it now returns a string instead of being void and just printing to the command line. This could have been better if we’d originally thought more about loose coupling, but it’s fixed now.
#### Outcome:
#### With these changes, the code is now way easier to work with and extend, especially as we start moving into more complex stuff like sockets and multi-threading. We’ve learned from our mistakes and made the system a lot more modular and flexible.
