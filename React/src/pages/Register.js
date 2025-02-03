import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';
import RegisterLoginBar from '../components/RegisterLoginBar'; // ייבוא ה-navbar
import '../styles/RegisterLogin.css';
import AuthForm from '../components/AutoForm';


const Welcome = () => {
    const BASE_URL = process.env.REACT_APP_BASE_URL;
    const navigate = useNavigate(); 
    const [userName, setUserName] = useState(''); 
    const [name, setName] = useState(''); 
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [profilePicture, setProfilePicture] = useState(null);
    const [error, setError] = useState('');
    const [imagePreview, setImagePreview] = useState(null); 

    const handleImageChange = (e) => { 
        const file = e.target.files[0];
        if (file) {
            setProfilePicture(file);
            setImagePreview(URL.createObjectURL(file));
        }
    };

    const inputs = [
        { type: 'text', value: userName, onChange: (e) => setUserName(e.target.value), placeholder: 'UserName (3-15 characters, letters, numbers, underscores, hyphens)' },
        { type: 'text', value: name, onChange: (e) => setName(e.target.value), placeholder: 'Full Name (English letters only)' },
        { type: 'email', value: email, onChange: (e) => setEmail(e.target.value), placeholder: 'Email (valid email address)' },
        { type: 'password', value: password, onChange: (e) => setPassword(e.target.value), placeholder: 'Password (at least 8, one letter, one number)' },
        { 
            type: 'file', 
            value: undefined, 
            onChange: handleImageChange, 
            placeholder: '', 
            extraProps: { id: 'profilePicture' },
        },
    ];

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
            const response = await axios.post(BASE_URL + '/api/users', formData, {
                headers: {
                    'Content-Type': 'multipart/form-data'
                }
            });
    
            console.log('Response received:', response.status);
    

            if (response.status === 201) {
                alert('Registration successful');
                Promise.resolve().then(() => {
                    navigate('/login');
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
            <RegisterLoginBar />
            
            {/* <div className="register-content">
                <div className="register">
                    <h1>register</h1>
                    <form onSubmit={handleSubmit}>
                        <input
                            type="text"
                            value={userName}
                            onChange={(e) => setUserName(e.target.value)}
                            placeholder="UserName (3-15 characters, letters, numbers, underscores, hyphens)"
                        />
                        <input
                            type="text"
                            value={name}
                            onChange={(e) => setName(e.target.value)}
                            placeholder="Full Name (English letters only)"
                        />
                        <input
                            type="email"
                            value={email}
                            onChange={(e) => setEmail(e.target.value)}
                            placeholder="Email (valid email address)"
                        />
                        <input
                            type="password"
                            value={password}
                            onChange={(e) => setPassword(e.target.value)}
                            placeholder="Password (at least 8, one letter, one number)"
                        />
                        <div className="file-upload">
                            <label htmlFor="profilePicture">Upload Profile Picture</label>
                            <input
                                type="file"
                                id="profilePicture"
                                onChange={handleImageChange}
                            />
                            {imagePreview && (
                                <img 
                                    src={imagePreview} 
                                    alt="Preview" 
                                    className="preview-image"
                                />
                            )}
                        </div>
                        <button type="submit">Register</button>
                    </form>
                </div>
            </div> */}

<AuthForm 
    title="Register"
    inputs={inputs}
    handleSubmit={handleSubmit}
    buttonText="Register"
    imagePreview={imagePreview}
/>
        </div>
    );
};

export default Welcome;