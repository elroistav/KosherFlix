# Admin Homepage

## Introduction

The **Admin Homepage** is an exclusive page that allows administrators to manage the movie catalog and categories. Admin users have the ability to **create, update, and remove** movies and categories directly from this interface. This page is not visible to regular users and can only be accessed when the **isAdmin** field is manually set to `true` in the database.

The Admin Homepage retains the same **navbar** as the regular homepage, ensuring easy navigation back to the main site.

### Main page
![Admin Homescreen](./Screenshots/web_admin_homescreen.png)

## How to Access the Admin Homepage

### Step 1: Grant Admin Privileges
To access the **Admin Homepage**, an admin account must be configured manually:
1. Navigate to your **database**.
2. Locate the user's record in the **users** collection.
3. Find the field named `isAdmin`.
4. Change its value from `false` to `true`.
5. Save the changes.

### Step 2: Logging In as an Admin
1. Log in using the credentials of the modified admin account.
2. Click on the **Profile Picture** in the top-right corner.
3. You will now see an additional option in the dropdown labeled **Admin**.
4. Click **Admin** to navigate to the **Admin Homepage**.

### Example
![Enter Admin](./Screenshots/web_enter_admin.png)

---

## Admin Homepage Features
Note: When editing and adding make sure you fill in all required feilds and correctly otherwise you will get an error. For example, if you edit a movie but leave the title empty you will see:
![Info Error](./Screenshots/web_info_error.png)

### 1. **Movie Management**
Admins can **add, update, or delete** movies from the platform.

- **Add Movie**  
  - Click the **"Add Movie"** button.
  - Fill in details such as **title, description, release date, categories, director, and cast**.
  - Upload a **thumbnail** for the movie.
  - Upload a **video** for the movie
  - Click **Save** to add the movie to the database.

  ![Add Movie](./Screenshots/web_add_movie1.png)
  ![Add Movie](./Screenshots/web_add_movie2.png)

- **Edit Movie**  
  - Navigate to a movie you want to edit.
  - Click the edit button in the bottom right of the movie
  - Modify details such as title, description, or category.
  - Click **Update** to save changes.

  ![Edit Movie](./Screenshots/web_edit_movie1.png)
  ![Edit Movie](./Screenshots/web_edit_movie2.png)

- **Delete Movie**  
  - Navigate to a movie you want to remove.
  - Click the remove button in the bottom left of the movie
  - Click to confirm you want to delete the movie

  ![Delete Movie](./Screenshots/web_delete_movie.png)

### 2. **Category Management**
Admins can create and organize movie categories.

- **Add Category**  
  - Click the **"Add Category"** button.
  - Enter a **category name and description** (e.g., Action, Comedy, Horror).
  - Select whether the category should be promoted or not
  - Click **Save** to add the category.

  ![Add Category](./Screenshots/web_add_category1.png)
  ![Add Category](./Screenshots/web_add_category2.png)

- **Edit Category**  
  - Navigate to a category you want to edit.
  - Click the edit button to the right of the category
  - Modify details such as name or description.
  - Click **Update** to save changes.

  ![Edit Category](./Screenshots/web_edit_category1.png)
  ![Edit Category](./Screenshots/web_edit_category2.png)

- **Delete Category**  
  - Navigate to a category you want to remove.
  - Click the remove button to the left of the category
  - Click to confirm you want to delete the category

  ![Delete Category](./Screenshots/web_delete_category.png)

### 3. **Navigation & UI**
- **Navbar**: The Admin Homepage retains the same **navbar** as the regular homepage.
- **Return to Main Homepage**: Click the **Notflicks logo** in the navbar to return to the homepage.

![Notflicks Logo](./Screenshots/web_kosherflix_logo.png)

### 4. **Whats's stopping me from just going straight to the admin page?**

I'm glad you asked. When trying to navigate to /admin page without actually being an admin
<br>
![Browser Url](./Screenshots/web_url.png)
<br>
You will be redirrected to the login page as punnishment and this likely will be reported:
![Tricky](./Screenshots/web_tricky_user.png)

---

## Error Handling
- **Admin Page Not Visible**  
  - Ensure the `isAdmin` field in the database is set to `true`.
  - Log out and log back in after making the change.
  
- **Failed to Add/Update/Delete Movie**  
  - Check if all required fields are filled.
  - Ensure the movie title is unique.
  - Verify the server connection.

- **Category Deletion Warning**  
  - If a category is deleted, all movies linked to it may become unclassified.

---

## Tips
- Only **trusted users** should be given admin access, as they have the ability to modify all movies and categories.
- Regularly **backup** the database to prevent accidental deletions.
- Use meaningful category names to help users find movies easily.

