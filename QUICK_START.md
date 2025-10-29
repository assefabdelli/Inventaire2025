# Quick Start Guide - Inventory VM Management System

## 🚀 What Was Created

A complete **frontend interface** for your Inventory VM Management Spring Boot application with:

### ✨ Features
- **Dashboard** - Real-time statistics and overview
- **Hardware Management** - Animated flip cards for servers, storage, and network equipment
- **Virtual Machines** - Beautiful VM cards with status indicators
- **Sites Management** - Data center location management
- **Deployment Tasks** - Task tracking and assignment system

### 🎨 Design Elements
- Modern, responsive UI using Bootstrap 5
- Animated 3D flip cards (inspired by your reference files)
- Color-coded status badges
- Mobile-friendly navigation
- Professional icons and imagery

## 📁 Files Created

```
src/main/resources/static/
├── index.html                    ✅ Dashboard
├── hardware.html                 ✅ Hardware management
├── virtual-machines.html         ✅ VM management
├── sites.html                    ✅ Sites management
├── deployment-tasks.html         ✅ Deployment tasks
├── css/
│   └── style.css                 ✅ Styling with animations
├── js/
│   ├── api.js                    ✅ API helper functions
│   ├── dashboard.js              ✅ Dashboard logic
│   ├── hardware.js               ✅ Hardware CRUD operations
│   ├── virtual-machines.js       ✅ VM CRUD operations
│   ├── sites.js                  ✅ Sites CRUD operations
│   └── deployment-tasks.js       ✅ Tasks CRUD operations
└── images/                       ✅ All reference images copied
```

## 🏃 How to Run

### Step 1: Ensure Database is Running
Make sure MySQL is running with the database configuration in `application.properties`:
```properties
Database: inventorydb
User: root
Password: NouveauMotDePasse!
```

### Step 2: Start the Application
```bash
# Navigate to project directory
cd C:\Users\ridha\Desktop\baha2\InvM\Inventory-vm

# Run with Maven wrapper
.\mvnw.cmd spring-boot:run
```

### Step 3: Access the Frontend
Open your browser and go to:
```
http://localhost:8080
```

## 📖 Usage Guide

### First Time Setup (Recommended Order)

1. **Create Sites First**
   - Go to Sites page
   - Click "Add Site"
   - Enter site details (name, code, location, contact)
   - Save

2. **Add Hardware**
   - Go to Hardware page
   - Click "Add Hardware"
   - Select site, type (Server/Storage/Network)
   - Enter specifications (Model, CPU, RAM, Storage, IP)
   - Save

3. **Create Virtual Machines**
   - Go to Virtual Machines page
   - Click "Add VM"
   - Select host hardware
   - Enter VM details (name, OS, vCPU, vRAM)
   - Save

4. **Create Deployment Tasks**
   - Go to Deployments page
   - Click "Create Task"
   - Select VM and assign to user
   - Set status and schedule date
   - Save

## 🎯 Key Features

### Hardware Cards
- **Hover to flip** - See detailed specs on front, image on back
- **Filter by type** - Server, Storage, Network
- **Grouped by site** - Easy organization
- **Status indicators** - Active, Inactive, Maintenance

### Virtual Machines
- **Animated cards** - Beautiful presentation
- **Filter by status** - Running, Stopped, Suspended
- **Grouped by host** - See which VMs are on which hardware
- **Full details** - OS, vCPU, vRAM, disk size

### Deployment Tasks
- **Table view** - Easy to scan and manage
- **Filter by status** - Pending, In Progress, Completed, Failed
- **Assignment** - Link to VMs and users
- **Scheduling** - Set deployment dates

## 🎨 UI Elements

### Status Colors
- 🟢 **Green** - Active, Running, Completed
- 🟠 **Orange** - Maintenance, Suspended, In Progress
- 🔴 **Red** - Inactive, Failed
- 🔵 **Blue** - Pending

### Navigation
- **Top navbar** - Always visible
- **Quick actions** - From dashboard
- **Breadcrumbs** - Easy navigation

## 🔧 API Integration

All frontend pages connect to your Spring Boot REST APIs:
- `GET /api/hardware` - List all hardware
- `POST /api/hardware` - Create hardware
- `PUT /api/hardware/{id}` - Update hardware
- `DELETE /api/hardware/{id}` - Delete hardware

Similar endpoints for VMs, Sites, and Deployment Tasks.

## 💡 Tips

1. **Use the dashboard** to get a quick overview of your infrastructure
2. **Filter options** make it easy to find specific items
3. **Hover over cards** to see animations and details
4. **Forms validate** input before submission
5. **Toast notifications** confirm actions

## 🎭 Visual Design

The design incorporates:
- **Your reference styles** from IaaS-EO.html
- **Animated flip cards** from the reference
- **Modern color scheme** with Bootstrap 5
- **Professional icons** from Font Awesome
- **Your images** from the Architecture project

## 🔐 Security Note

Current configuration allows all requests (development mode).
For production, update `SecurityConfig.java` to add authentication.

## 📱 Responsive Design

The interface works on:
- 💻 Desktop computers
- 📱 Tablets
- 📱 Mobile phones
- 🖥️ Large displays

## 🐛 Troubleshooting

### Pages not loading?
- Check if backend is running on port 8080
- Verify MySQL database is accessible
- Look at browser console for errors

### Images not showing?
- Images are in `static/images/`
- Clear browser cache
- Check file paths in HTML

### API errors?
- Ensure controllers are working
- Check database connection
- Verify entity relationships

## 🚀 Next Steps

1. **Test the application** - Create some sample data
2. **Customize colors** - Edit `style.css` to match your brand
3. **Add authentication** - Secure the endpoints
4. **Deploy** - Move to production environment

## 📚 Additional Documentation

- See `FRONTEND_README.md` for detailed technical documentation
- Check Spring Boot controllers for API specifications
- Review entity classes for data structure

---

**Enjoy your new Inventory VM Management System! 🎉**

