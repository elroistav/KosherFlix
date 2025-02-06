# Android Homepage

## Introduction

The **Android Homepage** serves as the main hub for users to discover and browse movies, access personalized features, and manage their viewing experience. The interface is optimized for mobile devices, featuring smooth scrolling and intuitive touch controls. For admin users, the profile section provides access to the admin interface.

## Key Features

- **Search Bar**: Quickly find movies using the search icon in the top bar
- **Theme Toggle**: Switch between light and dark themes in settings
- **Featured Movie**: Prominently displayed movie that changes periodically
- **Movie Categories**: Horizontal scrolling rows of movie categories
- **Recently Watched**: Quick access to previously viewed content
- **Profile Access**: Access to user profile and admin features (for admins)

## Homepage Layout

### 1. **Featured Movie**

The featured movie section occupies the top portion of the screen, displaying:
- **Movie Poster/Preview**: Large format display of the movie
- **Movie Title**: Prominently displayed
- **Quick Actions**: Play and Info buttons
- **Brief Description**: Short summary of the movie

### Example
![Random Movie](./Screenshots/android_random_movie.png)

### 2. **Movie Categories**

Below the featured movie, horizontally scrollable rows display different categories:
- **Action**
- **Comedy**
- **Drama**
- **Romance**
- **Documentary**

Each category shows movie posters that can be tapped to view details.

### Example
![Category Rows](./Screenshots/android_category_rows.png)

### 3. **Search Feature**

The search icon in the top bar opens the search interface:
- **Search Bar**: Enter movie titles or categories
- **Real-time Results**: Results update as you type
- **Recent Searches**: Quick access to previous searches

### Example
![Search Bar](./Screenshots/android_searchbar.png)

### 4. **Theme Settings**

Access theme settings through the profile menu:
- **Dark Theme**: Optimized for low-light environments
- **Light Theme**: Bright interface for daytime use
- **System Default**: Follows system theme settings

### Example
![Dark Mode](./Screenshots/android_dark_mode.png)
![Light Mode](./Screenshots/android_light_mode.png)

### 5. **Recently Watched**

The recently watched section appears as a horizontal row showing:
- **Previously watched movies**
- **Progress indicators** for partially watched content
- **Resume playback** option

### Example
![Last Watched](./Screenshots/android_last_watched.png)

### 6. **Profile Section**

The profile icon in the bottom navigation provides access to:
- **User Profile**: Personal information and settings
- **Admin Interface**: For users with admin privileges
- **Account Settings**: Manage account preferences

### Example
![Profile](./Screenshots/android_profile.png)

### 7. **Category Filter**

Access the category filter through the top bar:
- **All Categories**: View complete category list
- **Quick Filters**: Commonly used categories
- **Recently Used**: Recently selected categories

### Example
![Category Selector](./Screenshots/android_category_selector.png)

## Navigation

The app uses a bottom navigation bar with:
- **Home**: Return to main screen
- **Search**: Open search interface
- **Categories**: Browse all categories
- **Profile**: Access user settings

### Navigation Examples
![Notflicks Logo](./Screenshots/android_notflicks_logo.png)

## Error Handling

- **Connection Issues**: Offline mode with cached content
- **Loading Errors**: Retry options with error messages
- **Search Failures**: Clear suggestions for refining search
