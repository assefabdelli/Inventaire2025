// API Base URL
const API_BASE_URL = '/api';

// API Helper Functions
const API = {
    // Hardware APIs
    hardware: {
        getAll: () => fetch(`${API_BASE_URL}/hardware`).then(res => res.json()),
        getById: (id) => fetch(`${API_BASE_URL}/hardware/${id}`).then(res => res.json()),
        create: (data) => fetch(`${API_BASE_URL}/hardware`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(data)
        }).then(res => res.json()),
        update: (id, data) => fetch(`${API_BASE_URL}/hardware/${id}`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(data)
        }).then(res => res.json()),
        delete: (id) => fetch(`${API_BASE_URL}/hardware/${id}`, {
            method: 'DELETE'
        })
    },

    // Virtual Machine APIs
    virtualMachines: {
        getAll: () => fetch(`${API_BASE_URL}/virtual-machines`).then(res => res.json()),
        getById: (id) => fetch(`${API_BASE_URL}/virtual-machines/${id}`).then(res => res.json()),
        create: (data) => fetch(`${API_BASE_URL}/virtual-machines`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(data)
        }).then(res => res.json()),
        update: (id, data) => fetch(`${API_BASE_URL}/virtual-machines/${id}`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(data)
        }).then(res => res.json()),
        delete: (id) => fetch(`${API_BASE_URL}/virtual-machines/${id}`, {
            method: 'DELETE'
        })
    },

    // Site APIs
    sites: {
        getAll: () => fetch(`${API_BASE_URL}/sites`).then(res => res.json()),
        getById: (id) => fetch(`${API_BASE_URL}/sites/${id}`).then(res => res.json()),
        create: (data) => fetch(`${API_BASE_URL}/sites`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(data)
        }).then(res => res.json()),
        update: (id, data) => fetch(`${API_BASE_URL}/sites/${id}`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(data)
        }).then(res => res.json()),
        delete: (id) => fetch(`${API_BASE_URL}/sites/${id}`, {
            method: 'DELETE'
        })
    },

    // Deployment Task APIs
    deploymentTasks: {
        getAll: () => fetch(`${API_BASE_URL}/deployment-tasks`).then(res => res.json()),
        getById: (id) => fetch(`${API_BASE_URL}/deployment-tasks/${id}`).then(res => res.json()),
        create: (data) => fetch(`${API_BASE_URL}/deployment-tasks`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(data)
        }).then(res => res.json()),
        update: (id, data) => fetch(`${API_BASE_URL}/deployment-tasks/${id}`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(data)
        }).then(res => res.json()),
        delete: (id) => fetch(`${API_BASE_URL}/deployment-tasks/${id}`, {
            method: 'DELETE'
        })
    },

    // User APIs
    users: {
        getAll: () => fetch(`${API_BASE_URL}/users`).then(res => res.json()),
        getById: (id) => fetch(`${API_BASE_URL}/users/${id}`).then(res => res.json())
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
        'ACTIVE': 'status-active',
        'INACTIVE': 'status-inactive',
        'MAINTENANCE': 'status-maintenance',
        'RUNNING': 'status-active',
        'STOPPED': 'status-inactive',
        'SUSPENDED': 'status-maintenance',
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

