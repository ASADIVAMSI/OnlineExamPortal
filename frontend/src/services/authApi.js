import { apiCall } from './apiClient.js';

/**
 * Service API module handling authentication (signup and signin).
 */
export const authAPI = {
  /** Registers a new user account. */
  signup: (fullName, email, password) =>
    apiCall('/auth/signup', 'POST', { fullName, email, password }),

  /** Authenticates user credentials and retrieves session token. */
  signin: (email, password) =>
    apiCall('/auth/signin', 'POST', { email, password })
};
