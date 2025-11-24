# Cloud Inventory Management System

A comprehensive multi-tenant inventory management system for cloud infrastructure, built with Spring Boot and modern web technologies. This application enables organizations to manage hardware resources, virtual machines, sites, deployment tasks, and users across multiple departments with role-based access control.

## 🚀 Features

### Core Functionality
- **Hardware Management**: Track servers, storage, and network equipment
- **Virtual Machine Management**: Monitor and manage VMs across different hypervisors (KVM, Hyper-V, Oracle Linux VM)
- **Site Management**: Organize resources by physical locations
- **Deployment Task Management**: Track and schedule VM deployment tasks
- **User Management**: Comprehensive user administration with role-based access
- **Department Management**: Multi-tenant architecture supporting multiple departments

### Security & Access Control
- **Three-Tier Role System**:
  - **SUPER_ADMIN**: Full system access, can manage departments and users
  - **ADMIN**: Department-scoped access, manages resources within their department
  - **USER**: Read-only access to their department's resources
- **Department Isolation**: Each department's data is completely isolated
- **Secure Authentication**: BCrypt password hashing
- **Session Management**: Secure user sessions with role-based routing

### Modern UI
- **Dark Theme**: Professional dark mode design throughout
- **Responsive Layout**: Works on desktop, tablet, and mobile devices
- **Interactive Cards**: Beautiful flip cards with hover effects for hardware and VMs
- **Real-time Statistics**: Dashboard with live resource counts and status
- **Filter & Search**: Advanced filtering by department, type, and status

## 🛠 Technology Stack

### Backend
- **Java 17**
- **Spring Boot 3.5.6**
  - Spring Web
  - Spring Data JPA
  - Spring Security
- **MySQL 8.0** (Database)
- **Maven** (Build tool)

### Frontend
- **HTML5 / CSS3 / JavaScript (ES6+)**
- **Bootstrap 5** (UI Framework)
- **Font Awesome 6** (Icons)
- **Custom CSS** (Dark theme with gradients and animations)

## 📋 Prerequisites

- **Java Development Kit (JDK) 17** or higher
- **MySQL 8.0** or higher (XAMPP recommended for Windows)
- **Maven 3.6+** (included via Maven Wrapper)
- **Web Browser** (Chrome, Firefox, Edge - latest versions)

## 🚀 Quick Start

### 1. Database Setup

#### Option A: Using XAMPP (Recommended for Windows)

1. **Install and start XAMPP**
   - Download from: https://www.apachefriends.org/
   - Start Apache and MySQL services

2. **Create the database**
   - Open phpMyAdmin: `http://localhost/phpmyadmin`
   - Create database: `inventorydb`
   - Collation: `utf8mb4_general_ci`

3. **Import the data**
   - Select `inventorydb`
   - Click "Import" tab
   - Choose file: `database/import_xampp.sql`
   - Click "Go"

#### Option B: Using MySQL Command Line

```bash
# Create database
mysql -u root -p
CREATE DATABASE inventorydb CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
EXIT;

# Import data
cd database
mysql -u root -p inventorydb < import_xampp.sql
```

### 2. Configure Application

Edit `src/main/resources/application.properties` if needed:

```properties
# Database Configuration (default XAMPP settings)
spring.datasource.url=jdbc:mysql://localhost:3306/inventorydb
spring.datasource.username=root
spring.datasource.password=

# Server Configuration
server.port=8080
```

### 3. Build and Run

```bash
# Build the project
.\mvnw.cmd clean package -DskipTests

# Run the application
.\mvnw.cmd spring-boot:run
```

The application will start at: `http://localhost:8080`

### 4. Login

You'll be automatically redirected to the login page.

**Test Accounts:**

| Role | Username | Password | Access Level |
|------|----------|----------|--------------|
| Super Admin | `admin` | `password123` | Full system access |
| IT Admin | `admin_it` | `password123` | IT department only |
| Ops Admin | `admin_ops` | `password123` | Operations department only |
| Finance Admin | `admin_finance` | `password123` | Finance department only |
| IT User | `john.doe` | `password123` | IT department (read-only) |
| Ops User | `tom.anderson` | `password123` | Operations department (read-only) |
| Finance User | `william.lee` | `password123` | Finance department (read-only) |

## 📊 Database Schema

The system includes the following tables:

- **department**: Organizational departments
- **users**: System users with roles and department assignments
- **site**: Physical locations/data centers
- **hardware**: Physical servers and equipment
- **virtual_machine**: Virtual machines running on hardware
- **deployment_task**: VM deployment tasks and schedules

### Sample Data Included:
- 3 Departments (IT, Operations, Finance)
- 34 Users (1 super admin, 3 department admins, 30 regular users)
- 15 Sites (5 per department)
- 75 Hardware Servers (25 per department)
- 225 Virtual Machines (75 per department)
- 45 Deployment Tasks (15 per department)

