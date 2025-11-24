package com.example.demo.dto;

import com.example.demo.enums.Role;

public class UserDto {
    public Long id;
    public String username;
    public String email;
    public String passwordHash;
    public boolean enabled;
    public Role role;
    public Long departmentId;
}


