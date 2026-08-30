import { apiCall } from './apiClient.js';

/**
 * Service API module for assessment test taking and submission.
 */
export const assessmentAPI = {
  /** Fetches active assigned test questions for the authenticated candidate. */
  getQuestions: () => apiCall('/assessment/questions'),

  /** Submits completed candidate answer options for grading. */
  submitAssessment: (answers) =>
    apiCall('/assessment/submit', 'POST', { answers })
};
