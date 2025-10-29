// Dashboard JavaScript

let stats = {
    hardware: 0,
    vms: 0,
    sites: 0,
    tasks: 0
};

// Load dashboard data on page load
document.addEventListener('DOMContentLoaded', () => {
    loadStatistics();
    loadRecentTasks();
});

// Load statistics
async function loadStatistics() {
    try {
        const [hardware, vms, sites, tasks] = await Promise.all([
            API.hardware.getAll(),
            API.virtualMachines.getAll(),
            API.sites.getAll(),
            API.deploymentTasks.getAll()
        ]);

        stats.hardware = hardware.length;
        stats.vms = vms.length;
        stats.sites = sites.length;
        stats.tasks = tasks.filter(t => t.status === 'PENDING' || t.status === 'IN_PROGRESS').length;

        updateStatistics();
    } catch (error) {
        console.error('Error loading statistics:', error);
        showToast('Error loading statistics', 'danger');
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
        const tasks = await API.deploymentTasks.getAll();
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

