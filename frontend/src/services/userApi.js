import { apiCall } from './apiClient.js';

/**
 * Service API module handling student user dashboard data and notification queries.
 */
export const userAPI = {
  /** Fetches current user profile, active assignment, and completed test submission results. */
  getStatus: () => apiCall('/user/me'),

  /** Fetches list of notifications for candidate user. */
  getNotifications: () => apiCall('/user/notifications'),

  /** Submits a request for a new test module assignment. */
  requestNewAssessment: () => apiCall('/user/request-assessment', { method: 'POST' })
};