## 🎨 User Interface

### Main Pages

1. **Dashboard** (`/index.html`)
   - Overview statistics (hardware, VMs, sites, tasks)
   - Department filter (SUPER_ADMIN only)
   - Recent deployment tasks
   - Quick navigation cards

2. **Hardware** (`/hardware.html`)
   - View all hardware servers
   - Filter by type (Server, Storage, Network)
   - Department filter (SUPER_ADMIN only)
   - Add/Edit/Delete hardware
   - Flip cards with detailed specs

3. **Virtual Machines** (`/virtual-machines.html`)
   - View all VMs
   - Filter by status (Running, Stopped, etc.)
   - Add/Edit/Delete VMs
   - Interactive cards with VM details

4. **Sites** (`/sites.html`)
   - Manage physical locations
   - Add/Edit/Delete sites
   - View site details (address, city, country)

5. **Deployment Tasks** (`/deployment-tasks.html`)
   - View all deployment tasks
   - Filter by status (Pending, In Progress, Completed, Failed)
   - Schedule new deployments
   - Track task progress

6. **Departments** (`/departments.html`) - *SUPER_ADMIN only*
   - Create/Edit/Delete departments
   - Manage department status
   - View department details

7. **Users** (`/users.html`) - *SUPER_ADMIN only*
   - Create/Edit/Delete users
   - Assign users to departments
   - Set user roles and permissions

## 🔒 Role-Based Access Control

### SUPER_ADMIN
- Full access to all features
- Can manage departments (CRUD)
- Can manage users (CRUD)
- Can view and manage resources across all departments
- Can see department filters on all pages

### ADMIN (Department Admin)
- Can view and manage resources within their assigned department only
- Cannot access Department or User management pages
- Cannot see other departments' data
- Can perform CRUD operations on department resources

### USER
- Read-only access to their department's resources
- Cannot create, edit, or delete any resources
- Cannot access Department or User management pages
- Cannot see other departments' data

## 🎯 Key Features Explained

### Multi-Tenant Architecture
Each department operates in complete isolation:
- Department admins only see their department's resources
- Users cannot access data from other departments
- SUPER_ADMIN has cross-department visibility

### Hardware Status Tracking
- **OPERATIONAL**: Fully functional and in use
- **MAINTENANCE**: Under maintenance
- **DOWN**: Not operational
- **DECOMMISSIONED**: Retired from service

### VM Hypervisor Support
- KVM (Kernel-based Virtual Machine)
- Hyper-V (Microsoft)
- Oracle Linux VM (OLVM)

### Deployment Task Workflow
1. Create deployment task
2. Assign to VM and user
3. Schedule deployment date
4. Track status: PENDING → IN_PROGRESS → COMPLETED/FAILED

## 🛠 Development

### Project Structure

```
Inventory-vm/
├── src/
│   ├── main/
│   │   ├── java/com/example/demo/
│   │   │   ├── config/           # Security configuration
│   │   │   ├── controller/       # REST API endpoints
│   │   │   ├── dto/              # Data Transfer Objects
│   │   │   ├── entite/           # JPA Entities
│   │   │   ├── enums/            # Enumerations (Role, Status, etc.)
│   │   │   ├── repository/       # Data access layer
│   │   │   └── service/          # Business logic
│   │   └── resources/
│   │       ├── application.properties  # Configuration
│   │       └── static/           # Frontend files
│   │           ├── css/          # Stylesheets
│   │           ├── js/           # JavaScript files
│   │           ├── images/       # Icons and images
│   │           └── *.html        # HTML pages
│   └── test/                     # Test files
├── database/                     # SQL scripts
│   ├── dummy_data.sql           # Main data with all tables
│   ├── import_xampp.sql         # Pre-configured for XAMPP
│   ├── fix_deployment_task_table.sql  # Quick fix script
│   └── update_role_to_super_admin.sql # Role migration
├── pom.xml                      # Maven configuration
└── README.md                    # This file
```

### Building from Source

```bash
# Clean build
.\mvnw.cmd clean

# Compile
.\mvnw.cmd compile

# Package (creates JAR)
.\mvnw.cmd package

# Run tests
.\mvnw.cmd test

# Skip tests during build
.\mvnw.cmd clean package -DskipTests
```

### Running in Production

```bash
# Build the JAR
.\mvnw.cmd clean package -DskipTests

# Run the JAR
java -jar target/Inventory_Vm-0.0.1-SNAPSHOT.jar
```

## 🐛 Troubleshooting

### Database Connection Issues

**Error: Access denied for user 'root'@'localhost'**
- Check MySQL is running in XAMPP
- Verify credentials in `application.properties`
- Default XAMPP password is empty

**Error: Unknown database 'inventorydb'**
- Create the database first: `CREATE DATABASE inventorydb;`
- Re-import the SQL file

