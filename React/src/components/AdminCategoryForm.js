import React, { useState } from "react";
import axios from "axios";
import "../styles/AdminCategoryPage.css";

function AdminCategoryForm() {
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
      const response = await axios.post("http://localhost:4000/api/categories", {
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
