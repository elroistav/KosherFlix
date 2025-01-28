import React from 'react';
import { useNavigate } from 'react-router-dom';
import WelcomeBar from '../components/WelcomeBar'; // ייבוא ה-navbar
import '../styles/Welcome.css';

const Welcome = () => {
    const navigate = useNavigate(); // מאפשר ניווט בין מסכים

    return (
        <div className="welcome-page">
            <WelcomeBar />
            
            <div className="welcome-content">
                <h1>Welcome to NETFLICK</h1>
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
