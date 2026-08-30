import React, { useEffect, useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { userAPI } from '../services/userApi.js';

/**
 * Shared Authentication Form Component (handles login and signup views, form state, and session checks).
 *
 * @param {Object} props Component properties.
 * @param {'login'|'signup'} props.mode Auth page mode identifier.
 * @param {Function} props.onSubmit Form submit handler callback.
 * @param {string} props.title Heading title text.
 * @param {string} props.subtitle Subheading text.
 * @param {Array} props.fields Input field configurations.
 * @param {string} props.submitLabel Text display for submission button.
 */
function AuthPage({ mode, onSubmit, title, subtitle, fields, submitLabel }) {
  const navigate = useNavigate();
  const [message, setMessage] = useState('');
  const [isError, setIsError] = useState(false);
  const [isLoading, setIsLoading] = useState(false);
  const [isCheckingSession, setIsCheckingSession] = useState(true);

  useEffect(() => {
    const redirectSignedInUser = async () => {
      const token = sessionStorage.getItem('authToken');
      if (!token) {
        setIsCheckingSession(false);
        return;
      }

      try {
        const user = await userAPI.getStatus();
        navigate(user.role === 'ADMIN' ? '/admin' : '/user-dashboard', { replace: true });
      } catch {
        sessionStorage.removeItem('authToken');
        setIsCheckingSession(false);
      }
    };

    redirectSignedInUser();
  }, [navigate]);

  if (isCheckingSession) {
    return (
      <div className="auth-page">
        <main className="page-frame auth-shell">
          <div className="loading">Checking your session...</div>
        </main>
      </div>
    );
  }

  const handleSubmit = async (event) => {
    event.preventDefault();
    setIsLoading(true);
    setMessage('');

    try {
      const nextPath = await onSubmit(new FormData(event.currentTarget));
      setIsError(false);
      setMessage(mode === 'login' ? 'Signing in...' : 'Account created successfully.');
      if (nextPath) {
        setTimeout(() => navigate(nextPath, { replace: true }), 250);
      }
      if (mode === 'signup') {
        event.currentTarget.reset();
      }
    } catch (error) {
      setIsError(true);
      setMessage(error.message || 'Request failed');
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="auth-page">
      <header className="site-header page-frame">
        <Link className="brand" to="/">
          <span className="brand-mark">+</span>
          <span>Online Exam Portal</span>
        </Link>
        <div className="header-actions">
          <Link className="outline-button" to="/login">Login</Link>
          <Link className="dark-button" to="/signup">Sign up</Link>
        </div>
      </header>

      <main className="page-frame auth-shell">
        <section className="auth-card section-card">
          <p className="eyebrow">{mode === 'login' ? 'Sign in' : 'Create account'}</p>
          <h1 className="panel-title">{title}</h1>
          <p className="intro-copy">{subtitle}</p>

          <form className="auth-form" onSubmit={handleSubmit}>
            {fields.map((field) => (
              <label key={field.name}>
                {field.label}
                <input
                  type={field.type}
                  name={field.name}
                  autoComplete={field.autoComplete}
                  required={field.required !== false}
                  minLength={field.minLength}
                />
              </label>
            ))}
            <button className="primary-button auth-submit" type="submit" disabled={isLoading}>
              {isLoading ? `${submitLabel}...` : submitLabel}
            </button>
          </form>

          {message && (
            <p className={`message ${isError ? 'error' : ''}`} role="status">
              {message}
            </p>
          )}

          <p className="auth-switch">
            {mode === 'login' ? (
              <>
                Need an account? <Link to="/signup">Create one</Link>
              </>
            ) : (
              <>
                Already have an account? <Link to="/login">Sign in</Link>
              </>
            )}
          </p>
        </section>
      </main>
    </div>
  );
}

export default AuthPage;
