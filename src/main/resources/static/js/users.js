// Load users when page loads
document.addEventListener('DOMContentLoaded', function() {
    loadUsers();
});

// Users list
let usersList = [];

// Load all users
async function loadUsers() {
    try {
        usersList = await API.users.getAll();
        renderUsers();
    } catch (error) {
        console.error('Error loading users:', error);
        showToast('Error loading users', 'danger');
    }
}

// Render users
function renderUsers() {
    const container = document.getElementById('usersContainer');
    
    if (usersList.length === 0) {
        container.innerHTML = `
            <div class="empty-state">
                <i class="fas fa-users"></i>
                <p>No users found</p>
            </div>
        `;
        return;
    }

    container.innerHTML = `
        <div class="row">
            ${usersList.map(user => createUserCard(user)).join('')}
        </div>
    `;
}

// Create user card
function createUserCard(user) {
    return `
        <div class="col-md-6 col-lg-4 mb-3">
            <div class="sub-card h-100">
                <div class="d-flex justify-content-between align-items-start mb-3">
                    <h5><i class="fas fa-user text-primary"></i> ${user.username}</h5>
                    <span class="badge bg-${user.role === 'ADMIN' ? 'danger' : 'primary'}">${user.role}</span>
                </div>
                <div class="text-start">
                    <p class="mb-2">
                        <strong><i class="fas fa-envelope"></i> Email:</strong><br>
                        ${user.email}
                    </p>
                    <p class="mb-3">
                        <strong><i class="fas fa-toggle-on"></i> Status:</strong><br>
                        <span class="badge bg-${user.enabled ? 'success' : 'secondary'}">${user.enabled ? 'Active' : 'Inactive'}</span>
                    </p>
                </div>
                <div class="d-grid gap-2">
                    <button class="btn btn-sm btn-info" onclick="editUserModal(${user.id})">
                        <i class="fas fa-edit"></i> Edit
                    </button>
                    <button class="btn btn-sm btn-danger" onclick="deleteUser(${user.id})">
                        <i class="fas fa-trash"></i> Delete
                    </button>
                </div>
            </div>
        </div>
    `;
}

// Add new user
async function addUser() {
    const data = {
        username: document.getElementById('username').value,
        email: document.getElementById('email').value,
        passwordHash: document.getElementById('password').value,
        role: document.getElementById('role').value,
        enabled: document.getElementById('enabled').checked
    };
    
    try {
        await API.users.create(data);
        showToast('User added successfully', 'success');
        bootstrap.Modal.getInstance(document.getElementById('addUserModal')).hide();
        document.getElementById('addUserForm').reset();
        loadUsers();
    } catch (error) {
        console.error('Error adding user:', error);
        showToast('Error adding user: ' + (error.message || 'Unknown error'), 'danger');
    }
}

// Show edit user modal
async function editUserModal(id) {
    try {
        const user = await API.users.getById(id);
        
        document.getElementById('editId').value = user.id;
        document.getElementById('editUsername').value = user.username;
        document.getElementById('editEmail').value = user.email;
        document.getElementById('editPassword').value = '';
        document.getElementById('editRole').value = user.role;
        document.getElementById('editEnabled').checked = user.enabled;
        
        new bootstrap.Modal(document.getElementById('editUserModal')).show();
    } catch (error) {
        console.error('Error loading user details:', error);
        showToast('Error loading user details', 'danger');
    }
}

// Update user
async function updateUser() {
    const id = document.getElementById('editId').value;
    const password = document.getElementById('editPassword').value;
    
    const data = {
        username: document.getElementById('editUsername').value,
        email: document.getElementById('editEmail').value,
        role: document.getElementById('editRole').value,
        enabled: document.getElementById('editEnabled').checked
    };
    
    // Only include password if it was changed
    if (password) {
        data.passwordHash = password;
    }
    
    try {
        await API.users.update(id, data);
        showToast('User updated successfully', 'success');
        bootstrap.Modal.getInstance(document.getElementById('editUserModal')).hide();
        loadUsers();
    } catch (error) {
        console.error('Error updating user:', error);
        showToast('Error updating user: ' + (error.message || 'Unknown error'), 'danger');
    }
}

// Delete user
async function deleteUser(id) {
    if (!confirm('Are you sure you want to delete this user?')) {
        return;
    }
    
    try {
        await API.users.delete(id);
        showToast('User deleted successfully', 'success');
        loadUsers();
    } catch (error) {
        console.error('Error deleting user:', error);
        showToast('Error deleting user: ' + (error.message || 'Unknown error'), 'danger');
    }
}

// Show toast notification
function showToast(message, type = 'info') {
    const toastElement = document.getElementById('toast');
    const toastBody = document.getElementById('toastMessage');
    
    toastBody.textContent = message;
    toastElement.className = `toast bg-${type} text-white`;
    
    const toast = new bootstrap.Toast(toastElement);
    toast.show();
}

