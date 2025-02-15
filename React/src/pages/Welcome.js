import React from 'react';
import { useNavigate } from 'react-router-dom';
import WelcomeBar from '../components/WelcomeBar';
import '../styles/Welcome.css';

/**
 * Welcome component renders the welcome page of the application.
 * It includes a welcome message, some promotional text, and a register button.
 * 
 * @component
 * @example
 * return (
 *   <Welcome />
 * )
 * 
 * @returns {JSX.Element} The Welcome component.
 */
const Welcome = () => {
    const navigate = useNavigate();

    return (
        <div className="welcome-page">
            <WelcomeBar />
            
            <div className="welcome-content">
                <h1>Welcome to KOSHERFLIX</h1>
                <h2>Waste your time</h2>
                <h2>Give us your money</h2>
                <div className="register">
                    <p>Sounds good? let's start..</p>
                    <button onClick={() => navigate('/register')} className="btn btn-secondary">
                    Register
                    </button>
                </div>
            </div>
        </div>
    );
};

export default Welcome;
