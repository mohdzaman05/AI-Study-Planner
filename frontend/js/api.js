/**
 * AI Study Planner - API Client Module
 * Unified architecture: all API calls use relative paths
 */

// Unified backend: all API calls use relative paths
const API_BASE = '/api';

const api = {
    async request(endpoint, options = {}) {
        const url = endpoint.startsWith('http') ? endpoint : `${API_BASE}${endpoint}`;
        const headers = {
            'Content-Type': 'application/json',
            'Accept': 'application/json',
            ...(options.headers || {})
        };

        const token = localStorage.getItem('study_token');
        if (token) {
            headers['Authorization'] = `Bearer ${token}`;
        }

        const config = {
            ...options,
            headers
        };

        try {
            const response = await fetch(url, config);

            // Handle 401 Unauthorized
            if (response.status === 401) {
                const currentPath = window.location.pathname;
                const publicPages = ['index.html', 'login.html', 'register.html'];
                const isPublic = publicPages.some(page => currentPath.endsWith(page)) || currentPath === '/' || currentPath === '';

                if (!isPublic) {
                    localStorage.removeItem('study_token');
                    localStorage.removeItem('study_user');
                    window.location.href = 'login.html?expired=true';
                    return null;
                }
            }

            if (response.status === 204) {
                return null;
            }

            const data = await response.json().catch(() => null);

            if (!response.ok) {
                let errorMessage = 'An error occurred. Please try again.';
                if (data && data.message) {
                    errorMessage = data.message;
                } else if (data && data.error) {
                    errorMessage = data.error;
                }
                const error = new Error(errorMessage);
                error.status = response.status;
                error.data = data;
                throw error;
            }

            return data;
        } catch (error) {
            console.error(`API Error on ${endpoint}:`, error);
            if (!error.status && (error.name === 'TypeError' || (error.message && error.message.toLowerCase().includes('fetch')))) {
                const connError = new Error('Unable to connect to backend server. Please make sure the backend is running (double-click start-backend.bat).');
                connError.isConnectionError = true;
                throw connError;
            }
            throw error;
        }
    },

    get(endpoint) {
        return this.request(endpoint, { method: 'GET' });
    },

    post(endpoint, body) {
        return this.request(endpoint, {
            method: 'POST',
            body: JSON.stringify(body)
        });
    },

    put(endpoint, body) {
        return this.request(endpoint, {
            method: 'PUT',
            body: JSON.stringify(body)
        });
    },

    patch(endpoint, body) {
        return this.request(endpoint, {
            method: 'PATCH',
            body: body ? JSON.stringify(body) : undefined
        });
    },

    delete(endpoint) {
        return this.request(endpoint, { method: 'DELETE' });
    }
};
