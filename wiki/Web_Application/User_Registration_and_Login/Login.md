# User Login

## Introduction

The **Login** process allows registered users to access their accounts on the **Web** applications. By entering their credentials (email and password), users gain access to their personalized movie experiences, including browsing movies, watching content, and managing their profiles.

The login system is designed with security in mind, featuring token-based authentication to ensure the safety of user data.

### Welcome Page
![Welcome Page](./Screenshots/web_welcome.png)

## Key Features
- **Login with Email and Password**: Users can log in using the credentials they provided during registration.
- **Token-based Authentication**: Once logged in, the user receives a token (JWT) for secure access to the app.

## Login Flow

1. **User Input**: The user enters their registered email address and password.
2. **Backend Validation**: The backend checks the provided credentials.
   - If valid, a token is issued for the session.
   - If invalid, an error message is displayed.
3. **Token Storage**: After successful login, the user’s session is maintained with a JWT, which is stored securely.
4. **Access Granted**: Once logged in, the user can navigate to the homepage and access other personalized features.

### Step-by-Step Instructions

1. Navigate to the **Login Page** from the homepage or registration page.
2. Enter your registered **userName** and **Password**.
3. Click the **Login** button to submit your credentials.
4. If the credentials are valid, you will be redirected to the homepage with personalized content.
5. If the login fails, an error message will appear indicating incorrect credentials.

### Screenshot
![Login Page](./Screenshots/web_login_form.png)

## Tips
- Ensure that you are using the correct userName and password combination.
- Keep your login credentials safe and do not share them with others.

For any problems with registration please contact our help office at YedYoniElroi@barilan.com or call 1800-KOSHER1