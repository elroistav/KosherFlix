# Installation Guide (Web Application)

## Introduction

This guide will walk you through setting up the **Netflix lookalike web application**. The application consists of a **backend (Node.js with MongoDB)** and a **frontend (React.js)**.

Both services need to be set up separately, and you must install dependencies for both the **backend and frontend**.

---

## Prerequisites

Before starting, ensure you have the following installed on your system:

- **Node.js (v16 or higher)** – [Download here](https://nodejs.org/)
- **npm (Node Package Manager)** – Comes with Node.js
- **MongoDB (if running locally)** – [Download here](https://www.mongodb.com/try/download/community)
- **Docker & Docker Compose** (if using Docker) – [Install here](https://docs.docker.com/get-docker/)

---

## 1. Clone the Repository

```sh
git clone https://github.com/Yedidya-Darshan-code/Netflix-proj-4.git
```

## 2. Setting Up the Backend (Node.js)

### **Navigate to the backend folder**
```sh
cd NetflixProj3
```

### **Install Dependencies**
```sh
npm install
```

### **Create the Backend Configuration File (.env.docker)**
### Inside the NetflixProj3 folder, create a file named .env.docker. Here is an example:
```sh
CONNECTION_STRING=mongodb://mongo:27017/ProjDB
PORT=4000
```

## 3. Setting Up the Frontend (React.js)
### Navigate to the frontend folder
```sh
cd React
```

### Install Dependencies
```sh
npm install
```

### **Create the Frontend Configuration File (.env)**
### In the React folder create a config file called .env. It should look somthing like this:
```sh
REACT_APP_BASE_URL=http://localhost:4000
```

### Start the frontend docker
```sh
docker-compose up --build
```
Note: By default, the frontend will start on http://localhost:3000/


## 4. Running the Application

Once both the backend and frontend are running:

    1. Open your browser and go to:
    http://localhost:3000/

    2. Register or log in to start using the application.

    3. Enjoy streaming movies!

## 5. Troubleshooting & Additional Notes

    - If using Docker, ensure Docker is running before starting the project.
    - If MongoDB is installed locally, ensure the MongoDB service is running.
    - If you encounter any errors, check the console output for missing dependencies or incorrect configuration.
    - If the backend fails to connect to the database, ensure that MongoDB is running and that the CONNECTION_STRING is correct.
    - Restart both services if any changes are made to the environment files (.env or .env.docker).


