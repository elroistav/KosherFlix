# Installation Guide (Android Application)

## Introduction

This guide will walk you through setting up and running the **Netflix lookalike Android application**. The application requires a **backend (Node.js with MongoDB)** to be running before launching the Android app.

---

## Prerequisites

Before starting, ensure you have the following installed on your system:

- **Node.js (v16 or higher)** – [Download here](https://nodejs.org/)
- **npm (Node Package Manager)** – Comes with Node.js
- **MongoDB** – [Download here](https://www.mongodb.com/try/download/community)
- **Docker & Docker Compose** – [Install here](https://docs.docker.com/get-docker/)
- **Android Studio** – [Download here](https://developer.android.com/studio)

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
Inside the `NetflixProj3` folder, create a file named `.env.docker` with the following content:
```sh
CONNECTION_STRING=mongodb://mongo:27017/ProjDB
PORT=4000
```

### **Start the Backend using Docker**
```sh
docker-compose up --build
```

Ensure that the backend is fully running before proceeding to the Android setup.

---

## 3. Setting Up the Android Application

### **Navigate to the android folder**
```sh
cd Android
```

### **Open the Android Project in Android Studio**
1. Open **Android Studio**.
2. Click **File > Open**.
3. Navigate to the **NetflixProj4/Android** folder and open the project.

### **Configure the API Connection**
The Android application needs to connect to the backend server. You must update the backend URL in two places:

#### **Step 1: Create the `config.properties` File**
1. Inside the `Android/app/src/main/assets/` folder, create a new folder called `utils` (if it doesn't exist).
2. Inside `utils`, create a file named `config.properties`.
3. Add the following content to the file, replacing `<your-computer-ip>` with the correct IP address:

```properties
backend_url=http://<your-computer-ip>:4000
```

#### **Step 2: Update `network_security_config.xml`**
1. Navigate to `Android/app/src/main/res/xml/network_security_config.xml`.
2. Update the IP address to match the one used in `config.properties`.

#### **How to Find Your Computer's IP Address**
If you're running the backend on your local machine, follow these steps to find your IP address:
- **Windows:** Run `ipconfig` in the command prompt and look for `IPv4 Address`.
- **Mac/Linux:** Run `ifconfig` or `ip a` in the terminal and find the local IP.

### **Ensure Your Phone is on the Same Network**
Your Android device must be connected to the same Wi-Fi network as your computer to access the backend.

### **Build and Run the Android Application**
1. In **Android Studio**, click **Run > Run 'app'**.
2. Select your emulator or connected device.
3. The application should launch and connect to the backend server.

---

## 4. Running the Application

Once everything is set up:
1. Ensure the backend is running (`docker-compose up --build`).
2. Run the Android application in **Android Studio**.
3. Log in or register to start using the application.
4. Enjoy streaming movies!

---

## 5. Troubleshooting & Additional Notes

- **Backend Not Connecting?** Ensure Docker is running and your backend is accessible via the configured IP.
- **App Not Loading Data?** Check the `config.properties` and `network_security_config.xml` files for the correct IP address.
- **Device Cannot Reach Backend?** Make sure your phone is on the same Wi-Fi network as your computer.
- **Changes Not Reflecting?** Restart both the backend and Android app to apply updates.

