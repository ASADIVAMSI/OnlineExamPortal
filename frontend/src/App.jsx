import React from 'react';
import { HashRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import LandingPage from './pages/LandingPage.jsx';
import LoginPage from './pages/LoginPage.jsx';
import SignupPage from './pages/SignupPage.jsx';
import AdminPage from './pages/AdminPage.jsx';
import AssessmentPage from './pages/AssessmentPage.jsx';
import UserDashboardPage from './pages/UserDashboardPage.jsx';
import './App.css';

/**
 * Root React application component defining HashRouter application routes for
 * Landing, Authentication, Admin Panel, Student Dashboard, and Assessment pages.
 */
function App() {
  return (
    <Router>
      <Routes>
        {/* Public Landing Page */}
        <Route path="/" element={<LandingPage />} />
        
        {/* Authentication Routes */}
        <Route path="/login" element={<LoginPage />} />
        <Route path="/signup" element={<SignupPage />} />
        
        {/* Administrator Management Interface */}
        <Route path="/admin" element={<AdminPage />} />
        
        {/* Candidate Exam Assessment Taking Screen */}
        <Route path="/assessment" element={<AssessmentPage />} />
        
        {/* Student Dashboard & Score History */}
        <Route path="/user-dashboard" element={<UserDashboardPage />} />
        
        {/* Catch-all fallback redirecting unknown paths to Home */}
        <Route path="*" element={<Navigate to="/" replace />} />
      </Routes>
    </Router>
  );
}

export default App;
