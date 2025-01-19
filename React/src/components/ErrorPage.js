import React, { useEffect } from 'react';
import { Link } from 'react-router-dom';
import '../styles/ErrorPage.css';

function ErrorPage() {
  useEffect(() => {
    // Add the no-scroll class to the body element
    document.body.classList.add('no-scroll');

    // Remove the no-scroll class when the component is unmounted
    return () => {
      document.body.classList.remove('no-scroll');
    };
  }, []);

  return (
    <div className="error-page">
      <div className="error-content">
        <h1>404</h1>
        <p>Sorry, the page you are looking for does not exist.</p>
        <Link to="/" className="home-link">Go back to Home</Link>
      </div>
      <div className="background-overlay"></div>
    </div>
  );
}

export default ErrorPage;