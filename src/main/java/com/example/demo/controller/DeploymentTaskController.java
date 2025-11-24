package com.example.demo.controller;

import com.example.demo.dto.DeploymentTaskDto;
import com.example.demo.entite.Department;
import com.example.demo.entite.DeploymentTask;
import com.example.demo.repository.DepartmentRepository;
import com.example.demo.service.DeploymentTaskService;
import com.example.demo.service.UserContextService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.net.URI;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/deployment-tasks")
@CrossOrigin(origins = "*")
public class DeploymentTaskController {
    private final DeploymentTaskService service;
    private final DepartmentRepository departmentRepository;
    private final UserContextService userContextService;
    
    public DeploymentTaskController(DeploymentTaskService service, DepartmentRepository departmentRepository, UserContextService userContextService) { 
        this.service = service;
        this.departmentRepository = departmentRepository;
        this.userContextService = userContextService;
    }

    @GetMapping
    public ResponseEntity<?> list(
            @RequestParam(required = false) Long departmentId,
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        
        try {
            if (userId != null) {
                userContextService.setCurrentUserId(userId);
            }
            
            // If not admin, force filter by user's department
            if (!userContextService.isAdmin()) {
                Long userDeptId = userContextService.getCurrentUserDepartmentId();
                if (userDeptId != null) {
                    return ResponseEntity.ok(service.findByDepartmentId(userDeptId).stream()
                            .map(DeploymentTaskController::toDto).collect(Collectors.toList()));
                }
            }
            
            // Admin can filter or see all
            if (departmentId != null) {
                return ResponseEntity.ok(service.findByDepartmentId(departmentId).stream()
                        .map(DeploymentTaskController::toDto).collect(Collectors.toList()));
            }
            return ResponseEntity.ok(service.findAll().stream()
                    .map(DeploymentTaskController::toDto).collect(Collectors.toList()));
        } catch (Exception e) {
            System.err.println("Error loading deployment tasks: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.ok(List.of()); // Return empty list instead of error
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<DeploymentTaskDto> get(@PathVariable Long id) {
        return ResponseEntity.ok(toDto(service.findByIdOrThrow(id)));
    }

    @PostMapping
    public ResponseEntity<DeploymentTaskDto> create(@RequestBody DeploymentTaskDto dto) {
        DeploymentTask e = new DeploymentTask();
        e.setTaskName(dto.taskName);
        e.setDescription(dto.description);
        e.setStatus(dto.status);
        e.setCreatedAt(dto.createdAt != null ? dto.createdAt : Instant.now());
        e.setCompletedAt(dto.completedAt);
        e.setScheduledDate(dto.scheduledDate);
        if (dto.departmentId != null) {
            Department dept = departmentRepository.findById(dto.departmentId)
                .orElseThrow(() -> new RuntimeException("Department not found"));
            e.setDepartment(dept);
        }
        // Use assignedUserId if provided, otherwise fall back to requestedById
        Long userId = dto.assignedUserId != null ? dto.assignedUserId : dto.requestedById;
        DeploymentTask saved = service.create(e, dto.vmId, userId);
        return ResponseEntity.created(URI.create("/api/deployment-tasks/" + saved.getId())).body(toDto(saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DeploymentTaskDto> update(@PathVariable Long id, @RequestBody DeploymentTaskDto dto) {
        DeploymentTask updated = new DeploymentTask();
        updated.setTaskName(dto.taskName);
        updated.setDescription(dto.description);
        updated.setStatus(dto.status);
        updated.setCreatedAt(dto.createdAt);
        updated.setCompletedAt(dto.completedAt);
        updated.setScheduledDate(dto.scheduledDate);
        if (dto.departmentId != null) {
            Department dept = departmentRepository.findById(dto.departmentId)
                .orElseThrow(() -> new RuntimeException("Department not found"));
            updated.setDepartment(dept);
        }
        // Use assignedUserId if provided, otherwise fall back to requestedById
        Long userId = dto.assignedUserId != null ? dto.assignedUserId : dto.requestedById;
        DeploymentTask saved = service.update(id, updated, dto.vmId, userId);
        return ResponseEntity.ok(toDto(saved));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    private static DeploymentTaskDto toDto(DeploymentTask e) {
        DeploymentTaskDto dto = new DeploymentTaskDto();
        dto.id = e.getId();
        dto.taskName = e.getTaskName();
        dto.description = e.getDescription();
        dto.vmId = e.getVm() != null ? e.getVm().getId() : null;
        dto.requestedById = e.getRequestedBy() != null ? e.getRequestedBy().getId() : null;
        dto.assignedUserId = dto.requestedById; // Populate both fields
        dto.status = e.getStatus();
        dto.createdAt = e.getCreatedAt();
        dto.completedAt = e.getCompletedAt();
        dto.scheduledDate = e.getScheduledDate();
        dto.departmentId = e.getDepartment() != null ? e.getDepartment().getId() : null;
        return dto;
    }
}


