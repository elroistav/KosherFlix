import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';
import Navbar from '../components/RegisterLoginBar'; // ייבוא ה-navbar
import '../styles/RegisterLogin.css';

const Welcome = () => {
    const navigate = useNavigate(); 
    const [userName, setUserName] = useState(''); 
    const [name, setName] = useState(''); 
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [profilePicture, setProfilePicture] = useState(null);
    const [error, setError] = useState('');

    const handleSubmit = async (event) => {
        event.preventDefault();

        if (!userName || !name || !email || !password || !profilePicture) {
            alert('All fields are required, including profile picture!');
            setError('All fields are required');
            return;
        }

    const userNameRegex = /^[A-Za-z0-9_-]{3,15}$/;
    if (!userNameRegex.test(userName)) {
        alert('UserName must be between 3 and 15 characters and can only contain letters, numbers, underscores, and hyphens.');
        setError('UserName must be between 3 and 15 characters and contain only letters, numbers, underscores, and hyphens.');
        return;
    }

    const nameRegex = /^[A-Za-z ]+$/;
    if (!nameRegex.test(name)) {
        alert('Full name can only contain English letters and spaces.');
        setError('Full name can only contain English letters and spaces.');
        return;
    }

        if (password.length < 8) {
            alert('Password must be at least 8 characters');
            setError('Password must be at least 8 characters');
            return;
        }

        const passwordRegex = /^(?=.*[a-zA-Z])(?=.*\d)[A-Za-z\d!@#$%^&*]{8,}$/;
        if (!passwordRegex.test(password)) {
            alert('Password must contain at least one letter and one number, and no special characters.');
            setError('Password must contain at least one letter and one number.');
            return;
        }

        if (profilePicture) {
            const validImageTypes = ['image/jpeg', 'image/png', 'image/jpg'];
            if (!validImageTypes.includes(profilePicture.type)) {
                alert('Please upload a valid image file (JPG, PNG, JPEG).');
                setError('Please upload a valid image file (JPG, PNG, JPEG).');
                return;
            }
        }

        const formData = new FormData();
        formData.append('userName', userName);
        formData.append('name', name);
        formData.append('email', email);
        formData.append('password', password);
        formData.append('profilePicture', profilePicture);
    
        console.log('Preparing to send request...');
        console.log('User data:', formData);
    
        try {
            const response = await axios.post('http://localhost:3000/api/users', formData, {
                headers: {
                    'Content-Type': 'multipart/form-data'
                }
            });
    
            console.log('Response received:', response.status);
    

            if (response.status === 201) {
                alert('Registration successful');
                Promise.resolve().then(() => {
                    navigate('/homescreen');
                });
            } else {
                setError('Registration failed');
                console.error('Registration failed');
            }
        } catch (error) {
            setError('Registration failed');
            console.error('Registration failed', error);
        }
    };

    return (
        <div className="register-page">
            <Navbar />
            
            <div className="register-content">
                <div className="register">
                    <h1>register</h1>
                    <form onSubmit={handleSubmit}>
                        <input
                            type="text"
                            value={userName}
                            onChange={(e) => setUserName(e.target.value)}
                            placeholder="UserName"
                        />
                        <input
                            type="text"
                            value={name}
                            onChange={(e) => setName(e.target.value)}
                            placeholder="Full Name"
                        />
                        <input
                            type="email"
                            value={email}
                            onChange={(e) => setEmail(e.target.value)}
                            placeholder="Email"
                        />
                        <input
                            type="password"
                            value={password}
                            onChange={(e) => setPassword(e.target.value)}
                            placeholder="Password"
                        />
                        <div className="file-upload">
                            <label htmlFor="profilePicture">Upload Profile Picture</label>
                            <input
                                type="file"
                                id="profilePicture"
                                onChange={(e) => setProfilePicture(e.target.files[0])}
                            />
                        </div>
                        <button type="submit">Register</button>
                    </form>
                </div>
            </div>
        </div>
    );
};

export default Welcome;