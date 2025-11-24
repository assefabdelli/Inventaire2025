-- ====================================================================
-- COMPLETE DATABASE SETUP WITH DUMMY DATA
-- Multi-Tenant Inventory Management System
-- Creates tables and loads test data
-- ====================================================================

-- Drop existing tables (in correct order to handle foreign keys)
DROP TABLE IF EXISTS deployment_task;
DROP TABLE IF EXISTS virtual_machine;
DROP TABLE IF EXISTS hardware;
DROP TABLE IF EXISTS site;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS department;

-- ====================================================================
-- CREATE TABLES
-- ====================================================================

-- Department Table
CREATE TABLE department (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    description VARCHAR(500),
    active BOOLEAN NOT NULL DEFAULT TRUE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Users Table
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    role VARCHAR(20) NOT NULL,
    department_id BIGINT NOT NULL,
    FOREIGN KEY (department_id) REFERENCES department(id) ON DELETE RESTRICT,
    INDEX idx_username (username),
    INDEX idx_email (email),
    INDEX idx_department (department_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Site Table
CREATE TABLE site (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    address VARCHAR(255) NOT NULL,
    city VARCHAR(255) NOT NULL,
    country VARCHAR(255) NOT NULL,
    department_id BIGINT NOT NULL,
    FOREIGN KEY (department_id) REFERENCES department(id) ON DELETE RESTRICT,
    INDEX idx_department (department_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Hardware Table
CREATE TABLE hardware (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    type VARCHAR(30) NOT NULL,
    model VARCHAR(255) NOT NULL,
    serial_number VARCHAR(255) NOT NULL UNIQUE,
    ip_address VARCHAR(50),
    cpu_cores INT,
    ram_gb INT,
    storage_gb INT,
    status VARCHAR(30) NOT NULL,
    purchase_date DATE NOT NULL,
    warranty_end_date DATE NOT NULL,
    site_id BIGINT NOT NULL,
    department_id BIGINT NOT NULL,
    FOREIGN KEY (site_id) REFERENCES site(id) ON DELETE RESTRICT,
    FOREIGN KEY (department_id) REFERENCES department(id) ON DELETE RESTRICT,
    INDEX idx_type (type),
    INDEX idx_status (status),
    INDEX idx_site (site_id),
    INDEX idx_department (department_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Virtual Machine Table
CREATE TABLE virtual_machine (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    hostname VARCHAR(255) NOT NULL,
    ip_address VARCHAR(50),
    operating_system VARCHAR(255) NOT NULL,
    vcpu INT NOT NULL,
    vram INT NOT NULL,
    disk_size INT NOT NULL,
    status VARCHAR(30) NOT NULL,
    hardware_id BIGINT NOT NULL,
    department_id BIGINT NOT NULL,
    FOREIGN KEY (hardware_id) REFERENCES hardware(id) ON DELETE RESTRICT,
    FOREIGN KEY (department_id) REFERENCES department(id) ON DELETE RESTRICT,
    INDEX idx_status (status),
    INDEX idx_hardware (hardware_id),
    INDEX idx_department (department_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Deployment Task Table
CREATE TABLE deployment_task (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    task_name VARCHAR(255) NOT NULL,
    description VARCHAR(1000),
    vm_id BIGINT NOT NULL,
    requested_by BIGINT NOT NULL,
    status VARCHAR(30) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    scheduled_date DATETIME DEFAULT NULL,
    completed_at DATETIME DEFAULT NULL,
    department_id BIGINT NOT NULL,
    FOREIGN KEY (vm_id) REFERENCES virtual_machine(id) ON DELETE RESTRICT,
    FOREIGN KEY (requested_by) REFERENCES users(id) ON DELETE RESTRICT,
    FOREIGN KEY (department_id) REFERENCES department(id) ON DELETE RESTRICT,
    INDEX idx_status (status),
    INDEX idx_vm (vm_id),
    INDEX idx_user (requested_by),
    INDEX idx_department (department_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ====================================================================
-- INSERT DATA
-- ====================================================================

-- ====================================================================
-- 1. DEPARTMENTS (3 departments as required)
-- ====================================================================

INSERT INTO department (name, description, active) VALUES 
('IT Department', 'Information Technology and Infrastructure Management', true),
('Operations Department', 'Business Operations, Logistics and Supply Chain', true),
('Finance Department', 'Finance, Accounting and Budget Management', true);

-- ====================================================================
-- 2. USERS (Admins and Regular Users for each department)
-- ====================================================================

-- Password for all users: "password123"
-- BCrypt hash: $2a$10$ifKWhrmNcyisiOWv7K9m7OYH18mD3q3uhsPfmfON0fDg3dAKGtgFm

-- Super Admin (Global administrator - can manage all departments and users)
INSERT INTO users (username, email, password_hash, enabled, role, department_id) VALUES
('admin', 'admin@company.com', '$2a$10$ifKWhrmNcyisiOWv7K9m7OYH18mD3q3uhsPfmfON0fDg3dAKGtgFm', true, 'SUPER_ADMIN', 1);

-- Department Admins (Can manage resources within their department only)
INSERT INTO users (username, email, password_hash, enabled, role, department_id) VALUES
('admin_it', 'admin.it@company.com', '$2a$10$ifKWhrmNcyisiOWv7K9m7OYH18mD3q3uhsPfmfON0fDg3dAKGtgFm', true, 'ADMIN', 1),
('admin_ops', 'admin.ops@company.com', '$2a$10$ifKWhrmNcyisiOWv7K9m7OYH18mD3q3uhsPfmfON0fDg3dAKGtgFm', true, 'ADMIN', 2),
('admin_finance', 'admin.finance@company.com', '$2a$10$ifKWhrmNcyisiOWv7K9m7OYH18mD3q3uhsPfmfON0fDg3dAKGtgFm', true, 'ADMIN', 3);

-- IT Department Users (10 users)
INSERT INTO users (username, email, password_hash, enabled, role, department_id) VALUES
('john.doe', 'john.doe@company.com', '$2a$10$ifKWhrmNcyisiOWv7K9m7OYH18mD3q3uhsPfmfON0fDg3dAKGtgFm', true, 'USER', 1),
('jane.smith', 'jane.smith@company.com', '$2a$10$ifKWhrmNcyisiOWv7K9m7OYH18mD3q3uhsPfmfON0fDg3dAKGtgFm', true, 'USER', 1),
('mike.johnson', 'mike.johnson@company.com', '$2a$10$ifKWhrmNcyisiOWv7K9m7OYH18mD3q3uhsPfmfON0fDg3dAKGtgFm', true, 'USER', 1),
('sarah.williams', 'sarah.williams@company.com', '$2a$10$ifKWhrmNcyisiOWv7K9m7OYH18mD3q3uhsPfmfON0fDg3dAKGtgFm', true, 'USER', 1),
('david.brown', 'david.brown@company.com', '$2a$10$ifKWhrmNcyisiOWv7K9m7OYH18mD3q3uhsPfmfON0fDg3dAKGtgFm', true, 'USER', 1),
('emily.jones', 'emily.jones@company.com', '$2a$10$ifKWhrmNcyisiOWv7K9m7OYH18mD3q3uhsPfmfON0fDg3dAKGtgFm', true, 'USER', 1),
('chris.garcia', 'chris.garcia@company.com', '$2a$10$ifKWhrmNcyisiOWv7K9m7OYH18mD3q3uhsPfmfON0fDg3dAKGtgFm', true, 'USER', 1),
('amanda.martinez', 'amanda.martinez@company.com', '$2a$10$ifKWhrmNcyisiOWv7K9m7OYH18mD3q3uhsPfmfON0fDg3dAKGtgFm', true, 'USER', 1),
('robert.rodriguez', 'robert.rodriguez@company.com', '$2a$10$ifKWhrmNcyisiOWv7K9m7OYH18mD3q3uhsPfmfON0fDg3dAKGtgFm', true, 'USER', 1),
('lisa.wilson', 'lisa.wilson@company.com', '$2a$10$ifKWhrmNcyisiOWv7K9m7OYH18mD3q3uhsPfmfON0fDg3dAKGtgFm', true, 'USER', 1);

-- Operations Department Users (10 users)
INSERT INTO users (username, email, password_hash, enabled, role, department_id) VALUES
('tom.anderson', 'tom.anderson@company.com', '$2a$10$ifKWhrmNcyisiOWv7K9m7OYH18mD3q3uhsPfmfON0fDg3dAKGtgFm', true, 'USER', 2),
('patricia.thomas', 'patricia.thomas@company.com', '$2a$10$ifKWhrmNcyisiOWv7K9m7OYH18mD3q3uhsPfmfON0fDg3dAKGtgFm', true, 'USER', 2),
('james.taylor', 'james.taylor@company.com', '$2a$10$ifKWhrmNcyisiOWv7K9m7OYH18mD3q3uhsPfmfON0fDg3dAKGtgFm', true, 'USER', 2),
('mary.moore', 'mary.moore@company.com', '$2a$10$ifKWhrmNcyisiOWv7K9m7OYH18mD3q3uhsPfmfON0fDg3dAKGtgFm', true, 'USER', 2),
('charles.jackson', 'charles.jackson@company.com', '$2a$10$ifKWhrmNcyisiOWv7K9m7OYH18mD3q3uhsPfmfON0fDg3dAKGtgFm', true, 'USER', 2),
('jennifer.white', 'jennifer.white@company.com', '$2a$10$ifKWhrmNcyisiOWv7K9m7OYH18mD3q3uhsPfmfON0fDg3dAKGtgFm', true, 'USER', 2),
('daniel.harris', 'daniel.harris@company.com', '$2a$10$ifKWhrmNcyisiOWv7K9m7OYH18mD3q3uhsPfmfON0fDg3dAKGtgFm', true, 'USER', 2),
('linda.martin', 'linda.martin@company.com', '$2a$10$ifKWhrmNcyisiOWv7K9m7OYH18mD3q3uhsPfmfON0fDg3dAKGtgFm', true, 'USER', 2),
('matthew.thompson', 'matthew.thompson@company.com', '$2a$10$ifKWhrmNcyisiOWv7K9m7OYH18mD3q3uhsPfmfON0fDg3dAKGtgFm', true, 'USER', 2),
('barbara.garcia', 'barbara.garcia@company.com', '$2a$10$ifKWhrmNcyisiOWv7K9m7OYH18mD3q3uhsPfmfON0fDg3dAKGtgFm', true, 'USER', 2);

-- Finance Department Users (10 users)
INSERT INTO users (username, email, password_hash, enabled, role, department_id) VALUES
('william.lee', 'william.lee@company.com', '$2a$10$ifKWhrmNcyisiOWv7K9m7OYH18mD3q3uhsPfmfON0fDg3dAKGtgFm', true, 'USER', 3),
('susan.perez', 'susan.perez@company.com', '$2a$10$ifKWhrmNcyisiOWv7K9m7OYH18mD3q3uhsPfmfON0fDg3dAKGtgFm', true, 'USER', 3),
('joseph.roberts', 'joseph.roberts@company.com', '$2a$10$ifKWhrmNcyisiOWv7K9m7OYH18mD3q3uhsPfmfON0fDg3dAKGtgFm', true, 'USER', 3),
('karen.turner', 'karen.turner@company.com', '$2a$10$ifKWhrmNcyisiOWv7K9m7OYH18mD3q3uhsPfmfON0fDg3dAKGtgFm', true, 'USER', 3),
('thomas.phillips', 'thomas.phillips@company.com', '$2a$10$ifKWhrmNcyisiOWv7K9m7OYH18mD3q3uhsPfmfON0fDg3dAKGtgFm', true, 'USER', 3),
('nancy.campbell', 'nancy.campbell@company.com', '$2a$10$ifKWhrmNcyisiOWv7K9m7OYH18mD3q3uhsPfmfON0fDg3dAKGtgFm', true, 'USER', 3),
('christopher.parker', 'christopher.parker@company.com', '$2a$10$ifKWhrmNcyisiOWv7K9m7OYH18mD3q3uhsPfmfON0fDg3dAKGtgFm', true, 'USER', 3),
('betty.evans', 'betty.evans@company.com', '$2a$10$ifKWhrmNcyisiOWv7K9m7OYH18mD3q3uhsPfmfON0fDg3dAKGtgFm', true, 'USER', 3),
('anthony.edwards', 'anthony.edwards@company.com', '$2a$10$ifKWhrmNcyisiOWv7K9m7OYH18mD3q3uhsPfmfON0fDg3dAKGtgFm', true, 'USER', 3),
('sandra.collins', 'sandra.collins@company.com', '$2a$10$ifKWhrmNcyisiOWv7K9m7OYH18mD3q3uhsPfmfON0fDg3dAKGtgFm', true, 'USER', 3);

-- ====================================================================
-- 3. SITES (5 sites per department = 15 total)
-- ====================================================================

-- IT Department Sites
INSERT INTO site (name, address, city, country, department_id) VALUES
('IT Headquarters', '100 Technology Drive', 'San Francisco', 'USA', 1),
('IT Data Center East', '250 Server Boulevard', 'New York', 'USA', 1),
('IT Data Center West', '350 Cloud Avenue', 'Seattle', 'USA', 1),
('IT Research Lab', '450 Innovation Street', 'Austin', 'USA', 1),
('IT Support Center', '550 Help Desk Road', 'Denver', 'USA', 1);

-- Operations Department Sites
INSERT INTO site (name, address, city, country, department_id) VALUES
('Operations Headquarters', '200 Business Parkway', 'Chicago', 'USA', 2),
('Warehouse North', '300 Logistics Lane', 'Minneapolis', 'USA', 2),
('Warehouse South', '400 Distribution Drive', 'Atlanta', 'USA', 2),
('Operations Center East', '500 Supply Chain Street', 'Philadelphia', 'USA', 2),
('Operations Center West', '600 Fulfillment Avenue', 'Phoenix', 'USA', 2);

-- Finance Department Sites
INSERT INTO site (name, address, city, country, department_id) VALUES
('Finance Headquarters', '300 Financial Plaza', 'Boston', 'USA', 3),
('Accounting Office', '400 Ledger Lane', 'Miami', 'USA', 3),
('Treasury Center', '500 Capital Drive', 'Dallas', 'USA', 3),
('Audit Office', '600 Compliance Court', 'Houston', 'USA', 3),
('Budget Planning Center', '700 Fiscal Avenue', 'Detroit', 'USA', 3);

-- ====================================================================
-- 4. HARDWARE (20 items per department = 60 total)
-- ====================================================================

-- IT Department Hardware (20 items)
INSERT INTO hardware (name, type, model, serial_number, ip_address, cpu_cores, ram_gb, storage_gb, status, purchase_date, warranty_end_date, site_id, department_id) VALUES
-- Servers
('IT-SRV-001', 'SERVER', 'Dell PowerEdge R740', 'IT-SRV-001-2024', '192.168.1.10', 32, 256, 4000, 'OPERATIONAL', '2024-01-15', '2027-01-15', 1, 1),
('IT-SRV-002', 'SERVER', 'HP ProLiant DL380 Gen10', 'IT-SRV-002-2024', '192.168.1.11', 28, 192, 3000, 'OPERATIONAL', '2024-01-20', '2027-01-20', 1, 1),
('IT-SRV-003', 'SERVER', 'Dell PowerEdge R640', 'IT-SRV-003-2024', '192.168.1.12', 24, 128, 2000, 'OPERATIONAL', '2024-02-01', '2027-02-01', 1, 1),
('IT-SRV-004', 'SERVER', 'Cisco UCS C240 M5', 'IT-SRV-004-2024', '192.168.1.13', 40, 384, 5000, 'OPERATIONAL', '2024-02-15', '2027-02-15', 2, 1),
('IT-SRV-005', 'SERVER', 'HPE Apollo 6500', 'IT-SRV-005-2024', '192.168.1.14', 48, 512, 8000, 'OPERATIONAL', '2024-03-01', '2027-03-01', 2, 1),
('IT-SRV-006', 'SERVER', 'Dell PowerEdge R740xd', 'IT-SRV-006-2024', '192.168.1.15', 32, 256, 6000, 'MAINTENANCE', '2024-03-15', '2027-03-15', 2, 1),
('IT-SRV-007', 'SERVER', 'Lenovo ThinkSystem SR650', 'IT-SRV-007-2024', '192.168.1.16', 36, 320, 4500, 'OPERATIONAL', '2024-04-01', '2027-04-01', 3, 1),
('IT-SRV-008', 'SERVER', 'Dell PowerEdge R840', 'IT-SRV-008-2024', '192.168.1.17', 56, 768, 10000, 'OPERATIONAL', '2024-04-15', '2027-04-15', 3, 1),
-- Storage Systems
('IT-STR-001', 'STORAGE', 'NetApp FAS8200', 'IT-STR-001-2024', '192.168.1.20', 8, 64, 50000, 'OPERATIONAL', '2024-01-10', '2027-01-10', 1, 1),
('IT-STR-002', 'STORAGE', 'Dell EMC Unity 450F', 'IT-STR-002-2024', '192.168.1.21', 12, 96, 75000, 'OPERATIONAL', '2024-02-01', '2027-02-01', 1, 1),
('IT-STR-003', 'STORAGE', 'HPE 3PAR 8440', 'IT-STR-003-2024', '192.168.1.22', 16, 128, 100000, 'OPERATIONAL', '2024-03-01', '2027-03-01', 2, 1),
('IT-STR-004', 'STORAGE', 'Pure Storage FlashArray//X', 'IT-STR-004-2024', '192.168.1.23', 10, 80, 60000, 'OPERATIONAL', '2024-04-01', '2027-04-01', 2, 1),
('IT-STR-005', 'STORAGE', 'NetApp AFF A400', 'IT-STR-005-2024', '192.168.1.24', 14, 112, 80000, 'MAINTENANCE', '2024-05-01', '2027-05-01', 3, 1),
-- Network Equipment
('IT-NET-001', 'NETWORK', 'Cisco Nexus 9372PX', 'IT-NET-001-2024', '192.168.1.30', 4, 16, 500, 'OPERATIONAL', '2024-01-05', '2027-01-05', 1, 1),
('IT-NET-002', 'NETWORK', 'Juniper QFX5200', 'IT-NET-002-2024', '192.168.1.31', 4, 16, 500, 'OPERATIONAL', '2024-02-05', '2027-02-05', 2, 1),
('IT-NET-003', 'NETWORK', 'Arista 7280R', 'IT-NET-003-2024', '192.168.1.32', 4, 16, 500, 'OPERATIONAL', '2024-03-05', '2027-03-05', 3, 1),
('IT-NET-004', 'NETWORK', 'Cisco Catalyst 9500', 'IT-NET-004-2024', '192.168.1.33', 4, 16, 500, 'OPERATIONAL', '2024-04-05', '2027-04-05', 4, 1),
('IT-NET-005', 'NETWORK', 'HPE FlexFabric 5945', 'IT-NET-005-2024', '192.168.1.34', 4, 16, 500, 'DECOMMISSIONED', '2023-06-01', '2026-06-01', 5, 1),
('IT-SRV-009', 'SERVER', 'IBM Power System S922', 'IT-SRV-009-2024', '192.168.1.18', 20, 256, 3000, 'OPERATIONAL', '2024-05-01', '2027-05-01', 4, 1),
('IT-SRV-010', 'SERVER', 'Oracle SPARC M8', 'IT-SRV-010-2024', '192.168.1.19', 32, 512, 5000, 'OPERATIONAL', '2024-05-15', '2027-05-15', 5, 1);

-- Operations Department Hardware (20 items)
INSERT INTO hardware (name, type, model, serial_number, ip_address, cpu_cores, ram_gb, storage_gb, status, purchase_date, warranty_end_date, site_id, department_id) VALUES
-- Servers
('OPS-SRV-001', 'SERVER', 'Dell PowerEdge R740', 'OPS-SRV-001-2024', '192.168.2.10', 24, 128, 2000, 'OPERATIONAL', '2024-01-10', '2027-01-10', 6, 2),
('OPS-SRV-002', 'SERVER', 'HP ProLiant DL360 Gen10', 'OPS-SRV-002-2024', '192.168.2.11', 20, 96, 1500, 'OPERATIONAL', '2024-01-25', '2027-01-25', 6, 2),
('OPS-SRV-003', 'SERVER', 'Dell PowerEdge R640', 'OPS-SRV-003-2024', '192.168.2.12', 16, 64, 1000, 'OPERATIONAL', '2024-02-10', '2027-02-10', 6, 2),
('OPS-SRV-004', 'SERVER', 'Lenovo ThinkSystem SR630', 'OPS-SRV-004-2024', '192.168.2.13', 24, 128, 2000, 'OPERATIONAL', '2024-02-20', '2027-02-20', 7, 2),
('OPS-SRV-005', 'SERVER', 'HPE ProLiant DL380', 'OPS-SRV-005-2024', '192.168.2.14', 28, 192, 3000, 'MAINTENANCE', '2024-03-05', '2027-03-05', 7, 2),
('OPS-SRV-006', 'SERVER', 'Dell PowerEdge R740xd', 'OPS-SRV-006-2024', '192.168.2.15', 32, 256, 4000, 'OPERATIONAL', '2024-03-20', '2027-03-20', 8, 2),
('OPS-SRV-007', 'SERVER', 'Cisco UCS C220 M5', 'OPS-SRV-007-2024', '192.168.2.16', 20, 128, 2000, 'OPERATIONAL', '2024-04-10', '2027-04-10', 8, 2),
('OPS-SRV-008', 'SERVER', 'HPE Apollo 4200', 'OPS-SRV-008-2024', '192.168.2.17', 24, 192, 3000, 'OPERATIONAL', '2024-04-25', '2027-04-25', 9, 2),
-- Storage Systems
('OPS-STR-001', 'STORAGE', 'NetApp FAS2750', 'OPS-STR-001-2024', '192.168.2.20', 8, 48, 30000, 'OPERATIONAL', '2024-01-15', '2027-01-15', 6, 2),
('OPS-STR-002', 'STORAGE', 'Dell EMC Unity 380', 'OPS-STR-002-2024', '192.168.2.21', 10, 64, 40000, 'OPERATIONAL', '2024-02-15', '2027-02-15', 7, 2),
('OPS-STR-003', 'STORAGE', 'HPE MSA 2050', 'OPS-STR-003-2024', '192.168.2.22', 6, 32, 25000, 'OPERATIONAL', '2024-03-15', '2027-03-15', 8, 2),
('OPS-STR-004', 'STORAGE', 'Synology RS4021xs+', 'OPS-STR-004-2024', '192.168.2.23', 8, 64, 50000, 'OPERATIONAL', '2024-04-15', '2027-04-15', 9, 2),
('OPS-STR-005', 'STORAGE', 'QNAP TS-h1290FX', 'OPS-STR-005-2024', '192.168.2.24', 12, 96, 60000, 'OPERATIONAL', '2024-05-15', '2027-05-15', 10, 2),
-- Network Equipment
('OPS-NET-001', 'NETWORK', 'Cisco Catalyst 3850', 'OPS-NET-001-2024', '192.168.2.30', 4, 8, 250, 'OPERATIONAL', '2024-01-05', '2027-01-05', 6, 2),
('OPS-NET-002', 'NETWORK', 'HPE Aruba 2930M', 'OPS-NET-002-2024', '192.168.2.31', 4, 8, 250, 'OPERATIONAL', '2024-02-05', '2027-02-05', 7, 2),
('OPS-NET-003', 'NETWORK', 'Juniper EX4300', 'OPS-NET-003-2024', '192.168.2.32', 4, 8, 250, 'OPERATIONAL', '2024-03-05', '2027-03-05', 8, 2),
('OPS-NET-004', 'NETWORK', 'Dell N3248TE-ON', 'OPS-NET-004-2024', '192.168.2.33', 4, 8, 250, 'OPERATIONAL', '2024-04-05', '2027-04-05', 9, 2),
('OPS-NET-005', 'NETWORK', 'Netgear M4300-52G', 'OPS-NET-005-2024', '192.168.2.34', 4, 8, 250, 'OPERATIONAL', '2024-05-05', '2027-05-05', 10, 2),
('OPS-SRV-009', 'SERVER', 'Dell PowerEdge T640', 'OPS-SRV-009-2024', '192.168.2.18', 16, 96, 1500, 'OPERATIONAL', '2024-05-10', '2027-05-10', 9, 2),
('OPS-SRV-010', 'SERVER', 'HPE ProLiant ML350', 'OPS-SRV-010-2024', '192.168.2.19', 20, 128, 2000, 'OPERATIONAL', '2024-05-25', '2027-05-25', 10, 2);

-- Finance Department Hardware (20 items)
INSERT INTO hardware (name, type, model, serial_number, ip_address, cpu_cores, ram_gb, storage_gb, status, purchase_date, warranty_end_date, site_id, department_id) VALUES
-- Servers
('FIN-SRV-001', 'SERVER', 'Dell PowerEdge R740', 'FIN-SRV-001-2024', '192.168.3.10', 20, 128, 2000, 'OPERATIONAL', '2024-01-12', '2027-01-12', 11, 3),
('FIN-SRV-002', 'SERVER', 'HP ProLiant DL380', 'FIN-SRV-002-2024', '192.168.3.11', 24, 192, 3000, 'OPERATIONAL', '2024-02-12', '2027-02-12', 11, 3),
('FIN-SRV-003', 'SERVER', 'Dell PowerEdge R640', 'FIN-SRV-003-2024', '192.168.3.12', 16, 96, 1500, 'OPERATIONAL', '2024-03-12', '2027-03-12', 11, 3),
('FIN-SRV-004', 'SERVER', 'Lenovo ThinkSystem SR650', 'FIN-SRV-004-2024', '192.168.3.13', 28, 256, 4000, 'OPERATIONAL', '2024-04-12', '2027-04-12', 12, 3),
('FIN-SRV-005', 'SERVER', 'HPE ProLiant DL360', 'FIN-SRV-005-2024', '192.168.3.14', 20, 128, 2000, 'OPERATIONAL', '2024-05-12', '2027-05-12', 12, 3),
('FIN-SRV-006', 'SERVER', 'Cisco UCS C240', 'FIN-SRV-006-2024', '192.168.3.15', 32, 384, 5000, 'MAINTENANCE', '2024-01-20', '2027-01-20', 13, 3),
('FIN-SRV-007', 'SERVER', 'Dell PowerEdge R740xd', 'FIN-SRV-007-2024', '192.168.3.16', 24, 192, 3000, 'OPERATIONAL', '2024-02-20', '2027-02-20', 13, 3),
('FIN-SRV-008', 'SERVER', 'HPE Apollo 4510', 'FIN-SRV-008-2024', '192.168.3.17', 20, 128, 2500, 'OPERATIONAL', '2024-03-20', '2027-03-20', 14, 3),
-- Storage Systems
('FIN-STR-001', 'STORAGE', 'NetApp FAS2720', 'FIN-STR-001-2024', '192.168.3.20', 8, 64, 35000, 'OPERATIONAL', '2024-01-18', '2027-01-18', 11, 3),
('FIN-STR-002', 'STORAGE', 'Dell EMC Unity 350F', 'FIN-STR-002-2024', '192.168.3.21', 10, 80, 45000, 'OPERATIONAL', '2024-02-18', '2027-02-18', 11, 3),
('FIN-STR-003', 'STORAGE', 'HPE 3PAR 8200', 'FIN-STR-003-2024', '192.168.3.22', 12, 96, 55000, 'OPERATIONAL', '2024-03-18', '2027-03-18', 12, 3),
('FIN-STR-004', 'STORAGE', 'Pure Storage FlashArray//C', 'FIN-STR-004-2024', '192.168.3.23', 8, 64, 40000, 'OPERATIONAL', '2024-04-18', '2027-04-18', 13, 3),
('FIN-STR-005', 'STORAGE', 'NetApp AFF A250', 'FIN-STR-005-2024', '192.168.3.24', 10, 80, 50000, 'OPERATIONAL', '2024-05-18', '2027-05-18', 14, 3),
-- Network Equipment
('FIN-NET-001', 'NETWORK', 'Cisco Catalyst 3650', 'FIN-NET-001-2024', '192.168.3.30', 4, 8, 250, 'OPERATIONAL', '2024-01-08', '2027-01-08', 11, 3),
('FIN-NET-002', 'NETWORK', 'HPE Aruba 2540', 'FIN-NET-002-2024', '192.168.3.31', 4, 8, 250, 'OPERATIONAL', '2024-02-08', '2027-02-08', 12, 3),
('FIN-NET-003', 'NETWORK', 'Juniper EX2300', 'FIN-NET-003-2024', '192.168.3.32', 4, 8, 250, 'OPERATIONAL', '2024-03-08', '2027-03-08', 13, 3),
('FIN-NET-004', 'NETWORK', 'Dell N2248PX-ON', 'FIN-NET-004-2024', '192.168.3.33', 4, 8, 250, 'OPERATIONAL', '2024-04-08', '2027-04-08', 14, 3),
('FIN-NET-005', 'NETWORK', 'Cisco Meraki MS225', 'FIN-NET-005-2024', '192.168.3.34', 4, 8, 250, 'OPERATIONAL', '2024-05-08', '2027-05-08', 15, 3),
('FIN-SRV-009', 'SERVER', 'Dell PowerEdge T440', 'FIN-SRV-009-2024', '192.168.3.18', 16, 96, 1500, 'OPERATIONAL', '2024-04-20', '2027-04-20', 14, 3),
('FIN-SRV-010', 'SERVER', 'HPE ProLiant ML110', 'FIN-SRV-010-2024', '192.168.3.19', 12, 64, 1000, 'OPERATIONAL', '2024-05-20', '2027-05-20', 15, 3);

-- ====================================================================
-- 5. VIRTUAL MACHINES (30 VMs per department = 90 total)
-- ====================================================================

-- IT Department VMs (30 VMs)
INSERT INTO virtual_machine (name, hostname, ip_address, operating_system, vcpu, vram, disk_size, status, hardware_id, department_id) VALUES
-- Web Servers
('IT-WEB-01', 'web01.it.company.com', '10.1.1.10', 'Ubuntu 22.04 LTS', 4, 8, 100, 'RUNNING', 1, 1),
('IT-WEB-02', 'web02.it.company.com', '10.1.1.11', 'Ubuntu 22.04 LTS', 4, 8, 100, 'RUNNING', 1, 1),
('IT-WEB-03', 'web03.it.company.com', '10.1.1.12', 'Ubuntu 22.04 LTS', 4, 8, 100, 'RUNNING', 2, 1),
('IT-WEB-04', 'web04.it.company.com', '10.1.1.13', 'CentOS 8', 4, 8, 100, 'RUNNING', 2, 1),
-- Database Servers
('IT-DB-01', 'db01.it.company.com', '10.1.2.10', 'Ubuntu 22.04 LTS', 8, 32, 500, 'RUNNING', 3, 1),
('IT-DB-02', 'db02.it.company.com', '10.1.2.11', 'Ubuntu 22.04 LTS', 8, 32, 500, 'RUNNING', 3, 1),
('IT-DB-03', 'db03.it.company.com', '10.1.2.12', 'Red Hat Enterprise Linux 8', 16, 64, 1000, 'RUNNING', 4, 1),
('IT-DB-04', 'db04.it.company.com', '10.1.2.13', 'PostgreSQL on Ubuntu', 12, 48, 750, 'STOPPED', 4, 1),
-- Application Servers
('IT-APP-01', 'app01.it.company.com', '10.1.3.10', 'Windows Server 2022', 8, 16, 250, 'RUNNING', 5, 1),
('IT-APP-02', 'app02.it.company.com', '10.1.3.11', 'Windows Server 2022', 8, 16, 250, 'RUNNING', 5, 1),
('IT-APP-03', 'app03.it.company.com', '10.1.3.12', 'Ubuntu 22.04 LTS', 6, 12, 200, 'RUNNING', 6, 1),
('IT-APP-04', 'app04.it.company.com', '10.1.3.13', 'Ubuntu 22.04 LTS', 6, 12, 200, 'RUNNING', 6, 1),
-- Docker/Kubernetes Nodes
('IT-K8S-MASTER-01', 'k8s-master01.it.company.com', '10.1.4.10', 'Ubuntu 22.04 LTS', 4, 8, 150, 'RUNNING', 7, 1),
('IT-K8S-NODE-01', 'k8s-node01.it.company.com', '10.1.4.11', 'Ubuntu 22.04 LTS', 8, 16, 200, 'RUNNING', 7, 1),
('IT-K8S-NODE-02', 'k8s-node02.it.company.com', '10.1.4.12', 'Ubuntu 22.04 LTS', 8, 16, 200, 'RUNNING', 7, 1),
('IT-K8S-NODE-03', 'k8s-node03.it.company.com', '10.1.4.13', 'Ubuntu 22.04 LTS', 8, 16, 200, 'RUNNING', 8, 1),
-- Monitoring & Management
('IT-MON-01', 'monitoring01.it.company.com', '10.1.5.10', 'Ubuntu 22.04 LTS', 4, 8, 150, 'RUNNING', 1, 1),
('IT-LOG-01', 'logging01.it.company.com', '10.1.5.11', 'Ubuntu 22.04 LTS', 4, 8, 500, 'RUNNING', 2, 1),
('IT-BACKUP-01', 'backup01.it.company.com', '10.1.5.12', 'Ubuntu 22.04 LTS', 4, 8, 1000, 'RUNNING', 3, 1),
-- Development/Testing
('IT-DEV-01', 'dev01.it.company.com', '10.1.6.10', 'Ubuntu 22.04 LTS', 6, 12, 200, 'RUNNING', 4, 1),
('IT-DEV-02', 'dev02.it.company.com', '10.1.6.11', 'Windows Server 2022', 6, 12, 200, 'STOPPED', 5, 1),
('IT-TEST-01', 'test01.it.company.com', '10.1.6.12', 'Ubuntu 22.04 LTS', 4, 8, 150, 'RUNNING', 6, 1),
('IT-TEST-02', 'test02.it.company.com', '10.1.6.13', 'CentOS 8', 4, 8, 150, 'STOPPED', 7, 1),
-- Security & Network
('IT-FW-01', 'firewall01.it.company.com', '10.1.7.10', 'pfSense 2.7', 4, 8, 100, 'RUNNING', 1, 1),
('IT-VPN-01', 'vpn01.it.company.com', '10.1.7.11', 'Ubuntu 22.04 LTS', 2, 4, 50, 'RUNNING', 2, 1),
('IT-PROXY-01', 'proxy01.it.company.com', '10.1.7.12', 'Ubuntu 22.04 LTS', 4, 8, 100, 'RUNNING', 3, 1),
('IT-DNS-01', 'dns01.it.company.com', '10.1.7.13', 'Ubuntu 22.04 LTS', 2, 4, 50, 'RUNNING', 4, 1),
('IT-MAIL-01', 'mail01.it.company.com', '10.1.8.10', 'Ubuntu 22.04 LTS', 6, 12, 300, 'RUNNING', 5, 1),
('IT-LDAP-01', 'ldap01.it.company.com', '10.1.8.11', 'Ubuntu 22.04 LTS', 4, 8, 100, 'RUNNING', 6, 1),
('IT-CI-CD-01', 'jenkins01.it.company.com', '10.1.8.12', 'Ubuntu 22.04 LTS', 8, 16, 250, 'RUNNING', 7, 1);

-- Operations Department VMs (30 VMs)
INSERT INTO virtual_machine (name, hostname, ip_address, operating_system, vcpu, vram, disk_size, status, hardware_id, department_id) VALUES
-- Application Servers
('OPS-APP-01', 'app01.ops.company.com', '10.2.1.10', 'Windows Server 2022', 6, 12, 200, 'RUNNING', 21, 2),
('OPS-APP-02', 'app02.ops.company.com', '10.2.1.11', 'Windows Server 2022', 6, 12, 200, 'RUNNING', 21, 2),
('OPS-APP-03', 'app03.ops.company.com', '10.2.1.12', 'Ubuntu 22.04 LTS', 4, 8, 150, 'RUNNING', 22, 2),
('OPS-APP-04', 'app04.ops.company.com', '10.2.1.13', 'Ubuntu 22.04 LTS', 4, 8, 150, 'RUNNING', 22, 2),
-- Database Servers
('OPS-DB-01', 'db01.ops.company.com', '10.2.2.10', 'Ubuntu 22.04 LTS', 8, 32, 500, 'RUNNING', 23, 2),
('OPS-DB-02', 'db02.ops.company.com', '10.2.2.11', 'Windows Server 2022', 8, 32, 500, 'RUNNING', 23, 2),
('OPS-DB-03', 'db03.ops.company.com', '10.2.2.12', 'Red Hat Enterprise Linux 8', 12, 48, 750, 'RUNNING', 24, 2),
-- Web Servers
('OPS-WEB-01', 'web01.ops.company.com', '10.2.3.10', 'Ubuntu 22.04 LTS', 4, 8, 100, 'RUNNING', 25, 2),
('OPS-WEB-02', 'web02.ops.company.com', '10.2.3.11', 'Ubuntu 22.04 LTS', 4, 8, 100, 'RUNNING', 25, 2),
('OPS-WEB-03', 'web03.ops.company.com', '10.2.3.12', 'CentOS 8', 4, 8, 100, 'STOPPED', 26, 2),
-- Warehouse Management Systems
('OPS-WMS-01', 'wms01.ops.company.com', '10.2.4.10', 'Windows Server 2022', 8, 16, 250, 'RUNNING', 27, 2),
('OPS-WMS-02', 'wms02.ops.company.com', '10.2.4.11', 'Windows Server 2022', 8, 16, 250, 'RUNNING', 27, 2),
('OPS-WMS-03', 'wms03.ops.company.com', '10.2.4.12', 'Ubuntu 22.04 LTS', 6, 12, 200, 'RUNNING', 28, 2),
-- Logistics & Tracking
('OPS-TMS-01', 'tms01.ops.company.com', '10.2.5.10', 'Ubuntu 22.04 LTS', 6, 12, 200, 'RUNNING', 21, 2),
('OPS-TMS-02', 'tms02.ops.company.com', '10.2.5.11', 'Windows Server 2022', 6, 12, 200, 'RUNNING', 22, 2),
('OPS-GPS-01', 'gps01.ops.company.com', '10.2.5.12', 'Ubuntu 22.04 LTS', 4, 8, 150, 'RUNNING', 23, 2),
-- Inventory Management
('OPS-INV-01', 'inventory01.ops.company.com', '10.2.6.10', 'Windows Server 2022', 8, 16, 300, 'RUNNING', 24, 2),
('OPS-INV-02', 'inventory02.ops.company.com', '10.2.6.11', 'Windows Server 2022', 8, 16, 300, 'RUNNING', 25, 2),
('OPS-RFID-01', 'rfid01.ops.company.com', '10.2.6.12', 'Ubuntu 22.04 LTS', 4, 8, 150, 'RUNNING', 26, 2),
-- Reporting & Analytics
('OPS-BI-01', 'bi01.ops.company.com', '10.2.7.10', 'Windows Server 2022', 12, 32, 500, 'RUNNING', 27, 2),
('OPS-BI-02', 'bi02.ops.company.com', '10.2.7.11', 'Ubuntu 22.04 LTS', 8, 24, 400, 'RUNNING', 28, 2),
('OPS-RPT-01', 'reports01.ops.company.com', '10.2.7.12', 'Windows Server 2022', 6, 12, 250, 'RUNNING', 21, 2),
-- Monitoring & Management
('OPS-MON-01', 'monitoring01.ops.company.com', '10.2.8.10', 'Ubuntu 22.04 LTS', 4, 8, 150, 'RUNNING', 22, 2),
('OPS-LOG-01', 'logging01.ops.company.com', '10.2.8.11', 'Ubuntu 22.04 LTS', 4, 8, 300, 'RUNNING', 23, 2),
('OPS-BACKUP-01', 'backup01.ops.company.com', '10.2.8.12', 'Ubuntu 22.04 LTS', 4, 8, 500, 'RUNNING', 24, 2),
-- Development/Testing
('OPS-DEV-01', 'dev01.ops.company.com', '10.2.9.10', 'Ubuntu 22.04 LTS', 6, 12, 200, 'RUNNING', 25, 2),
('OPS-TEST-01', 'test01.ops.company.com', '10.2.9.11', 'Windows Server 2022', 4, 8, 150, 'STOPPED', 26, 2),
('OPS-STAGE-01', 'staging01.ops.company.com', '10.2.9.12', 'Ubuntu 22.04 LTS', 6, 12, 200, 'RUNNING', 27, 2),
-- Integration & APIs
('OPS-API-01', 'api01.ops.company.com', '10.2.10.10', 'Ubuntu 22.04 LTS', 6, 12, 150, 'RUNNING', 28, 2),
('OPS-ESB-01', 'esb01.ops.company.com', '10.2.10.11', 'Ubuntu 22.04 LTS', 8, 16, 200, 'RUNNING', 21, 2);

-- Finance Department VMs (30 VMs)
INSERT INTO virtual_machine (name, hostname, ip_address, operating_system, vcpu, vram, disk_size, status, hardware_id, department_id) VALUES
-- Accounting Systems
('FIN-ACC-01', 'accounting01.fin.company.com', '10.3.1.10', 'Windows Server 2022', 8, 16, 300, 'RUNNING', 41, 3),
('FIN-ACC-02', 'accounting02.fin.company.com', '10.3.1.11', 'Windows Server 2022', 8, 16, 300, 'RUNNING', 41, 3),
('FIN-GL-01', 'generalledger01.fin.company.com', '10.3.1.12', 'Windows Server 2022', 6, 12, 250, 'RUNNING', 42, 3),
('FIN-AP-01', 'payables01.fin.company.com', '10.3.1.13', 'Windows Server 2022', 6, 12, 250, 'RUNNING', 42, 3),
('FIN-AR-01', 'receivables01.fin.company.com', '10.3.1.14', 'Windows Server 2022', 6, 12, 250, 'RUNNING', 43, 3),
-- Database Servers
('FIN-DB-01', 'db01.fin.company.com', '10.3.2.10', 'Ubuntu 22.04 LTS', 12, 48, 750, 'RUNNING', 44, 3),
('FIN-DB-02', 'db02.fin.company.com', '10.3.2.11', 'Windows Server 2022', 12, 48, 750, 'RUNNING', 44, 3),
('FIN-DB-03', 'db03.fin.company.com', '10.3.2.12', 'Oracle Linux 8', 16, 64, 1000, 'RUNNING', 45, 3),
-- Financial Reporting
('FIN-RPT-01', 'reports01.fin.company.com', '10.3.3.10', 'Windows Server 2022', 8, 16, 300, 'RUNNING', 46, 3),
('FIN-RPT-02', 'reports02.fin.company.com', '10.3.3.11', 'Windows Server 2022', 8, 16, 300, 'RUNNING', 46, 3),
('FIN-BI-01', 'bi01.fin.company.com', '10.3.3.12', 'Ubuntu 22.04 LTS', 12, 32, 500, 'RUNNING', 47, 3),
-- Budgeting & Planning
('FIN-BUD-01', 'budget01.fin.company.com', '10.3.4.10', 'Windows Server 2022', 6, 12, 250, 'RUNNING', 48, 3),
('FIN-PLAN-01', 'planning01.fin.company.com', '10.3.4.11', 'Windows Server 2022', 6, 12, 250, 'RUNNING', 41, 3),
('FIN-FORECAST-01', 'forecast01.fin.company.com', '10.3.4.12', 'Ubuntu 22.04 LTS', 8, 16, 300, 'RUNNING', 42, 3),
-- Treasury & Cash Management
('FIN-TMS-01', 'treasury01.fin.company.com', '10.3.5.10', 'Windows Server 2022', 8, 16, 300, 'RUNNING', 43, 3),
('FIN-CASH-01', 'cashmanagement01.fin.company.com', '10.3.5.11', 'Windows Server 2022', 6, 12, 250, 'RUNNING', 44, 3),
('FIN-BANK-01', 'banking01.fin.company.com', '10.3.5.12', 'Ubuntu 22.04 LTS', 6, 12, 250, 'RUNNING', 45, 3),
-- Compliance & Audit
('FIN-AUDIT-01', 'audit01.fin.company.com', '10.3.6.10', 'Windows Server 2022', 6, 12, 250, 'RUNNING', 46, 3),
('FIN-COMP-01', 'compliance01.fin.company.com', '10.3.6.11', 'Ubuntu 22.04 LTS', 6, 12, 250, 'RUNNING', 47, 3),
('FIN-SOX-01', 'sox01.fin.company.com', '10.3.6.12', 'Windows Server 2022', 4, 8, 200, 'RUNNING', 48, 3),
-- Web Services
('FIN-WEB-01', 'web01.fin.company.com', '10.3.7.10', 'Ubuntu 22.04 LTS', 4, 8, 150, 'RUNNING', 41, 3),
('FIN-WEB-02', 'web02.fin.company.com', '10.3.7.11', 'Ubuntu 22.04 LTS', 4, 8, 150, 'RUNNING', 42, 3),
('FIN-API-01', 'api01.fin.company.com', '10.3.7.12', 'Ubuntu 22.04 LTS', 6, 12, 200, 'RUNNING', 43, 3),
-- Analytics & Data Warehouse
('FIN-DW-01', 'datawarehouse01.fin.company.com', '10.3.8.10', 'Ubuntu 22.04 LTS', 16, 64, 1000, 'RUNNING', 44, 3),
('FIN-ETL-01', 'etl01.fin.company.com', '10.3.8.11', 'Ubuntu 22.04 LTS', 8, 24, 500, 'RUNNING', 45, 3),
('FIN-ANALYTICS-01', 'analytics01.fin.company.com', '10.3.8.12', 'Ubuntu 22.04 LTS', 12, 32, 600, 'RUNNING', 46, 3),
-- Monitoring & Support
('FIN-MON-01', 'monitoring01.fin.company.com', '10.3.9.10', 'Ubuntu 22.04 LTS', 4, 8, 150, 'RUNNING', 47, 3),
('FIN-LOG-01', 'logging01.fin.company.com', '10.3.9.11', 'Ubuntu 22.04 LTS', 4, 8, 300, 'RUNNING', 48, 3),
('FIN-BACKUP-01', 'backup01.fin.company.com', '10.3.9.12', 'Ubuntu 22.04 LTS', 4, 8, 500, 'RUNNING', 41, 3),
-- Development/Testing
('FIN-DEV-01', 'dev01.fin.company.com', '10.3.10.10', 'Windows Server 2022', 6, 12, 200, 'STOPPED', 42, 3);

-- ====================================================================
-- 6. DEPLOYMENT TASKS (20 tasks per department = 60 total)
-- ====================================================================

-- IT Department Tasks (20 tasks)
INSERT INTO deployment_task (task_name, description, vm_id, requested_by, status, created_at, scheduled_date, completed_at, department_id) VALUES
('Deploy Web Server Cluster', 'Deploy 3 new web servers for high availability', 1, 5, 'COMPLETED', '2024-11-01 08:00:00', '2024-11-05 09:00:00', '2024-11-05 15:30:00', 1),
('Database Migration', 'Migrate MySQL database to new server', 5, 6, 'COMPLETED', '2024-11-02 10:00:00', '2024-11-10 02:00:00', '2024-11-10 06:45:00', 1),
('Kubernetes Upgrade', 'Upgrade Kubernetes cluster to version 1.28', 13, 7, 'IN_PROGRESS', '2024-11-05 14:00:00', '2024-11-20 20:00:00', NULL, 1),
('SSL Certificate Renewal', 'Renew SSL certificates for all web servers', 1, 5, 'PENDING', '2024-11-15 09:00:00', '2024-11-25 10:00:00', NULL, 1),
('Monitoring Setup', 'Install and configure Prometheus monitoring', 17, 8, 'COMPLETED', '2024-10-25 11:00:00', '2024-10-28 14:00:00', '2024-10-28 17:20:00', 1),
('Backup Configuration', 'Configure automated backups for all VMs', 19, 9, 'IN_PROGRESS', '2024-11-10 13:00:00', '2024-11-18 10:00:00', NULL, 1),
('VPN Server Setup', 'Deploy new VPN server for remote access', 25, 5, 'PENDING', '2024-11-12 10:00:00', '2024-11-22 14:00:00', NULL, 1),
('DNS Server Migration', 'Migrate DNS services to new infrastructure', 27, 6, 'PENDING', '2024-11-13 15:00:00', '2024-11-27 09:00:00', NULL, 1),
('Mail Server Upgrade', 'Upgrade mail server to latest version', 28, 7, 'COMPLETED', '2024-10-20 10:00:00', '2024-10-25 08:00:00', '2024-10-25 11:30:00', 1),
('LDAP Integration', 'Integrate LDAP with all applications', 29, 8, 'IN_PROGRESS', '2024-11-08 09:00:00', '2024-11-16 10:00:00', NULL, 1),
('CI/CD Pipeline Setup', 'Configure Jenkins CI/CD pipeline', 30, 9, 'PENDING', '2024-11-14 11:00:00', '2024-11-24 13:00:00', NULL, 1),
('Security Audit', 'Perform security audit on all systems', 24, 5, 'IN_PROGRESS', '2024-11-06 14:00:00', '2024-11-19 09:00:00', NULL, 1),
('Load Balancer Setup', 'Deploy HAProxy load balancers', 3, 6, 'COMPLETED', '2024-10-28 10:00:00', '2024-11-01 14:00:00', '2024-11-01 17:45:00', 1),
('Docker Registry Deploy', 'Set up private Docker registry', 14, 7, 'PENDING', '2024-11-16 13:00:00', '2024-11-26 11:00:00', NULL, 1),
('Database Replication', 'Configure master-slave DB replication', 6, 8, 'IN_PROGRESS', '2024-11-09 10:00:00', '2024-11-17 15:00:00', NULL, 1),
('Firewall Rule Update', 'Update firewall rules for new services', 24, 9, 'COMPLETED', '2024-10-30 09:00:00', '2024-11-03 10:00:00', '2024-11-03 12:15:00', 1),
('Application Server Patch', 'Apply security patches to app servers', 9, 5, 'PENDING', '2024-11-17 14:00:00', '2024-11-28 08:00:00', NULL, 1),
('Network Monitoring', 'Deploy network monitoring tools', 17, 6, 'IN_PROGRESS', '2024-11-11 11:00:00', '2024-11-21 09:00:00', NULL, 1),
('Storage Expansion', 'Add new storage volumes to VMs', 19, 7, 'PENDING', '2024-11-18 10:00:00', '2024-11-29 13:00:00', NULL, 1),
('Performance Tuning', 'Optimize database query performance', 7, 8, 'COMPLETED', '2024-11-01 13:00:00', '2024-11-07 10:00:00', '2024-11-07 16:20:00', 1);

-- Operations Department Tasks (20 tasks)
INSERT INTO deployment_task (task_name, description, vm_id, requested_by, status, created_at, scheduled_date, completed_at, department_id) VALUES
('WMS System Upgrade', 'Upgrade warehouse management system', 41, 15, 'COMPLETED', '2024-10-28 09:00:00', '2024-11-02 08:00:00', '2024-11-02 14:30:00', 2),
('TMS Integration', 'Integrate transportation management system', 44, 16, 'IN_PROGRESS', '2024-11-05 10:00:00', '2024-11-15 09:00:00', NULL, 2),
('Inventory System Deploy', 'Deploy new inventory tracking system', 47, 17, 'PENDING', '2024-11-12 11:00:00', '2024-11-22 10:00:00', NULL, 2),
('GPS Tracking Setup', 'Install GPS tracking for fleet', 46, 18, 'COMPLETED', '2024-10-25 13:00:00', '2024-10-30 09:00:00', '2024-10-30 15:45:00', 2),
('RFID System Deploy', 'Deploy RFID tracking system', 49, 19, 'IN_PROGRESS', '2024-11-07 10:00:00', '2024-11-17 08:00:00', NULL, 2),
('BI Dashboard Setup', 'Create business intelligence dashboards', 50, 20, 'PENDING', '2024-11-14 14:00:00', '2024-11-24 11:00:00', NULL, 2),
('Database Backup Config', 'Configure automated database backups', 35, 15, 'COMPLETED', '2024-10-30 09:00:00', '2024-11-04 10:00:00', '2024-11-04 13:20:00', 2),
('API Gateway Deploy', 'Deploy API gateway for integrations', 60, 16, 'IN_PROGRESS', '2024-11-09 11:00:00', '2024-11-19 10:00:00', NULL, 2),
('Web Portal Upgrade', 'Upgrade customer web portal', 38, 17, 'PENDING', '2024-11-15 10:00:00', '2024-11-25 09:00:00', NULL, 2),
('Reporting System Deploy', 'Deploy new reporting system', 52, 18, 'COMPLETED', '2024-11-02 12:00:00', '2024-11-08 08:00:00', '2024-11-08 16:15:00', 2),
('Monitoring Tool Setup', 'Install operations monitoring tools', 53, 19, 'IN_PROGRESS', '2024-11-10 13:00:00', '2024-11-20 09:00:00', NULL, 2),
('Load Balancer Config', 'Configure load balancers for WMS', 41, 20, 'PENDING', '2024-11-16 11:00:00', '2024-11-26 10:00:00', NULL, 2),
('Disaster Recovery Test', 'Test disaster recovery procedures', 55, 15, 'COMPLETED', '2024-10-26 08:00:00', '2024-11-01 09:00:00', '2024-11-01 17:30:00', 2),
('Application Server Patch', 'Patch application servers', 31, 16, 'IN_PROGRESS', '2024-11-11 14:00:00', '2024-11-21 08:00:00', NULL, 2),
('Database Migration', 'Migrate legacy database to new platform', 36, 17, 'PENDING', '2024-11-17 10:00:00', '2024-11-27 09:00:00', NULL, 2),
('SSL Certificate Update', 'Update SSL certificates', 38, 18, 'COMPLETED', '2024-11-03 11:00:00', '2024-11-09 10:00:00', '2024-11-09 14:45:00', 2),
('Network Segmentation', 'Implement network segmentation', 53, 19, 'IN_PROGRESS', '2024-11-12 09:00:00', '2024-11-22 08:00:00', NULL, 2),
('Storage Expansion', 'Expand storage capacity', 55, 20, 'PENDING', '2024-11-18 13:00:00', '2024-11-28 10:00:00', NULL, 2),
('Security Scan', 'Perform security vulnerability scan', 54, 15, 'COMPLETED', '2024-11-04 10:00:00', '2024-11-10 09:00:00', '2024-11-10 15:20:00', 2),
('Performance Optimization', 'Optimize system performance', 50, 16, 'IN_PROGRESS', '2024-11-13 11:00:00', '2024-11-23 09:00:00', NULL, 2);

-- Finance Department Tasks (20 tasks)
INSERT INTO deployment_task (task_name, description, vm_id, requested_by, status, created_at, scheduled_date, completed_at, department_id) VALUES
('Accounting System Upgrade', 'Upgrade accounting software to latest version', 61, 25, 'COMPLETED', '2024-10-27 09:00:00', '2024-11-01 08:00:00', '2024-11-01 15:30:00', 3),
('Financial Reporting Deploy', 'Deploy new financial reporting system', 69, 26, 'IN_PROGRESS', '2024-11-06 10:00:00', '2024-11-16 09:00:00', NULL, 3),
('Budget Planning System', 'Install budget planning software', 72, 27, 'PENDING', '2024-11-13 11:00:00', '2024-11-23 10:00:00', NULL, 3),
('Treasury System Setup', 'Configure treasury management system', 75, 28, 'COMPLETED', '2024-10-29 13:00:00', '2024-11-03 09:00:00', '2024-11-03 16:45:00', 3),
('Audit System Deploy', 'Deploy audit management system', 78, 29, 'IN_PROGRESS', '2024-11-08 10:00:00', '2024-11-18 08:00:00', NULL, 3),
('Data Warehouse Build', 'Build financial data warehouse', 84, 30, 'PENDING', '2024-11-15 14:00:00', '2024-11-25 11:00:00', NULL, 3),
('Database Replication', 'Set up database replication', 67, 25, 'COMPLETED', '2024-10-31 09:00:00', '2024-11-05 10:00:00', '2024-11-05 14:20:00', 3),
('API Integration', 'Integrate with banking APIs', 83, 26, 'IN_PROGRESS', '2024-11-10 11:00:00', '2024-11-20 10:00:00', NULL, 3),
('Web Portal Maintenance', 'Perform web portal maintenance', 81, 27, 'PENDING', '2024-11-16 10:00:00', '2024-11-26 09:00:00', NULL, 3),
('BI Dashboard Creation', 'Create executive BI dashboards', 71, 28, 'COMPLETED', '2024-11-03 12:00:00', '2024-11-09 08:00:00', '2024-11-09 17:15:00', 3),
('Compliance Check', 'Run compliance verification checks', 79, 29, 'IN_PROGRESS', '2024-11-11 13:00:00', '2024-11-21 09:00:00', NULL, 3),
('SSL Certificate Renewal', 'Renew SSL certificates', 81, 30, 'PENDING', '2024-11-17 11:00:00', '2024-11-27 10:00:00', NULL, 3),
('Backup Verification', 'Verify backup and recovery procedures', 89, 25, 'COMPLETED', '2024-10-28 08:00:00', '2024-11-02 09:00:00', '2024-11-02 18:30:00', 3),
('Application Patch', 'Apply security patches', 61, 26, 'IN_PROGRESS', '2024-11-12 14:00:00', '2024-11-22 08:00:00', NULL, 3),
('Database Upgrade', 'Upgrade database to new version', 68, 27, 'PENDING', '2024-11-18 10:00:00', '2024-11-28 09:00:00', NULL, 3),
('Network Security Audit', 'Conduct network security audit', 88, 28, 'COMPLETED', '2024-11-04 11:00:00', '2024-11-10 10:00:00', '2024-11-10 16:45:00', 3),
('ETL Pipeline Setup', 'Configure ETL data pipeline', 85, 29, 'IN_PROGRESS', '2024-11-13 09:00:00', '2024-11-23 08:00:00', NULL, 3),
('Storage Optimization', 'Optimize storage usage', 89, 30, 'PENDING', '2024-11-19 13:00:00', '2024-11-29 10:00:00', NULL, 3),
('Performance Tuning', 'Tune database performance', 67, 25, 'COMPLETED', '2024-11-05 10:00:00', '2024-11-11 09:00:00', '2024-11-11 16:20:00', 3),
('DR Test', 'Test disaster recovery plan', 89, 26, 'IN_PROGRESS', '2024-11-14 11:00:00', '2024-11-24 09:00:00', NULL, 3);

-- ====================================================================
-- SUMMARY
-- ====================================================================
-- 3 Departments
-- 34 Users (4 admins + 30 regular users)
-- 15 Sites (5 per department)
-- 60 Hardware items (20 per department)
-- 90 Virtual Machines (30 per department)
-- 60 Deployment Tasks (20 per department)
-- ====================================================================

SELECT 'DUMMY DATA LOADED SUCCESSFULLY!' as status;
SELECT 
    (SELECT COUNT(*) FROM department) as departments,
    (SELECT COUNT(*) FROM users) as users,
    (SELECT COUNT(*) FROM site) as sites,
    (SELECT COUNT(*) FROM hardware) as hardware_items,
    (SELECT COUNT(*) FROM virtual_machine) as virtual_machines,
    (SELECT COUNT(*) FROM deployment_task) as deployment_tasks;

