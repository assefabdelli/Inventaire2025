package com.example.demo.controller;

import com.example.demo.dto.UserDto;
import com.example.demo.entite.Department;
import com.example.demo.entite.User;
import com.example.demo.repository.DepartmentRepository;
import com.example.demo.service.UserContextService;
import com.example.demo.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {
    private final UserService service;
    private final PasswordEncoder passwordEncoder;
    private final DepartmentRepository departmentRepository;
    private final UserContextService userContextService;
    
    public UserController(UserService service, PasswordEncoder passwordEncoder, DepartmentRepository departmentRepository, UserContextService userContextService) { 
        this.service = service;
        this.passwordEncoder = passwordEncoder;
        this.departmentRepository = departmentRepository;
        this.userContextService = userContextService;
    }

    @GetMapping
    public ResponseEntity<?> list(
            @RequestParam(required = false) Long departmentId,
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        
        if (userId != null) {
            userContextService.setCurrentUserId(userId);
        }
        
        // SUPER_ADMIN can see all users or filter by department
        if (userContextService.isSuperAdmin()) {
            if (departmentId != null) {
                return ResponseEntity.ok(service.findByDepartmentId(departmentId).stream().map(UserController::toDto).collect(Collectors.toList()));
            }
            return ResponseEntity.ok(service.findAll().stream().map(UserController::toDto).collect(Collectors.toList()));
        }
        
        // Regular ADMIN and USER can only see users in their own department
        Long userDeptId = userContextService.getCurrentUserDepartmentId();
        if (userDeptId == null) {
            return ResponseEntity.ok(List.of());
        }
        return ResponseEntity.ok(service.findByDepartmentId(userDeptId).stream().map(UserController::toDto).collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> get(@PathVariable Long id) {
        return service.findById(id).map(UserController::toDto).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> create(
            @RequestBody UserDto dto,
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        
        if (userId != null) {
            userContextService.setCurrentUserId(userId);
        }
        
        if (!userContextService.isSuperAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body("Only super administrators can create users");
        }
        
        User entity = new User();
        entity.setUsername(dto.username);
        entity.setEmail(dto.email);
        entity.setEnabled(dto.enabled);
        entity.setRole(dto.role);
        if (dto.departmentId != null) {
            Department dept = departmentRepository.findById(dto.departmentId)
                .orElseThrow(() -> new RuntimeException("Department not found"));
            entity.setDepartment(dept);
        }
        // Hash the password using BCrypt
        String rawPassword = dto.passwordHash != null ? dto.passwordHash : "defaultPassword123";
        entity.setPasswordHash(passwordEncoder.encode(rawPassword));
        User saved = service.save(entity);
        return ResponseEntity.created(URI.create("/api/users/" + saved.getId())).body(toDto(saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(
            @PathVariable Long id, 
            @RequestBody UserDto dto,
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        
        if (userId != null) {
            userContextService.setCurrentUserId(userId);
        }
        
        if (!userContextService.isSuperAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body("Only super administrators can update users");
        }
        
        return service.findById(id).map(existing -> {
            existing.setUsername(dto.username);
            existing.setEmail(dto.email);
            existing.setEnabled(dto.enabled);
            existing.setRole(dto.role);
            if (dto.departmentId != null) {
                Department dept = departmentRepository.findById(dto.departmentId)
                    .orElseThrow(() -> new RuntimeException("Department not found"));
                existing.setDepartment(dept);
            }
            // Update and hash password only if provided
            if (dto.passwordHash != null && !dto.passwordHash.isEmpty()) {
                existing.setPasswordHash(passwordEncoder.encode(dto.passwordHash));
            }
            User saved = service.save(existing);
            return ResponseEntity.ok((Object)toDto(saved));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(
            @PathVariable Long id,
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        
        if (userId != null) {
            userContextService.setCurrentUserId(userId);
        }
        
        if (!userContextService.isSuperAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body("Only super administrators can delete users");
        }
        
        if (!service.existsById(id)) return ResponseEntity.notFound().build();
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    private static UserDto toDto(User u) {
        UserDto dto = new UserDto();
        dto.id = u.getId();
        dto.username = u.getUsername();
        dto.email = u.getEmail();
        dto.enabled = u.isEnabled();
        dto.role = u.getRole();
        dto.departmentId = u.getDepartment() != null ? u.getDepartment().getId() : null;
        return dto;
    }
}


