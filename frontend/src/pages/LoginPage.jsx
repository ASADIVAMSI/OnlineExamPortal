import React from 'react';
import { authAPI } from '../services/authApi.js';
import AuthPage from './AuthPage.jsx';

/**
 * Login Page Component.
 * Wraps AuthPage layout component with sign-in specific configuration, fields, and API handler.
 */
function LoginPage() {
  return (
    <AuthPage
      mode="login"
      title="Welcome back"
      subtitle="Sign in to access your dashboard, assessment, or admin tools."
      submitLabel="Sign in"
      fields={[
        { name: 'email', label: 'Email', type: 'email', autoComplete: 'email' },
        { name: 'password', label: 'Password', type: 'password', autoComplete: 'current-password' }
      ]}
      onSubmit={async (formData) => {
        // Authenticate credentials via Auth API
        const response = await authAPI.signin(formData.get('email'), formData.get('password'));
        // Save retrieved JWT token to session storage
        sessionStorage.setItem('authToken', response.token);
        // Redirect user based on assigned role
        return response.role === 'ADMIN' ? '/admin' : '/user-dashboard';
      }}
    />
  );
}

export default LoginPage;
