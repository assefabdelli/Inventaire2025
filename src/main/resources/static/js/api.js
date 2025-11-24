// API Base URL
const API_BASE_URL = '/api';

// Authentication Helper Functions
function getCurrentUser() {
    const userStr = sessionStorage.getItem('currentUser');
    return userStr ? JSON.parse(userStr) : null;
}

function isLoggedIn() {
    return getCurrentUser() !== null;
}

function isAdmin() {
    const user = getCurrentUser();
    return user && (user.role === 'ADMIN' || user.role === 'SUPER_ADMIN');
}

function isSuperAdmin() {
    const user = getCurrentUser();
    return user && user.role === 'SUPER_ADMIN';
}

function checkAuth() {
    if (!isLoggedIn() && !window.location.pathname.includes('login.html')) {
        window.location.href = 'login.html';
        return false;
    }
    return true;
}

function logout() {
    sessionStorage.removeItem('currentUser');
    window.location.href = 'login.html';
}

// Update navigation with user info
function updateNavUserInfo() {
    const user = getCurrentUser();
    if (user) {
        const usernameEl = document.getElementById('navUsername');
        const deptEl = document.getElementById('navDepartment');
        const roleEl = document.getElementById('navRole');
        
        if (usernameEl) usernameEl.textContent = user.username || 'User';
        if (deptEl) deptEl.textContent = user.departmentName || 'No Department';
        if (roleEl) roleEl.textContent = user.role || 'USER';
    }
}

// Hide navigation items based on role
function hideRestrictedNavItems() {
    if (!isSuperAdmin()) {
        // Hide Departments and Users nav items for non-super-admins
        const deptNav = document.getElementById('nav-departments');
        const usersNav = document.getElementById('nav-users');
        
        if (deptNav) deptNav.style.display = 'none';
        if (usersNav) usersNav.style.display = 'none';
    }
}

function getAuthHeaders() {
    const user = getCurrentUser();
    const headers = {
        'Content-Type': 'application/json'
    };
    if (user) {
        headers['X-User-Id'] = user.userId;
    }
    return headers;
}

// Generic API request helper
async function apiRequest(url, options = {}) {
    const user = getCurrentUser();
    const headers = options.headers || {};
    
    if (user) {
        headers['X-User-Id'] = user.userId;
    }
    
    if (options.body && !headers['Content-Type']) {
        headers['Content-Type'] = 'application/json';
    }
    
    const response = await fetch(url, {
        ...options,
        headers
    });
    
    if (response.status === 401 || response.status === 403) {
        // Unauthorized or Forbidden - redirect to login
        if (!window.location.pathname.includes('login.html')) {
            alert('Session expired or insufficient permissions. Please login again.');
            logout();
        }
        throw new Error('Unauthorized');
    }
    
    return response;
}

// API Helper Functions
const API = {
    // Generic methods
    get: async (endpoint) => {
        const response = await apiRequest(`${API_BASE_URL}${endpoint}`);
        return response.json();
    },
    post: async (endpoint, data) => {
        const response = await apiRequest(`${API_BASE_URL}${endpoint}`, {
            method: 'POST',
            body: JSON.stringify(data)
        });
        return response.json();
    },
    put: async (endpoint, data) => {
        const response = await apiRequest(`${API_BASE_URL}${endpoint}`, {
            method: 'PUT',
            body: JSON.stringify(data)
        });
        return response.json();
    },
    delete: async (endpoint) => {
        return apiRequest(`${API_BASE_URL}${endpoint}`, {
            method: 'DELETE'
        });
    },
    // Hardware APIs (legacy - use generic methods above)
    hardware: {
        getAll: () => API.get('/hardware'),
        getById: (id) => API.get(`/hardware/${id}`),
        create: (data) => API.post('/hardware', data),
        update: (id, data) => API.put(`/hardware/${id}`, data),
        delete: (id) => API.delete(`/hardware/${id}`)
    },

    // Virtual Machine APIs
    virtualMachines: {
        getAll: () => API.get('/virtual-machines'),
        getById: (id) => API.get(`/virtual-machines/${id}`),
        create: (data) => API.post('/virtual-machines', data),
        update: (id, data) => API.put(`/virtual-machines/${id}`, data),
        delete: (id) => API.delete(`/virtual-machines/${id}`)
    },

    // Site APIs
    sites: {
        getAll: () => API.get('/sites'),
        getById: (id) => API.get(`/sites/${id}`),
        create: (data) => API.post('/sites', data),
        update: (id, data) => API.put(`/sites/${id}`, data),
        delete: (id) => API.delete(`/sites/${id}`)
    },

    // Deployment Task APIs
    deploymentTasks: {
        getAll: () => API.get('/deployment-tasks'),
        getById: (id) => API.get(`/deployment-tasks/${id}`),
        create: (data) => API.post('/deployment-tasks', data),
        update: (id, data) => API.put(`/deployment-tasks/${id}`, data),
        delete: (id) => API.delete(`/deployment-tasks/${id}`)
    },

    // User APIs
    users: {
        getAll: () => API.get('/users'),
        getById: (id) => API.get(`/users/${id}`)
    }
};

// Utility Functions
function showToast(message, type = 'success') {
    const toast = document.createElement('div');
    toast.className = `alert alert-${type} position-fixed top-0 end-0 m-3`;
    toast.style.zIndex = '9999';
    toast.textContent = message;
    document.body.appendChild(toast);
    
    setTimeout(() => {
        toast.remove();
    }, 3000);
}

function getStatusBadgeClass(status) {
    const statusMap = {
        // Hardware statuses
        'OPERATIONAL': 'status-active',
        'MAINTENANCE': 'status-maintenance',
        'DOWN': 'status-inactive',
        'DECOMMISSIONED': 'status-failed',
        // General statuses
        'ACTIVE': 'status-active',
        'INACTIVE': 'status-inactive',
        // VM statuses
        'RUNNING': 'status-active',
        'STOPPED': 'status-inactive',
        'SUSPENDED': 'status-maintenance',
        // Deployment task statuses
        'PENDING': 'status-pending',
        'IN_PROGRESS': 'status-maintenance',
        'COMPLETED': 'status-completed',
        'FAILED': 'status-failed'
    };
    return statusMap[status] || 'status-pending';
}

function getTypeImage(type) {
    const imageMap = {
        'SERVER': 'images/srv.png',
        'STORAGE': 'images/storage.png',
        'NETWORK': 'images/icons8-server-64.png'
    };
    return imageMap[type] || 'images/srv.png';
}

function formatDateTime(dateString) {
    if (!dateString) return 'N/A';
    const date = new Date(dateString);
    return date.toLocaleString();
}

function formatDate(dateString) {
    if (!dateString) return 'N/A';
    const date = new Date(dateString);
    return date.toLocaleDateString();
}

