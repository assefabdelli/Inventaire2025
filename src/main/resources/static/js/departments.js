// Department-related utilities

// Store current department selection
let currentDepartment = localStorage.getItem('selectedDepartment') || '';

// Get current department ID
function getCurrentDepartment() {
    return currentDepartment;
}

// Set current department
function setCurrentDepartment(deptId) {
    currentDepartment = deptId;
    localStorage.setItem('selectedDepartment', deptId);
}

// Load departments into a select element
async function loadDepartmentSelect(selectId, includeAll = true) {
    try {
        const departments = await API.get('/departments/active');
        const select = document.getElementById(selectId);
        
        if (!select) return;
        
        // Clear existing options except the first one if it exists
        select.innerHTML = '';
        
        if (includeAll) {
            const allOption = document.createElement('option');
            allOption.value = '';
            allOption.textContent = 'All Departments';
            select.appendChild(allOption);
        }
        
        departments.forEach(dept => {
            const option = document.createElement('option');
            option.value = dept.id;
            option.textContent = dept.name;
            select.appendChild(option);
        });
        
        // Set saved selection
        if (currentDepartment) {
            select.value = currentDepartment;
        }
        
        return departments;
    } catch (error) {
        console.error('Error loading departments:', error);
        return [];
    }
}

// Add department query parameter if set
function buildQueryWithDepartment(additionalParams = {}) {
    const params = new URLSearchParams();
    
    if (currentDepartment) {
        params.append('departmentId', currentDepartment);
    }
    
    Object.entries(additionalParams).forEach(([key, value]) => {
        if (value !== null && value !== undefined && value !== '') {
            params.append(key, value);
        }
    });
    
    const queryString = params.toString();
    return queryString ? `?${queryString}` : '';
}

// Create department filter UI element
function createDepartmentFilter(onChangeCallback) {
    const container = document.createElement('div');
    container.className = 'department-filter mb-3';
    container.innerHTML = `
        <label for="departmentFilterSelect" class="me-2"><i class="fas fa-sitemap"></i> Department:</label>
        <select id="departmentFilterSelect" class="form-select d-inline-block" style="width: auto;">
            <option value="">All Departments</option>
        </select>
    `;
    
    loadDepartmentSelect('departmentFilterSelect', true).then(() => {
        const select = document.getElementById('departmentFilterSelect');
        if (select) {
            select.addEventListener('change', (e) => {
                setCurrentDepartment(e.target.value);
                if (onChangeCallback) {
                    onChangeCallback();
                }
            });
        }
    });
    
    return container;
}

