import React, { useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { userAPI } from '../services/userApi.js';
import './LandingPage.css';

/**
 * Public Landing Page Component.
 * Displays portal features, registration/login CTA links, and handles session auto-redirection if already logged in.
 */
function LandingPage() {
  const navigate = useNavigate();

  useEffect(() => {
    // Auto-redirect authenticated candidates/admins directly to their respective dashboards
    const redirectSignedInUser = async () => {
      const token = sessionStorage.getItem('authToken');
      if (!token) return;

      try {
        const user = await userAPI.getStatus();
        navigate(user.role === 'ADMIN' ? '/admin' : '/user-dashboard', { replace: true });
      } catch {
        sessionStorage.removeItem('authToken');
      }
    };

    redirectSignedInUser();
  }, [navigate]);

  return (
    <div className="landing-page">
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

      <main className="page-frame landing-shell">
        <section className="landing-card section-card">
          <p className="eyebrow">Online assessment</p>
          <h1 className="panel-title">Simple, secure exam portal</h1>
          <p className="intro-copy">
            Sign in to continue or create a new account. Approvals, question-set assignment, and
            submissions stay synchronized with the Spring Boot backend.
          </p>
          <div className="landing-actions">
            <Link className="primary-button" to="/login">Go to login</Link>
            <Link className="outline-button" to="/signup">Create account</Link>
          </div>
          <div className="landing-badges" aria-label="Portal highlights">
            <div className="feature-pill">
              <strong>Secure login</strong>
              <span>Session token based access</span>
            </div>
            <div className="feature-pill">
              <strong>Admin review</strong>
              <span>Approve users before assessments</span>
            </div>
            <div className="feature-pill">
              <strong>30-question exams</strong>
              <span>Assigned only by administrators</span>
            </div>
          </div>
        </section>

        <section className="landing-grid">
          <Link className="mini-card section-card" to="/login">
            <span className="eyebrow">Login</span>
            <strong>Sign in and continue</strong>
            <p>Jump to your dashboard, assessment, or admin tools with an existing account.</p>
          </Link>
          <Link className="mini-card section-card" to="/signup">
            <span className="eyebrow">Signup</span>
            <strong>Create an account</strong>
            <p>Register a new user account and wait for administrator approval.</p>
          </Link>
        </section>
      </main>
    </div>
  );
}

export default LandingPage;
