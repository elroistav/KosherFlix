# Android Movie Details

## Introduction

The **Movie Details** screen provides comprehensive information about selected movies on the Android platform. This screen appears when users tap on any movie poster throughout the app. It features a **video preview**, detailed movie information, and personalized movie recommendations based on viewing history.

![Movie Popup](./Screenshots/android_movie_popup.png)

---

## Movie Details Features

### 1. **Watch Movie Button**
A prominent **Play** button appears at the top of the screen.

- Tapping the button starts the movie in the **Movie Player**.
- Age restrictions trigger appropriate warnings.
- Network issues display relevant error messages.

![Watch Movie](./Screenshots/android_watch_movie.png)

### 2. **Video Preview**
The preview section automatically plays a short clip or trailer:

- **Auto-play**: Begins when the screen loads
- **Muted by Default**: Tap to enable sound
- **Gesture Controls**: Pinch to expand/minimize

### 3. **Movie Information**
Below the preview, you'll find:

- **Title**: Movie name
- **Description**: Plot summary
- **Duration**: Total runtime
- **Director**: Director's name
- **Release Date**: Original release date
- **Language Options**: Audio and subtitle availability
- **Additional Details**: Cast, ratings, etc.

![Movie Info](./Screenshots/android_movie_info.png)

### 4. **Similar Movies**
At the bottom, a horizontally scrollable list shows recommended movies:

- **Personalized**: Based on viewing history
- **Touch Scrolling**: Swipe to see more
- **Quick Access**: Tap to view details

![Movie Recommendations](./Screenshots/android_movie_recommendations.png)

---

## Navigation

### Accessing Movie Details
1. **Find a movie** through:
   - Homepage browsing
   - Search results
   - Recently watched list
2. **Tap the movie poster** to open details
3. **View information** and preview content
4. **Tap Play** to start watching or select another recommended movie

---

## Error States

### Video Preview Issues
- Shows movie poster if preview fails to load
- Displays connectivity warning when offline
- Provides retry option for failed loads

![Movie No Rec](./Screenshots/android_movie_no_rec.png)

### Missing Information
- Placeholder text for unavailable details
- "No recommendations available" message when applicable
- "Content unavailable" notice for removed movies

---

## Features

### Gesture Controls
- **Swipe Down**: Close details screen
- **Double Tap**: Expand/minimize preview
- **Long Press**: Add to watchlist

### Share Options
- Share movie details via:
  - Messaging apps
  - Social media
  - Email

### Navigation
- **Back Button**: Returns to previous screen
---

## Tips
- **Rotate Device**: Landscape mode for better preview viewing
- **Check Network**: Ensure stable connection for smooth preview playback