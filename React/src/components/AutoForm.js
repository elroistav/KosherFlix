import React from 'react';
import '../styles/AutoForm.css';


const AuthForm = ({ title, inputs, handleSubmit, buttonText, imagePreview }) => {
    return (
        <div className="auth-form">
            <h1>{title}</h1>
            <form onSubmit={handleSubmit}>
                {inputs.map((input, index) => (
                    <div key={index}>
                        {input.type === 'file' ? (
                            <div className="file-input-container">
                                <input
                                    type={input.type}
                                    onChange={input.onChange}
                                    placeholder="Upload image file (JPG, PNG, JPEG)"
                                    {...input.extraProps}
                                />
                                {imagePreview && (
                                    <img 
                                        src={imagePreview} 
                                        alt="Profile Preview" 
                                        className="preview-image"
                                    />
                                )}
                            </div>
                        ) : (
                            <input
                                type={input.type}
                                value={input.value}
                                onChange={input.onChange}
                                placeholder={input.placeholder}
                                {...input.extraProps}
                            />
                        )}
                    </div>
                ))}
                <button type="submit">{buttonText}</button>
            </form>
        </div>
    );
};

export default AuthForm;
