/**
 * AI Study Planner - UI Components, Layout & Theme Manager Module
 */

// Initialize Theme immediately on script load
(function initTheme() {
    const saved = localStorage.getItem('study_theme') || 'dark';
    document.documentElement.setAttribute('data-theme', saved);
})();

const ui = {
    getTheme() {
        return localStorage.getItem('study_theme') || 'dark';
    },

    setTheme(theme) {
        document.documentElement.setAttribute('data-theme', theme);
        localStorage.setItem('study_theme', theme);
        this.updateThemeButton();
    },

    toggleTheme() {
        const current = this.getTheme();
        const next = current === 'dark' ? 'light' : 'dark';
        this.setTheme(next);
        this.showToast(`Switched to ${next === 'dark' ? 'Dark' : 'Light'} Mode`, 'info', 2000);
    },

    updateThemeButton() {
        const btn = document.getElementById('themeToggleBtn');
        if (!btn) return;
        const theme = this.getTheme();
        const icon = theme === 'dark' ? '🌙' : '☀️';
        const label = theme === 'dark' ? 'Dark' : 'Light';
        btn.innerHTML = `
            <span class="theme-icon">${icon}</span>
            <span class="theme-label">${label}</span>
        `;
        btn.setAttribute('title', `Current: ${label} mode. Click to switch.`);
    },

    showToast(message, type = 'info', duration = 3500) {
        let container = document.getElementById('toastContainer');
        if (!container) {
            container = document.createElement('div');
            container.id = 'toastContainer';
            container.className = 'toast-container';
            document.body.appendChild(container);
        }

        const toast = document.createElement('div');
        toast.className = `toast toast-${type}`;

        const icon = type === 'success' ? '✓' : (type === 'error' ? '✕' : 'ℹ');
        toast.innerHTML = `
            <span style="font-weight: 700; font-size: 1.1rem;">${icon}</span>
            <span style="flex: 1;">${message}</span>
        `;

        container.appendChild(toast);

        setTimeout(() => {
            toast.style.opacity = '0';
            toast.style.transform = 'translateX(100%)';
            toast.style.transition = 'all 0.3s ease';
            setTimeout(() => toast.remove(), 300);
        }, duration);
    },

    openModal(modalId) {
        const modal = document.getElementById(modalId);
        if (modal) {
            modal.classList.add('active');
        }
    },

    closeModal(modalId) {
        const modal = document.getElementById(modalId);
        if (modal) {
            modal.classList.remove('active');
        }
    },

    formatDate(dateStr) {
        if (!dateStr) return 'N/A';
        try {
            const date = new Date(dateStr + 'T00:00:00');
            return date.toLocaleDateString('en-US', {
                month: 'short',
                day: 'numeric',
                year: 'numeric'
            });
        } catch (e) {
            return dateStr;
        }
    },

    getDifficultyBadge(difficulty) {
        const d = (difficulty || 'MEDIUM').toUpperCase();
        if (d === 'HARD') return `<span class="badge badge-danger">Hard</span>`;
        if (d === 'EASY') return `<span class="badge badge-success">Easy</span>`;
        return `<span class="badge badge-warning">Medium</span>`;
    },

    getPriorityBadge(priority) {
        const p = (priority || 'MEDIUM').toUpperCase();
        if (p === 'HIGH') return `<span class="badge badge-danger">High Priority</span>`;
        if (p === 'LOW') return `<span class="badge badge-subtle">Low Priority</span>`;
        return `<span class="badge badge-primary">Medium Priority</span>`;
    },

    getStatusBadge(status) {
        const s = (status || 'NOT_STARTED').toUpperCase();
        if (s === 'COMPLETED') return `<span class="badge badge-success">Completed</span>`;
        if (s === 'IN_PROGRESS') return `<span class="badge badge-warning">In Progress</span>`;
        if (s === 'MISSED') return `<span class="badge badge-danger">Missed</span>`;
        return `<span class="badge badge-subtle">Not Started</span>`;
    },

    renderNavbar(activePage = '') {
        const navPlaceholder = document.getElementById('navbar');
        if (!navPlaceholder) return;

        const user = auth.getUser();
        const authed = auth.isAuthenticated();

        if (!authed) {
            navPlaceholder.innerHTML = `
                <div class="nav-container">
                    <a href="index.html" class="nav-brand">
                        <span>🎓 AI Study Planner</span>
                        <span class="brand-badge">Academic</span>
                    </a>
                    <div class="nav-actions">
                        <button type="button" id="themeToggleBtn" class="theme-toggle-btn">
                            <!-- Theme icon and label populated dynamically -->
                        </button>
                        <a href="login.html" class="btn btn-secondary btn-sm">Login</a>
                        <a href="register.html" class="btn btn-primary btn-sm">Get Started</a>
                    </div>
                </div>
            `;
            this.updateThemeButton();
            document.getElementById('themeToggleBtn')?.addEventListener('click', () => this.toggleTheme());
            return;
        }

        const navLinks = [
            { href: 'dashboard.html', text: 'Dashboard', id: 'dashboard' },
            { href: 'subjects.html', text: 'Subjects', id: 'subjects' },
            { href: 'study-plan.html', text: 'Study Plan', id: 'study-plan' },
            { href: 'tasks.html', text: 'Tasks', id: 'tasks' },
            { href: 'progress.html', text: 'Progress', id: 'progress' },
            { href: 'ai-assistant.html', text: 'AI Assistant', id: 'ai-assistant' },
            { href: 'profile.html', text: 'Profile', id: 'profile' }
        ];

        const linksHtml = navLinks.map(link => `
            <li>
                <a href="${link.href}" class="nav-link ${activePage === link.id ? 'active' : ''}">
                    ${link.text}
                </a>
            </li>
        `).join('');

        navPlaceholder.innerHTML = `
            <div class="nav-container">
                <div style="display: flex; align-items: center; gap: 0.75rem;">
                    <button type="button" class="mobile-toggle-btn" id="mobileMenuBtn" aria-label="Toggle navigation menu">
                        ☰
                    </button>
                    <a href="dashboard.html" class="nav-brand">
                        <span>🎓 AI Study Planner</span>
                    </a>
                </div>

                <ul class="nav-links" id="navLinksList">
                    ${linksHtml}
                </ul>

                <div class="nav-actions">
                    <button type="button" id="themeToggleBtn" class="theme-toggle-btn">
                        <!-- Theme icon and label populated dynamically -->
                    </button>
                    <span style="font-size: 0.8125rem; font-weight: 600; color: var(--text-muted); display: inline-block; max-width: 110px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap;" title="${user ? user.email : ''}">
                        👤 ${user ? user.fullName.split(' ')[0] : 'Student'}
                    </span>
                    <button id="logoutBtn" class="btn btn-secondary btn-sm" title="Log Out">Logout</button>
                </div>
            </div>
        `;

        this.updateThemeButton();
        document.getElementById('themeToggleBtn')?.addEventListener('click', () => this.toggleTheme());

        // Mobile menu toggle
        const mobileBtn = document.getElementById('mobileMenuBtn');
        const linksList = document.getElementById('navLinksList');
        if (mobileBtn && linksList) {
            mobileBtn.addEventListener('click', () => {
                linksList.classList.toggle('mobile-open');
            });
        }

        const logoutBtn = document.getElementById('logoutBtn');
        if (logoutBtn) {
            logoutBtn.addEventListener('click', () => auth.logout());
        }
    },

    renderFooter() {
        const footer = document.getElementById('footer');
        if (!footer) return;
        footer.innerHTML = `
            <p>AI Study Planner &copy; ${new Date().getFullYear()} &bull; Built for college & university students</p>
        `;
    }
};
