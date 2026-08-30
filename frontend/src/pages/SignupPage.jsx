import React from 'react';
import { authAPI } from '../services/authApi.js';
import AuthPage from './AuthPage.jsx';

/**
 * Signup Page Component.
 * Provides user registration form layout wrapped inside AuthPage layout component.
 */
function SignupPage() {
  return (
    <AuthPage
      mode="signup"
      title="Create your account"
      subtitle="New accounts stay pending until an administrator approves them."
      submitLabel="Create account"
      fields={[
        { name: 'fullName', label: 'Full name', type: 'text', autoComplete: 'name' },
        { name: 'email', label: 'Email', type: 'email', autoComplete: 'email' },
        { name: 'password', label: 'Password', type: 'password', autoComplete: 'new-password', minLength: 6 }
      ]}
      onSubmit={async (formData) => {
        // Register candidate details with backend Auth API
        await authAPI.signup(formData.get('fullName'), formData.get('email'), formData.get('password'));
        // Redirect user to login page after successful registration
        return '/login';
      }}
    />
  );
}

export default SignupPage;
