// Deployment Tasks JavaScript

let tasksList = [];
let vmList = [];
let usersList = [];
let currentFilter = 'ALL';

// Load data on page load
document.addEventListener('DOMContentLoaded', () => {
    loadTasks();
    loadDropdownData();
});

// Load all tasks
async function loadTasks() {
    try {
        tasksList = await API.deploymentTasks.getAll();
        vmList = await API.virtualMachines.getAll();
        usersList = await API.users.getAll();
        renderTasks();
    } catch (error) {
        console.error('Error loading tasks:', error);
        showToast('Error loading tasks', 'danger');
    }
}

// Load dropdown data
async function loadDropdownData() {
    try {
        const [vms, users] = await Promise.all([
            API.virtualMachines.getAll(),
            API.users.getAll()
        ]);
        
        // Populate VM dropdowns
        const vmSelect = document.getElementById('vmId');
        const editVmSelect = document.getElementById('editVmId');
        vms.forEach(vm => {
            const option = new Option(vm.name, vm.id);
            const editOption = new Option(vm.name, vm.id);
            vmSelect.add(option);
            editVmSelect.add(editOption.cloneNode(true));
        });
        
        // Populate user dropdowns
        const userSelect = document.getElementById('assignedUserId');
        const editUserSelect = document.getElementById('editAssignedUserId');
        users.forEach(user => {
            const option = new Option(user.username, user.id);
            const editOption = new Option(user.username, user.id);
            userSelect.add(option);
            editUserSelect.add(editOption.cloneNode(true));
        });
    } catch (error) {
        console.error('Error loading dropdown data:', error);
    }
}

// Filter tasks by status
function filterTasks(status) {
    currentFilter = status;
    
    // Update button states
    document.querySelectorAll('.btn-group button').forEach(btn => {
        btn.classList.remove('active');
    });
    event.target.classList.add('active');
    
    renderTasks();
}

// Render tasks table
function renderTasks() {
    const container = document.getElementById('tasksContainer');
    
    // Filter tasks
    let filteredTasks = currentFilter === 'ALL' 
        ? tasksList 
        : tasksList.filter(task => task.status === currentFilter);
    
    if (filteredTasks.length === 0) {
        container.innerHTML = `
            <div class="empty-state">
                <i class="fas fa-tasks"></i>
                <p>No deployment tasks found</p>
            </div>
        `;
        return;
    }

    // Sort by scheduled date (most recent first)
    filteredTasks.sort((a, b) => {
        const dateA = new Date(a.scheduledDate || a.createdAt);
        const dateB = new Date(b.scheduledDate || b.createdAt);
        return dateB - dateA;
    });

    container.innerHTML = `
        <table class="table table-hover">
            <thead>
                <tr>
                    <th>Task Name</th>
                    <th>VM</th>
                    <th>Assigned To</th>
                    <th>Status</th>
                    <th>Scheduled Date</th>
                    <th>Created</th>
                    <th>Actions</th>
                </tr>
            </thead>
            <tbody>
                ${filteredTasks.map(task => createTaskRow(task)).join('')}
            </tbody>
        </table>
    `;
}

// Create task row
function createTaskRow(task) {
    const vm = vmList.find(v => v.id === task.vmId);
    const user = usersList.find(u => u.id === task.assignedUserId);
    const statusClass = getStatusBadgeClass(task.status);
    
    return `
        <tr>
            <td>
                <strong>${task.taskName}</strong>
                ${task.description ? `<br><small class="text-muted">${task.description}</small>` : ''}
            </td>
            <td>${vm ? vm.name : 'N/A'}</td>
            <td>${user ? user.username : 'Unassigned'}</td>
            <td>
                <span class="status-badge ${statusClass}">
                    ${task.status}
                </span>
            </td>
            <td>${formatDateTime(task.scheduledDate)}</td>
            <td>${formatDateTime(task.createdAt)}</td>
            <td>
                <button class="btn btn-sm btn-primary" onclick="editTaskModal(${task.id})">
                    <i class="fas fa-edit"></i>
                </button>
            </td>
        </tr>
    `;
}

// Add task
async function addTask() {
    const scheduledDate = document.getElementById('scheduledDate').value;
    
    const data = {
        taskName: document.getElementById('taskName').value,
        description: document.getElementById('description').value,
        vmId: parseInt(document.getElementById('vmId').value),
        assignedUserId: document.getElementById('assignedUserId').value 
            ? parseInt(document.getElementById('assignedUserId').value) 
            : null,
        status: document.getElementById('status').value,
        scheduledDate: scheduledDate ? new Date(scheduledDate).toISOString() : null
    };

    try {
        await API.deploymentTasks.create(data);
        showToast('Task created successfully', 'success');
        bootstrap.Modal.getInstance(document.getElementById('addTaskModal')).hide();
        document.getElementById('addTaskForm').reset();
        loadTasks();
    } catch (error) {
        console.error('Error creating task:', error);
        showToast('Error creating task', 'danger');
    }
}

// Edit task modal
async function editTaskModal(id) {
    try {
        const task = await API.deploymentTasks.getById(id);
        
        document.getElementById('editId').value = task.id;
        document.getElementById('editTaskName').value = task.taskName;
        document.getElementById('editDescription').value = task.description || '';
        document.getElementById('editVmId').value = task.vmId;
        document.getElementById('editAssignedUserId').value = task.assignedUserId || '';
        document.getElementById('editStatus').value = task.status;
        
        // Format date for datetime-local input
        if (task.scheduledDate) {
            const date = new Date(task.scheduledDate);
            const formattedDate = date.toISOString().slice(0, 16);
            document.getElementById('editScheduledDate').value = formattedDate;
        } else {
            document.getElementById('editScheduledDate').value = '';
        }
        
        new bootstrap.Modal(document.getElementById('editTaskModal')).show();
    } catch (error) {
        console.error('Error loading task details:', error);
        showToast('Error loading task details', 'danger');
    }
}

// Update task
async function updateTask() {
    const id = document.getElementById('editId').value;
    const scheduledDate = document.getElementById('editScheduledDate').value;
    
    const data = {
        taskName: document.getElementById('editTaskName').value,
        description: document.getElementById('editDescription').value,
        vmId: parseInt(document.getElementById('editVmId').value),
        assignedUserId: document.getElementById('editAssignedUserId').value 
            ? parseInt(document.getElementById('editAssignedUserId').value) 
            : null,
        status: document.getElementById('editStatus').value,
        scheduledDate: scheduledDate ? new Date(scheduledDate).toISOString() : null
    };

    try {
        await API.deploymentTasks.update(id, data);
        showToast('Task updated successfully', 'success');
        bootstrap.Modal.getInstance(document.getElementById('editTaskModal')).hide();
        loadTasks();
    } catch (error) {
        console.error('Error updating task:', error);
        showToast('Error updating task', 'danger');
    }
}

// Delete task
async function deleteTask() {
    if (!confirm('Are you sure you want to delete this task?')) {
        return;
    }

    const id = document.getElementById('editId').value;

    try {
        await API.deploymentTasks.delete(id);
        showToast('Task deleted successfully', 'success');
        bootstrap.Modal.getInstance(document.getElementById('editTaskModal')).hide();
        loadTasks();
    } catch (error) {
        console.error('Error deleting task:', error);
        showToast('Error deleting task', 'danger');
    }
}

