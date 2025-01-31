import React, { useState } from "react";
import axios from "axios";
import "../styles/AdminCategoryPage.css";

/**
 * AdminCategoryForm component allows an admin to add a new category.
 * 
 * @component
 * @example
 * return (
 *   <AdminCategoryForm />
 * )
 * 
 * @returns {JSX.Element} The rendered form component for adding a new category.
 * 
 * @description
 * This component renders a form that allows an admin to add a new category by providing
 * a name, description, promoted status, and a list of movie IDs. The form data is managed
 * using the useState hook and submitted via an asynchronous POST request to the server.
 * 
 * @function
 * @name AdminCategoryForm
 * 
 * @property {string} BASE_URL - The base URL for the API, retrieved from environment variables.
 * @property {Object} formData - The state object containing form data.
 * @property {string} formData.name - The name of the category.
 * @property {string} formData.description - The description of the category.
 * @property {boolean} formData.promoted - The promoted status of the category.
 * @property {string} formData.movies - Comma-separated movie IDs.
 * 
 * @method
 * @name handleInputChange
 * @description Handles changes to text input fields and updates the formData state.
 * @param {Object} e - The event object.
 * 
 * @method
 * @name handleCheckboxChange
 * @description Handles changes to the checkbox input and updates the formData state.
 * @param {Object} e - The event object.
 * 
 * @method
 * @name handleSubmit
 * @description Handles form submission, sends a POST request to the server, and resets the form.
 * @param {Object} e - The event object.
 */
function AdminCategoryForm() {
  const BASE_URL = process.env.REACT_APP_BASE_URL;
  const [formData, setFormData] = useState({
    name: "",
    description: "",
    promoted: false,
    movies: "",
  });

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormData({ ...formData, [name]: value });
  };

  const handleCheckboxChange = (e) => {
    setFormData({ ...formData, promoted: e.target.checked });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      const response = await axios.post(BASE_URL + "/api/categories", {
        ...formData,
        movies: formData.movies.split(",").map((id) => id.trim()), // Convert comma-separated movie IDs into an array
      });
      alert("Category added successfully!");
      setFormData({ name: "", description: "", promoted: false, movies: "" });
    } catch (error) {
      console.error("Error adding category:", error);
      alert("Failed to add category.");
    }
  };

  return (
    <form className="admin-category-form" onSubmit={handleSubmit}>
      <div className="form-group">
        <label>Category Name</label>
        <input
          type="text"
          name="name"
          value={formData.name}
          onChange={handleInputChange}
          placeholder="Enter category name"
          required
        />
      </div>
      <div className="form-group">
        <label>Description</label>
        <textarea
          name="description"
          value={formData.description}
          onChange={handleInputChange}
          placeholder="Enter a short description"
        />
      </div>
      <div className="form-group checkbox-group">
        <label>
          <input
            type="checkbox"
            name="promoted"
            checked={formData.promoted}
            onChange={handleCheckboxChange}
          />
          Promoted
        </label>
      </div>
      <div className="form-group">
        <label>Movies (Comma-Separated IDs)</label>
        <input
          type="text"
          name="movies"
          value={formData.movies}
          onChange={handleInputChange}
          placeholder="Enter movie IDs separated by commas"
        />
      </div>
      <button type="submit" className="submit-button">
        Add Category
      </button>
    </form>
  );
}

export default AdminCategoryForm;
