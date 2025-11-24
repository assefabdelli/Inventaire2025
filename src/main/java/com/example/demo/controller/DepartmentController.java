package com.example.demo.controller;

import com.example.demo.dto.DepartmentDto;
import com.example.demo.service.DepartmentService;
import com.example.demo.service.UserContextService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/departments")
@CrossOrigin(origins = "*")
public class DepartmentController {

    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private UserContextService userContextService;

    @GetMapping
    public ResponseEntity<?> getAllDepartments(
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        
        if (userId != null) {
            userContextService.setCurrentUserId(userId);
        }
        
        // Only SUPER_ADMIN can view all departments
        if (!userContextService.isSuperAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body("Only super administrators can view all departments");
        }
        
        return ResponseEntity.ok(departmentService.getAllDepartments());
    }

    @GetMapping("/active")
    public ResponseEntity<?> getActiveDepartments(
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        
        if (userId != null) {
            userContextService.setCurrentUserId(userId);
        }
        
        // Only SUPER_ADMIN can view all departments
        if (!userContextService.isSuperAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body("Only super administrators can view departments");
        }
        
        return ResponseEntity.ok(departmentService.getActiveDepartments());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getDepartmentById(
            @PathVariable Long id,
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        
        if (userId != null) {
            userContextService.setCurrentUserId(userId);
        }
        
        // Only SUPER_ADMIN can view department details
        if (!userContextService.isSuperAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body("Only super administrators can view department details");
        }
        
        return ResponseEntity.ok(departmentService.getDepartmentById(id));
    }

    @PostMapping
    public ResponseEntity<?> createDepartment(
            @RequestBody DepartmentDto dto,
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        
        if (userId != null) {
            userContextService.setCurrentUserId(userId);
        }
        
        if (!userContextService.isSuperAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body("Only super administrators can create departments");
        }
        
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(departmentService.createDepartment(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateDepartment(
            @PathVariable Long id, 
            @RequestBody DepartmentDto dto,
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        
        if (userId != null) {
            userContextService.setCurrentUserId(userId);
        }
        
        if (!userContextService.isSuperAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body("Only super administrators can update departments");
        }
        
        return ResponseEntity.ok(departmentService.updateDepartment(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteDepartment(
            @PathVariable Long id,
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        
        if (userId != null) {
            userContextService.setCurrentUserId(userId);
        }
        
        if (!userContextService.isSuperAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body("Only super administrators can delete departments");
        }
        
        departmentService.deleteDepartment(id);
        return ResponseEntity.noContent().build();
    }
}

