import React, { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { userAPI } from '../services/userApi.js';
import './UserDashboardPage.css';

/**
 * Student User Dashboard Page Component.
 * Displays candidate registration approval state, assigned test set, previous exam score reports,
 * notifications list, and allows requesting new assessment modules.
 */
function UserDashboardPage() {
  const navigate = useNavigate();
  const [user, setUser] = useState(null);
  const [notifications, setNotifications] = useState([]);
  const [message, setMessage] = useState('Checking your account status...');
  const [errorMessage, setErrorMessage] = useState('');
  const [isLoading, setIsLoading] = useState(true);
  const [isRefreshing, setIsRefreshing] = useState(false);
  const [isRequestingNew, setIsRequestingNew] = useState(false);
  const [lastSyncedAt, setLastSyncedAt] = useState('');

  /**
   * Helper function updating status message depending on user approval and test progress.
   * @param {Object} userData - Candidate user profile object.
   */
  const updateMessageForStatus = (userData) => {
    if (userData.approvalStatus === 'COMPLETED') {
      setMessage('You have completed your assessment. You can submit a request for a new assessment below.');
    } else if (userData.approvalStatus === 'APPROVED' && userData.assignedSet) {
      setMessage('Your assessment is ready.');
    } else if (userData.approvalStatus === 'REJECTED') {
      setMessage('Your access request was rejected. Contact the administrator.');
    } else {
      setMessage('Your account is waiting for administrator approval and question-set assignment.');
    }
  };

  useEffect(() => {
    const boot = async () => {
      const token = sessionStorage.getItem('authToken');
      if (!token) {
        navigate('/');
        return;
      }

      try {
        const [userData, notificationsData] = await Promise.all([
          userAPI.getStatus(),
          userAPI.getNotifications().catch(() => [])
        ]);
        
        if (userData.role === 'ADMIN') {
          navigate('/admin', { replace: true });
          return;
        }
        setErrorMessage('');
        setUser(userData);
        setNotifications(notificationsData);
        updateMessageForStatus(userData);
        setLastSyncedAt(new Date().toLocaleTimeString([], { hour: 'numeric', minute: '2-digit' }));
      } catch {
        sessionStorage.removeItem('authToken');
        navigate('/');
      } finally {
        setIsLoading(false);
      }
    };

    boot();
  }, [navigate]);

  const handleLogout = () => {
    sessionStorage.removeItem('authToken');
    navigate('/');
  };

  const refreshStatus = async () => {
    try {
      setIsRefreshing(true);
      const [userData, notificationsData] = await Promise.all([
        userAPI.getStatus(),
        userAPI.getNotifications().catch(() => [])
      ]);
      setErrorMessage('');
      setUser(userData);
      setNotifications(notificationsData);
      updateMessageForStatus(userData);
      setLastSyncedAt(new Date().toLocaleTimeString([], { hour: 'numeric', minute: '2-digit' }));
    } catch (error) {
      setErrorMessage(error.message || 'Unable to refresh your status right now.');
    } finally {
      setIsRefreshing(false);
    }
  };

  const handleStartAssessment = () => {
    navigate('/assessment');
  };

  const handleRequestNewAssessment = async () => {
    try {
      setIsRequestingNew(true);
      const updatedUser = await userAPI.requestNewAssessment();
      setUser(updatedUser);
      updateMessageForStatus(updatedUser);
      const notificationsData = await userAPI.getNotifications().catch(() => []);
      setNotifications(notificationsData);
    } catch (error) {
      setErrorMessage(error.message || 'Could not submit new assessment request.');
    } finally {
      setIsRequestingNew(false);
    }
  };

  const isAssessmentReady = user && user.approvalStatus === 'APPROVED' && user.assignedSet;

  return (
    <div className="dashboard-page">
      <header className="site-header page-frame">
        <Link className="brand" to="/">
          <span className="brand-mark">+</span>
          <span>Online Exam Portal</span>
        </Link>
        <button className="outline-button" onClick={handleLogout} type="button">
          Sign out
        </button>
      </header>

      <main className="dashboard-shell page-frame">
        <section className="dashboard-hero section-card">
          <p className="eyebrow">User dashboard</p>
          <h1 className="panel-title" id="welcome">
            {isLoading ? 'Your assessment journey' : `Welcome, ${user?.fullName || 'User'}`}
          </h1>
          <p id="dashboard-message" className="intro-copy">
            {message}
          </p>

          <div className="dashboard-grid">
            {isLoading ? (
              <div className="loading">Loading your status...</div>
            ) : (
              <section className="status-panel section-card">
                <div>
                  <span className="status-label">Approval status</span>
                  <strong id="approval-status" className={`status-${(user?.approvalStatus || '').toLowerCase()}`}>
                    {user?.approvalStatus || 'Loading'}
                  </strong>
                </div>
                <div>
                  <span className="status-label">Assigned question set</span>
                  <strong id="assigned-set">
                    {user?.assignedSet || 'Not assigned'}
                  </strong>
                </div>
                {user?.approvalStatus === 'COMPLETED' ? (
                  <button
                    id="request-new-assessment"
                    className="primary-button"
                    type="button"
                    disabled={isRequestingNew}
                    onClick={handleRequestNewAssessment}
                  >
                    {isRequestingNew ? 'Submitting request...' : 'Request new assessment'}
                  </button>
                ) : (
                  <button
                    id="start-assessment"
                    className="primary-button"
                    type="button"
                    disabled={!isAssessmentReady}
                    onClick={handleStartAssessment}
                  >
                    Start assessment
                  </button>
                )}
              </section>
            )}

            <aside className="dashboard-summary">
              {errorMessage && (
                <div className="summary-card summary-alert" role="alert">
                  <h3>Could not refresh</h3>
                  <p>{errorMessage}</p>
                  <button className="outline-button" type="button" onClick={refreshStatus} disabled={isRefreshing}>
                    {isRefreshing ? 'Refreshing...' : 'Try again'}
                  </button>
                </div>
              )}
              {notifications.length > 0 && (
                <div className="summary-card notifications-card">
                  <h3>Notifications</h3>
                  <ul style={{ paddingLeft: '20px', margin: '10px 0' }}>
                    {notifications.map((note) => (
                      <li key={note.id} style={{ marginBottom: '10px' }}>
                        {note.message}
                        <br />
                        <small style={{ color: '#666' }}>
                          {new Date(note.createdAt).toLocaleString()}
                        </small>
                      </li>
                    ))}
                  </ul>
                </div>
              )}
              <div className="summary-card">
                <h3>What happens next</h3>
                <p>
                  Once your account is approved and a question set is assigned, you can launch the
                  assessment from this screen.
                </p>
              </div>
              <div className="summary-card">
                <h3>Account state</h3>
                <p>
                  The portal stays synced with the Spring Boot backend, so your status here reflects
                  the current server data.
                </p>
              </div>
              <div className="summary-card">
                <h3>Last synced</h3>
                <p>{lastSyncedAt || 'Not synced yet'}</p>
              </div>
            </aside>
          </div>
        </section>
      </main>
    </div>
  );
}

export default UserDashboardPage;
