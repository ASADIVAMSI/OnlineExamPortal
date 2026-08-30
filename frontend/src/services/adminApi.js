import { apiCall } from './apiClient.js';

/**
 * Service API module wrapper for administrator functionality.
 */
export const adminAPI = {
  /** Fetches list of registered users and their assignment status. */
  getUsers: () => apiCall('/admin/users'),

  /** Fetches list of available question sets. */
  getQuestionSets: () => apiCall('/admin/question-sets'),

  /** Fetches list of all candidate exam submissions. */
  getSubmissions: () => apiCall('/admin/submissions'),

  /** Approves a pending candidate account registration. */
  approveUser: (userId) => apiCall(`/admin/users/${userId}/approve`, 'PUT'),

  /** Rejects a pending candidate account registration. */
  rejectUser: (userId) => apiCall(`/admin/users/${userId}/reject`, 'PUT'),

  /** Assigns a specific QuestionSet module to a candidate user. */
  assignQuestionSet: (userId, questionSetId) =>
    apiCall(`/admin/users/${userId}/question-set/${questionSetId}`, 'PUT')
};