**Error: #1067 - Invalid default value for 'scheduled_date'**
- Use the fixed SQL files provided
- Run `database/fix_deployment_task_table.sql`

### Login Issues

**Cannot login with admin/password123**
- Ensure database is imported correctly
- Check that password hash is BCrypt format
- Run: `SELECT username, role FROM users WHERE username='admin';`
- Should show: `admin | SUPER_ADMIN`

**Session expired error on every page**
- Clear browser sessionStorage: `sessionStorage.clear();`
- Hard refresh: `Ctrl + Shift + R`
- Re-login

### Permission Errors

**"Session expired or insufficient permissions" when accessing pages**
- Ensure you're logged in with the correct role
- SUPER_ADMIN: Full access
- ADMIN: Department-scoped access
- USER: Read-only access
- Check browser console for specific error messages

### Build Issues

**Error: JAVA_HOME not set**
```bash
# Windows
set JAVA_HOME=C:\Program Files\Java\jdk-17

# Add to PATH
set PATH=%JAVA_HOME%\bin;%PATH%
```

**Error: Port 8080 already in use**
- Change port in `application.properties`: `server.port=8081`
- Or stop the process using port 8080

## 🔄 Updating the Application

### Update User Role to SUPER_ADMIN

If you have an existing database:

```sql
UPDATE users 
SET role = 'SUPER_ADMIN' 
WHERE username = 'admin';
```

Or run: `database/update_role_to_super_admin.sql`

### Reset Database

```bash
# Drop and recreate
mysql -u root -p
DROP DATABASE inventorydb;
CREATE DATABASE inventorydb;
EXIT;

# Re-import
mysql -u root -p inventorydb < database/import_xampp.sql
```

## 📝 API Endpoints

### Authentication
- `POST /api/auth/login` - User login
- `POST /api/auth/logout` - User logout
- `GET /api/auth/me` - Get current user info

### Departments (SUPER_ADMIN only)
- `GET /api/departments` - List all departments
- `GET /api/departments/active` - List active departments
- `POST /api/departments` - Create department
- `PUT /api/departments/{id}` - Update department
- `DELETE /api/departments/{id}` - Delete department

### Users (CRUD: SUPER_ADMIN only, View: All users)
- `GET /api/users` - List users (filtered by department for non-super-admins)
- `POST /api/users` - Create user (SUPER_ADMIN only)
- `PUT /api/users/{id}` - Update user (SUPER_ADMIN only)
- `DELETE /api/users/{id}` - Delete user (SUPER_ADMIN only)

### Hardware
- `GET /api/hardware` - List hardware (filtered by department)
- `POST /api/hardware` - Create hardware
- `PUT /api/hardware/{id}` - Update hardware
- `DELETE /api/hardware/{id}` - Delete hardware

### Virtual Machines
- `GET /api/virtual-machines` - List VMs (filtered by department)
- `POST /api/virtual-machines` - Create VM
- `PUT /api/virtual-machines/{id}` - Update VM
- `DELETE /api/virtual-machines/{id}` - Delete VM

### Sites
- `GET /api/sites` - List sites (filtered by department)
- `POST /api/sites` - Create site
- `PUT /api/sites/{id}` - Update site
- `DELETE /api/sites/{id}` - Delete site

### Deployment Tasks
- `GET /api/deployment-tasks` - List tasks (filtered by department)
- `POST /api/deployment-tasks` - Create task
- `PUT /api/deployment-tasks/{id}` - Update task
- `DELETE /api/deployment-tasks/{id}` - Delete task

## 🎨 Customization

### Changing Colors

Edit `src/main/resources/static/css/style.css`:

```css
/* Main background gradient */
body {
    background: linear-gradient(135deg, #1a1a2e 0%, #16213e 50%, #0f3460 100%);
}

/* Card gradients */
.card .front {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

/* Button gradients */
.btn-primary {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}
```

### Adding a New Role

1. Add to `src/main/java/com/example/demo/enums/Role.java`
2. Update `UserContextService.java` with new role checks
3. Modify controllers to enforce permissions
4. Update frontend JavaScript for UI restrictions

## 📄 License

This project is provided as-is for educational and commercial use.

## 👥 Credits

Developed as a comprehensive inventory management solution for cloud infrastructure.

## 📞 Support

For issues or questions:
1. Check the Troubleshooting section above
2. Review database import logs
3. Check Spring Boot console for detailed error messages
4. Verify browser console (F12) for frontend errors

## 🔮 Future Enhancements

- Export reports to PDF/Excel
- Email notifications for deployment tasks
- Resource utilization charts
- Automated backups
- API documentation with Swagger
- Mobile app
- Multi-language support
- Advanced analytics dashboard

---

**Version:** 1.0.0  
**Last Updated:** November 2024  
**Framework:** Spring Boot 3.5.6  
**Database:** MySQL 8.0

