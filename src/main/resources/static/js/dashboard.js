// Dashboard JavaScript

let stats = {
    hardware: 0,
    vms: 0,
    sites: 0,
    tasks: 0
};

// Load dashboard data on page load
document.addEventListener('DOMContentLoaded', () => {
    // Check authentication
    if (!checkAuth()) return;
    
    // Update nav user info
    updateNavUserInfo();
    
    // Hide restricted nav items based on role
    hideRestrictedNavItems();
    
    // Show department filter only for super admins
    const user = getCurrentUser();
    if (user && user.role === 'SUPER_ADMIN') {
        const filterContainer = document.getElementById('departmentFilterContainer');
        if (filterContainer) filterContainer.style.display = 'block';
    }
    
    // Load data
    loadDepartments();
    loadDashboardData();
});

// Load departments for filter
async function loadDepartments() {
    if (!isSuperAdmin()) return; // Only load for super admins
    
    try {
        const departments = await API.get('/departments/active');
        const select = document.getElementById('departmentFilter');
        departments.forEach(dept => {
            const option = document.createElement('option');
            option.value = dept.id;
            option.textContent = dept.name;
            select.appendChild(option);
        });
    } catch (error) {
        console.error('Error loading departments:', error);
    }
}

// Load all dashboard data
function loadDashboardData() {
    loadStatistics();
    loadRecentTasks();
}

// Load statistics
async function loadStatistics() {
    try {
        const departmentId = document.getElementById('departmentFilter')?.value || '';
        const query = departmentId ? `?departmentId=${departmentId}` : '';
        
        const [hardware, vms, sites, tasks] = await Promise.all([
            API.get(`/hardware${query}`),
            API.get(`/virtual-machines${query}`),
            API.get(`/sites${query}`),
            API.get(`/deployment-tasks${query}`)
        ]);

        stats.hardware = hardware.length;
        stats.vms = vms.length;
        stats.sites = sites.length;
        stats.tasks = tasks.filter(t => t.status === 'PENDING' || t.status === 'IN_PROGRESS').length;

        updateStatistics();
    } catch (error) {
        console.error('Error loading statistics:', error);
        if (typeof showToast !== 'undefined') {
            showToast('Error loading statistics', 'danger');
        }
    }
}

// Update statistics display
function updateStatistics() {
    document.getElementById('totalHardware').textContent = stats.hardware;
    document.getElementById('totalVMs').textContent = stats.vms;
    document.getElementById('totalSites').textContent = stats.sites;
    document.getElementById('activeTasks').textContent = stats.tasks;
}

// Load recent deployment tasks
async function loadRecentTasks() {
    try {
        const departmentId = document.getElementById('departmentFilter')?.value || '';
        const query = departmentId ? `?departmentId=${departmentId}` : '';
        const tasks = await API.get(`/deployment-tasks${query}`);
        const container = document.getElementById('recentTasks');
        
        if (tasks.length === 0) {
            container.innerHTML = `
                <div class="empty-state">
                    <i class="fas fa-inbox"></i>
                    <p>No deployment tasks yet</p>
                </div>
            `;
            return;
        }

        // Sort by created date and take the 5 most recent
        const recentTasks = tasks
            .sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt))
            .slice(0, 5);

        container.innerHTML = `
            <table class="table table-hover">
                <thead>
                    <tr>
                        <th>Task Name</th>
                        <th>Status</th>
                        <th>Scheduled Date</th>
                        <th>Created</th>
                    </tr>
                </thead>
                <tbody>
                    ${recentTasks.map(task => `
                        <tr>
                            <td>${task.taskName}</td>
                            <td>
                                <span class="status-badge ${getStatusBadgeClass(task.status)}">
                                    ${task.status}
                                </span>
                            </td>
                            <td>${formatDateTime(task.scheduledDate)}</td>
                            <td>${formatDateTime(task.createdAt)}</td>
                        </tr>
                    `).join('')}
                </tbody>
            </table>
        `;
    } catch (error) {
        console.error('Error loading recent tasks:', error);
        const container = document.getElementById('recentTasks');
        container.innerHTML = `
            <div class="alert alert-danger">
                Error loading recent tasks
            </div>
        `;
    }
}

