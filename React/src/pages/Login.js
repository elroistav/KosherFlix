import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';
import RegisterLoginBar from '../components/RegisterLoginBar'; // ייבוא ה-navbar
import '../styles/RegisterLogin.css';
import AuthForm from '../components/AutoForm';

const Welcome = () => {
    const BASE_URL = process.env.REACT_APP_BASE_URL;
    console.log('BASE_URL:', BASE_URL);
    const navigate = useNavigate(); 
    const [userName, setUserName] = useState(''); 
    const [name, setName] = useState(''); 
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [profilePicture, setProfilePicture] = useState(null);
    const [error, setError] = useState('');

    const inputs = [
        { type: 'text', value: userName, onChange: (e) => setUserName(e.target.value), placeholder: 'UserName' },
        { type: 'password', value: password, onChange: (e) => setPassword(e.target.value), placeholder: 'Password' },

    ];

    const handleSubmit = async (event) => {
        event.preventDefault();

        if (!userName || !password) {
            alert('All fields are required, including profile picture!');
            setError('All fields are required');
            return;
        }


        const dataToSend = {
            userName: userName,
            password: password
        };
    
        console.log('Preparing to send request...');
        console.log('User data:', dataToSend);
    
        try {
            const response = await axios.post(BASE_URL + '/api/tokens', dataToSend, {
                headers: {
                    'Content-Type': 'application/json'
                }
            });
        
            console.log('Response received:', response.status);
        
            if (response.status === 201) {
                alert('Login successful');
                Promise.resolve().then(() => {
                const token = response.data.token;
                navigate('/homescreen', { 
                    state: { 
                        token: token,
                        isDarkMode: false
                    }
                });
                            });
            }
        } catch (error) {
            if (error.response) {
                // server error
                if (error.response.status === 401) {
                    alert('Login failed - Invalid username or password');
                }
                setError('Login failed');
            } else {
                // no server connection error
                console.error('An error occurred:', error.message || 'Unknown error');
                alert('No connection to server. Please try again later.');
                setError('No connection to server');
            }
            console.error('Login failed:', error);
        }
    };

    return (
        <div className="login-page">
            <RegisterLoginBar />
            
            {/* <div className="login-content">
                <div className="register">
                    <h1>login</h1>
                    <form onSubmit={handleSubmit}>
                        <input
                            type="text"
                            value={userName}
                            onChange={(e) => setUserName(e.target.value)}
                            placeholder="UserName"
                        />
                        <input
                            type="password"
                            value={password}
                            onChange={(e) => setPassword(e.target.value)}
                            placeholder="Password"
                        />
                        <button type="submit">Login</button>
                    </form>
                </div>
            </div> */}
            <AuthForm title="Login" inputs={inputs} handleSubmit={handleSubmit} buttonText="Login" />
        </div>
    );
};

export default Welcome;