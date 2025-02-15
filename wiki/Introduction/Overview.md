# Project Overview

## Introduction

Welcome to **KOSHERFLIX** – a web and Android application inspired by Netflix, designed specifically for religious Jews. This application allows users to browse and stream movies, while administrators can manage movie content. The project is designed to provide an intuitive and seamless experience for both users and administrators across multiple platforms. 

Both the **web** and **Android** applications are connected to the same backend, ensuring consistency in functionality and data between both versions.

## Key Features

### User Features
- **Registration & Login**: Users can register for a new account and log in to access personalized content.
- **Homepage**: The homepage displays movie categories, promoted content, and the user’s recently watched movies.
- **Movie Search**: A search feature allows users to find movies by title, genre, or actor.
- **Movie Details**: Clicking on a movie will take users to a detailed page with information like title, description, rating, and cast.
- **Movie Player**: Watch movies directly in the app with a custom player interface.

### Admin Features
- **Admin Dashboard**: A comprehensive interface where administrators can view and manage movies, categories, and other system settings.
- **Movie Management**: Admins can create new movie entries, update existing ones, and delete movies from the database.
- **User Management**: Admins can manage user accounts and permissions.

### Shared Backend
- Both the web and Android applications interact with a **single backend API**, providing consistent data handling and synchronization between both platforms.
- **Authentication**: Token-based authentication (JWT) ensures secure login and data access.
- **Database**: A centralized database stores information on users, movies, categories, and other relevant data.

## Technology Stack

### Frontend
- **Web Application**: Built using modern web technologies (HTML, CSS, JavaScript) and a responsive design to ensure compatibility with different devices.
- **Android Application**: 
    - Developed using Java and the Android SDK, following standard Android app development practices.
    - **Room** Database is used for local data storage, ensuring a smoother and faster experience by caching movie data locally. This reduces the need for repeated API calls, improving performance and usability.

### Backend
- **Node.js/Express**: The backend server is built with Node.js and the Express framework, providing a RESTful API for communication with the frontend.
- **MongoDB**: The database used to store user data, movie details, and other application-related information.
- **JWT (JSON Web Tokens)**: Used for authenticating users and managing sessions securely.

## Folder Structure

The project is organized into two main components: the **Web Application** and the **Android Application**. Each component has a dedicated folder for related assets, documentation, and code. Additionally, the **Backend** is shared across both applications to maintain consistent data and operations.

### Folder Breakdown:
- **Web_Application**: Contains all web app-specific assets, including pages like the homepage, user registration, login, movie details, search, and admin functionalities.
- **Android_Application**: Contains all Android app-specific files, including the Android version of the homepage, movie player, user login, and admin features.

### Explanation:
- **Introduction**: Provides an overview of the project and its core features, emphasizing the dual-platform approach (web and Android).
- **Key Features**: Clearly outlines the features available for both users and admins, highlighting the cross-platform consistency.
- **Technology Stack**: Details the tech used in both the frontend and backend.
- **Folder Structure**: Offers an overview of how the project is organized, making it easy for developers to navigate.
- **Installation & Setup**: Provides easy-to-follow setup instructions for getting the project up and running on both web and Android platforms.
- **Contributing**: Encourages open-source contributions.
- **License**: Mentions the project's open-source nature and licensing.



For any problems with registration please contact our help office at YedYoniElroi@barilan.com or call 1800-KOSHER1