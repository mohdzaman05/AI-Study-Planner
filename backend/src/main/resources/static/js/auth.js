/**
 * AI Study Planner - Authentication & Session Module
 */

const auth = {
    TOKEN_KEY: 'study_token',
    USER_KEY: 'study_user',

    isAuthenticated() {
        return !!localStorage.getItem(this.TOKEN_KEY);
    },

    getToken() {
        return localStorage.getItem(this.TOKEN_KEY);
    },

    getUser() {
        const raw = localStorage.getItem(this.USER_KEY);
        try {
            return raw ? JSON.parse(raw) : null;
        } catch (e) {
            return null;
        }
    },

    saveSession(authResponse) {
        if (authResponse && authResponse.token) {
            localStorage.setItem(this.TOKEN_KEY, authResponse.token);
            const user = {
                id: authResponse.id,
                email: authResponse.email,
                fullName: authResponse.fullName
            };
            localStorage.setItem(this.USER_KEY, JSON.stringify(user));
        }
    },

    updateUser(updates) {
        const current = this.getUser() || {};
        const merged = { ...current, ...updates };
        localStorage.setItem(this.USER_KEY, JSON.stringify(merged));
    },

    logout() {
        localStorage.removeItem(this.TOKEN_KEY);
        localStorage.removeItem(this.USER_KEY);
        window.location.href = '/login.html';
    },

    /**
     * Enforce page authorization.
     * If requiresAuth is true, unauthenticated users are redirected to login.html.
     * If requiresAuth is false and user is already logged in, redirect to dashboard.html.
     */
    guard(requiresAuth = true) {
        const authed = this.isAuthenticated();
        if (requiresAuth && !authed) {
            window.location.href = '/login.html?redirect=' + encodeURIComponent(window.location.pathname);
        } else if (!requiresAuth && authed) {
            window.location.href = '/dashboard.html';
        }
    }
};
