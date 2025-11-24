// Check authentication
if (!checkAuth()) {
    window.location.href = 'login.html';
}

// Load data on page load
document.addEventListener('DOMContentLoaded', function() {
    // Update nav user info
    updateNavUserInfo();
    hideRestrictedNavItems();
    
    loadDepartments();
    loadUsers();
    
    // Show Add User button and department filter only for super admins
    if (isSuperAdmin()) {
        document.getElementById('addUserBtn').style.display = 'inline-block';
        document.getElementById('departmentFilterContainer').style.display = 'block';
        loadDepartmentFilter();
    } else {
        // Redirect non-super-admins away from the users page
        window.location.href = 'index.html';
    }
});

// Load all users
async function loadUsers() {
    try {
        const departmentId = document.getElementById('departmentFilter')?.value || '';
        const query = departmentId ? `?departmentId=${departmentId}` : '';
        const users = await API.get(`/users${query}`);
        
        const container = document.getElementById('usersContainer');
        
        if (users.length === 0) {
            container.innerHTML = `
                <div class="empty-state">
                    <i class="fas fa-users fa-3x"></i>
                    <p>No users found</p>
                </div>
            `;
            return;
        }
        
        container.innerHTML = `
            <table class="table table-hover">
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>Username</th>
                        <th>Email</th>
                        <th>Department</th>
                        <th>Role</th>
                        <th>Status</th>
                        <th>Actions</th>
                    </tr>
                </thead>
                <tbody>
                    ${users.map(user => `
                        <tr>
                            <td>${user.id}</td>
                            <td><i class="fas fa-user me-2"></i>${user.username}</td>
                            <td>${user.email}</td>
                            <td>
                                <i class="fas fa-sitemap me-1"></i>
                                <span id="dept-${user.id}">Loading...</span>
                            </td>
                            <td>
                                <span class="badge ${user.role === 'ADMIN' ? 'bg-danger' : 'bg-primary'}">
                                    <i class="fas ${user.role === 'ADMIN' ? 'fa-user-shield' : 'fa-user'}"></i>
                                    ${user.role}
                                </span>
                            </td>
                            <td>
                                <span class="status-badge ${user.enabled ? 'status-active' : 'status-inactive'}">
                                    ${user.enabled ? 'Active' : 'Inactive'}
                                </span>
                            </td>
                            <td>
                                <button class="btn btn-sm btn-primary me-1" onclick="openEditModal(${user.id})">
                                    <i class="fas fa-edit"></i> Edit
                                </button>
                                <button class="btn btn-sm btn-danger" onclick="deleteUser(${user.id})">
                                    <i class="fas fa-trash"></i> Delete
                                </button>
                            </td>
                        </tr>
                    `).join('')}
                </tbody>
            </table>
        `;
        
        // Load department names
        users.forEach(async user => {
            if (user.departmentId) {
                try {
                    const dept = await API.get(`/departments/${user.departmentId}`);
                    document.getElementById(`dept-${user.id}`).textContent = dept.name;
                } catch (error) {
                    document.getElementById(`dept-${user.id}`).textContent = 'Unknown';
                }
            } else {
                document.getElementById(`dept-${user.id}`).textContent = '-';
            }
        });
    } catch (error) {
        console.error('Error loading users:', error);
        const container = document.getElementById('usersContainer');
        container.innerHTML = `
            <div class="alert alert-danger">
                <i class="fas fa-exclamation-triangle"></i> Error loading users
            </div>
        `;
    }
}

// Load departments for filter
async function loadDepartmentFilter() {
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

// Load departments for dropdowns in modals
async function loadDepartments() {
    try {
        const departments = await API.get('/departments/active');
        
        // Populate add form
        const addSelect = document.getElementById('departmentId');
        addSelect.innerHTML = '<option value="">Select Department</option>';
        departments.forEach(dept => {
            const option = document.createElement('option');
            option.value = dept.id;
            option.textContent = dept.name;
            addSelect.appendChild(option);
        });
        
        // Populate edit form
        const editSelect = document.getElementById('editDepartmentId');
        editSelect.innerHTML = '<option value="">Select Department</option>';
        departments.forEach(dept => {
            const option = document.createElement('option');
            option.value = dept.id;
            option.textContent = dept.name;
            editSelect.appendChild(option);
        });
    } catch (error) {
        console.error('Error loading departments:', error);
    }
}

// Save new user
async function saveUser() {
    const data = {
        username: document.getElementById('username').value,
        email: document.getElementById('email').value,
        passwordHash: document.getElementById('password').value,
        departmentId: parseInt(document.getElementById('departmentId').value),
        role: document.getElementById('role').value,
        enabled: document.getElementById('enabled').checked
    };
    
    if (!data.departmentId) {
        showToast('Please select a department', 'danger');
        return;
    }
    
    try {
        await API.post('/users', data);
        bootstrap.Modal.getInstance(document.getElementById('addUserModal')).hide();
        document.getElementById('addUserForm').reset();
        showToast('User created successfully!', 'success');
        loadUsers();
    } catch (error) {
        console.error('Error creating user:', error);
        showToast('Error creating user', 'danger');
    }
}

// Open edit modal
async function openEditModal(id) {
    try {
        const user = await API.get(`/users/${id}`);
        
        document.getElementById('editId').value = user.id;
        document.getElementById('editUsername').value = user.username;
        document.getElementById('editEmail').value = user.email;
        document.getElementById('editPassword').value = '';
        document.getElementById('editDepartmentId').value = user.departmentId || '';
        document.getElementById('editRole').value = user.role;
        document.getElementById('editEnabled').checked = user.enabled;
        
        new bootstrap.Modal(document.getElementById('editUserModal')).show();
    } catch (error) {
        console.error('Error loading user:', error);
        showToast('Error loading user', 'danger');
    }
}

// Update user
async function updateUser() {
    const id = document.getElementById('editId').value;
    const password = document.getElementById('editPassword').value;
    
    const data = {
        username: document.getElementById('editUsername').value,
        email: document.getElementById('editEmail').value,
        departmentId: parseInt(document.getElementById('editDepartmentId').value),
        role: document.getElementById('editRole').value,
        enabled: document.getElementById('editEnabled').checked
    };
    
    if (!data.departmentId) {
        showToast('Please select a department', 'danger');
        return;
    }
    
    // Only include password if it was changed
    if (password) {
        data.passwordHash = password;
    }
    
    try {
        await API.put(`/users/${id}`, data);
        bootstrap.Modal.getInstance(document.getElementById('editUserModal')).hide();
        showToast('User updated successfully!', 'success');
        loadUsers();
    } catch (error) {
        console.error('Error updating user:', error);
        showToast('Error updating user', 'danger');
    }
}

// Delete user
async function deleteUser(id) {
    if (!confirm('Are you sure you want to delete this user?')) {
        return;
    }
    
    try {
        await API.delete(`/users/${id}`);
        showToast('User deleted successfully!', 'success');
        loadUsers();
    } catch (error) {
        console.error('Error deleting user:', error);
        showToast('Error deleting user', 'danger');
    }
}
