package com.example.demo.dto;

import com.example.demo.enums.Role;

public class LoginResponse {
    private Long userId;
    private String username;
    private String email;
    private Role role;
    private Long departmentId;
    private String departmentName;
    private String token;

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public Long getDepartmentId() { return departmentId; }
    public void setDepartmentId(Long departmentId) { this.departmentId = departmentId; }

    public String getDepartmentName() { return departmentName; }
    public void setDepartmentName(String departmentName) { this.departmentName = departmentName; }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
}

