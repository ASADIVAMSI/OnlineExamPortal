import React, { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { adminAPI } from '../services/adminApi.js';
import { userAPI } from '../services/userApi.js';
import './AdminPage.css';

/**
 * Admin Panel Page Component.
 * Provides management views for approving candidate registrations, assigning question sets,
 * viewing live test completion statuses, and reviewing submitted candidate exams with answer breakdowns.
 */
function AdminPage() {
  const navigate = useNavigate();
  const [users, setUsers] = useState([]);
  const [questionSets, setQuestionSets] = useState([]);
  const [submissions, setSubmissions] = useState([]);
  const [message, setMessage] = useState('');
  const [errorMessage, setErrorMessage] = useState('');
  const [isLoading, setIsLoading] = useState(true);
  const [isRefreshing, setIsRefreshing] = useState(false);
  const [expandedSubmissions, setExpandedSubmissions] = useState(new Set());
  const [selectedSets, setSelectedSets] = useState({});
  const [lastSyncedAt, setLastSyncedAt] = useState('');

  useEffect(() => {
    let intervalId;
    const boot = async () => {
      const token = sessionStorage.getItem('authToken');
      if (!token) {
        navigate('/');
        return;
      }

      try {
        const user = await userAPI.getStatus();
        if (user.role !== 'ADMIN') {
          navigate('/user-dashboard', { replace: true });
          return;
        }

        await loadAdminData();
        intervalId = setInterval(() => loadAdminData(true), 10000);
      } catch {
        sessionStorage.removeItem('authToken');
        navigate('/');
      }
    };

    boot();
    return () => {
      if (intervalId) clearInterval(intervalId);
    };
  }, [navigate]);

  const loadAdminData = async (silent = false) => {
    try {
      if (silent) {
        setIsRefreshing(true);
      } else {
        setIsLoading(true);
      }
      setErrorMessage('');
      const [usersData, setsData, submissionsData] = await Promise.all([
        adminAPI.getUsers(),
        adminAPI.getQuestionSets(),
        adminAPI.getSubmissions()
      ]);

      setUsers(usersData);
      setQuestionSets(setsData);
      setSubmissions(submissionsData);
      setMessage('');
      setLastSyncedAt(new Date().toLocaleTimeString([], { hour: 'numeric', minute: '2-digit' }));
    } catch (error) {
      setMessage('Admin data could not be loaded.');
      setErrorMessage(error.message || 'The backend is unavailable or the session is no longer valid.');
      if (error.status === 401) {
        sessionStorage.removeItem('authToken');
        navigate('/');
      }
    } finally {
      if (silent) {
        setIsRefreshing(false);
      } else {
        setIsLoading(false);
      }
    }
  };

  const handleLogout = () => {
    sessionStorage.removeItem('authToken');
    navigate('/');
  };

  const handleApprove = async (userId) => {
    try {
      const setId = selectedSets[userId];
      if (setId) {
        await adminAPI.assignQuestionSet(userId, setId);
      }
      await adminAPI.approveUser(userId);
      setSelectedSets((prev) => {
        const next = { ...prev };
        delete next[userId];
        return next;
      });
      setMessage('User approved successfully.');
      await loadAdminData();
    } catch {
      setMessage('The update could not be completed.');
    }
  };

  const handleReject = async (userId) => {
    try {
      await adminAPI.rejectUser(userId);
      setMessage('User rejected successfully.');
      loadAdminData();
    } catch {
      setMessage('The update could not be completed.');
    }
  };



  const toggleSubmissionExpanded = (submissionId) => {
    const next = new Set(expandedSubmissions);
    next.has(submissionId) ? next.delete(submissionId) : next.add(submissionId);
    setExpandedSubmissions(next);
  };

  return (
    <div className="admin-page">
      <header className="site-header page-frame">
        <Link className="brand" to="/">
          <span className="brand-mark">+</span>
          <span>Online Exam Portal</span>
        </Link>
        <button className="outline-button" onClick={handleLogout} type="button">
          Sign out
        </button>
      </header>

      <main className="admin-shell page-frame">
        <section className="admin-hero">
          <p className="eyebrow">Online Exam Portal / Admin</p>
          <h1 className="panel-title">User approvals and submission review</h1>
          <p className="intro-copy">
            Review registrations, assign question sets, and inspect submitted answers without
            changing the existing backend workflow.
          </p>
          <div className="admin-stats">
            <div className="stat-card"><span>Users</span><strong>{users.length}</strong></div>
            <div className="stat-card"><span>Sets</span><strong>{questionSets.length}</strong></div>
            <div className="stat-card"><span>Submissions</span><strong>{submissions.length}</strong></div>
            <div className="stat-card"><span>Last synced</span><strong>{lastSyncedAt || 'Pending'}</strong></div>
          </div>
          <div className="admin-hero-actions">
            <button className="outline-button" type="button" onClick={() => loadAdminData(true)} disabled={isRefreshing}>
              {isRefreshing ? 'Refreshing...' : 'Refresh data'}
            </button>
          </div>
        </section>

        {message && <p className={`message ${message.includes('required') ? 'error' : ''}`} role="status">{message}</p>}
        {errorMessage && <p className="message error" role="alert">{errorMessage}</p>}

        {isLoading ? (
          <div className="loading">Loading data...</div>
        ) : (
          <div className="admin-columns">
            <section className="section-card">
              <h2 className="panel-title">Users</h2>
              <div className="user-list">
                {users.length === 0 ? (
                  <p className="empty-state">No users found</p>
                ) : (
                  users.map((user) => (
                    <article key={user.id} className="user-row section-card">
                      <div className="user-row-header">
                        <div className="user-details">
                          <strong>{user.fullName}</strong>
                          <span className="user-email">{user.email}</span>
                          <div className="user-meta">
                            <span className="user-role-badge">{user.role}</span>
                            <span className={`status-badge status-${(user.approvalStatus || '').toLowerCase()}`}>
                              {user.approvalStatus}
                            </span>
                          </div>
                        </div>
                      </div>
                      {user.role !== 'ADMIN' && (
                        <div className="actions">
                          {(user.approvalStatus !== 'APPROVED' || Boolean(selectedSets[user.id])) && (
                            <button className="primary-button compact" onClick={() => handleApprove(user.id)} type="button">Approve</button>
                          )}
                          <button className="primary-button compact" onClick={() => handleReject(user.id)} type="button">Reject</button>
                          <select 
                            className="set-select" 
                            onChange={(e) => setSelectedSets({ ...selectedSets, [user.id]: e.target.value })} 
                            value={selectedSets[user.id] || ""}
                          >
                            <option value="">Assign set</option>
                            {questionSets.map((set) => (
                              <option key={set.id} value={set.id}>{set.name}</option>
                            ))}
                          </select>
                        </div>
                      )}
                    </article>
                  ))
                )}
              </div>
            </section>

            <section className="submission-section section-card">
              <h2 className="panel-title">
                Submitted assessments <span className="notification-badge">{submissions.length}</span>
              </h2>
              <div className="submission-list">
                {submissions.length === 0 ? (
                  <p className="empty-state">No submissions yet</p>
                ) : (
                  submissions.map((submission) => (
                    <article key={submission.id} className="submission-card section-card">
                      <div className="submission-info">
                        <strong>{submission.userName}</strong>
                        <span className="submission-email">{submission.email}</span>
                        <span className="submission-set-badge">{submission.questionSetName}</span>
                        <small className="submission-date">{new Date(submission.submittedAt).toLocaleString()}</small>
                        <details
                          open={expandedSubmissions.has(submission.id)}
                          onToggle={() => toggleSubmissionExpanded(submission.id)}
                        >
                          <summary>View answer review</summary>
                          <ol>
                            {(submission.answers || []).map((answer, index) => (
                              <li key={answer.questionId} className={answer.correct ? 'answer-correct' : 'answer-wrong'}>
                                {index + 1}. {answer.correct ? 'Correct' : 'Wrong'} - selected {answer.selectedOption}, correct {answer.correctOption}
                              </li>
                            ))}
                          </ol>
                        </details>
                      </div>
                      <div className="marks">
                        <b>{submission.correctAnswers} / {submission.totalMarks}</b>
                        <span>{submission.wrongAnswers} wrong</span>
                      </div>
                    </article>
                  ))
                )}
              </div>
            </section>
          </div>
        )}
      </main>
    </div>
  );
}

export default AdminPage;
