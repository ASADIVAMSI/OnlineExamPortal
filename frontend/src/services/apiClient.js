/**
 * API Client Utility Module
 * Handles API base URL configuration, session storage authentication token management,
 * and fetch requests with automatic headers and error handling.
 */

const DEFAULT_API_BASE_URL = '/api';
const API_BASE_URL = (import.meta.env.VITE_API_BASE_URL || DEFAULT_API_BASE_URL).replace(/\/$/, '');

/**
 * Retrieves the stored authentication token from session storage.
 * @returns {string|null} The active auth token or null if not logged in.
 */
export function getAuthToken() {
  return sessionStorage.getItem('authToken');
}

/**
 * Persists an authentication token into session storage.
 * @param {string} token - The auth token returned by login/signup.
 */
export function setAuthToken(token) {
  sessionStorage.setItem('authToken', token);
}

/**
 * Clears the active authentication token from session storage upon logout.
 */
export function clearAuthToken() {
  sessionStorage.removeItem('authToken');
}

/**
 * Normalizes and builds a full request URL from an API endpoint path.
 * @param {string} endpoint - The target endpoint path (e.g. '/admin/users').
 * @returns {string} The full destination URL.
 */
function buildUrl(endpoint) {
  const normalizedEndpoint = endpoint.startsWith('/') ? endpoint : `/${endpoint}`;
  return `${API_BASE_URL}${normalizedEndpoint}`;
}

/**
 * Safely parses response body content as JSON or text.
 * @param {Response} response - The fetch Response object.
 * @returns {Promise<any>} The parsed response body content.
 */
async function readResponseBody(response) {
  const contentType = response.headers.get('content-type') || '';

  if (contentType.includes('application/json')) {
    return response.json().catch(() => ({}));
  }

  return response.text().catch(() => '');
}

/**
 * Extracts a human-readable error message from response data.
 * @param {any} responseData - The parsed error response body.
 * @returns {string} The extracted error message.
 */
function messageFromResponseData(responseData) {
  if (typeof responseData === 'string') {
    return responseData.trim();
  }

  if (responseData && typeof responseData === 'object') {
    return (
      responseData.message ||
      responseData.detail ||
      responseData.error ||
      responseData.title ||
      ''
    );
  }

  return '';
}

/**
 * Executes an HTTP fetch API call with custom headers and auth token management.
 * @param {string} endpoint - Target relative endpoint path.
 * @param {string} [method='GET'] - HTTP method (GET, POST, PUT, DELETE).
 * @param {any} [body=null] - Request payload object.
 * @returns {Promise<any>} Parsed response data.
 * @throws {Error} Throws error with HTTP status and parsed error details on failure.
 */
export async function apiCall(endpoint, method = 'GET', body = null) {
  const token = getAuthToken();
  const isPublicAuthRoute = endpoint.startsWith('/auth/');
  const headers = {
    'Content-Type': 'application/json'
  };

  // Attach custom auth header for non-public routes
  if (!isPublicAuthRoute && token) {
    headers['X-Auth-Token'] = token;
  }

  const options = { method, headers };

  if (body !== null) {
    options.body = JSON.stringify(body);
  }

  let response;

  try {
    response = await fetch(buildUrl(endpoint), options);
  } catch {
    const error = new Error('Unable to reach the backend. Start the Spring Boot server and try again.');
    error.status = 0;
    error.data = null;
    throw error;
  }

  const responseData = await readResponseBody(response);

  if (!response.ok) {
    const message = messageFromResponseData(responseData) || 'API request failed';
    const error = new Error(message);
    error.status = response.status;
    error.data = responseData;
    throw error;
  }

  return responseData;
}
