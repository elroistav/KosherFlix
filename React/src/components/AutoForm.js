import React from 'react';
import '../styles/AutoForm.css';


function AuthForm({ title, inputs, handleSubmit, buttonText }) {
    return (
        <div className="auth-content">
            <div className="auth">
                <h1>{title}</h1>
                <form onSubmit={handleSubmit}>
                    {inputs.map((input, index) => (
                        <div key={index} className="form-group">
                            <input
                                type={input.type}
                                value={input.value}
                                onChange={input.onChange}
                                placeholder={input.placeholder}
                                {...(input.extraProps || {})} // עבור שדות מיוחדים כמו file
                            />
                        </div>
                    ))}
                    <button type="submit">{buttonText}</button>
                </form>
            </div>
        </div>
    );
}

export default AuthForm;
