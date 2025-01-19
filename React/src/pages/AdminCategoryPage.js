import React from "react";
import AdminCategoryForm from "../components/AdminCategoryForm";
import Navbar from "../components/NavBar"; // Assuming Navbar is reusable
import "../styles/AdminCategoryPage.css";

function AdminCategoryPage() {
  return (
    <div className="admin-category-page">
      <Navbar />
      <div className="admin-category-container">
        <h1 className="admin-page-title">Add a New Movie Category</h1>
        <AdminCategoryForm />
      </div>
    </div>
  );
}

export default AdminCategoryPage;
