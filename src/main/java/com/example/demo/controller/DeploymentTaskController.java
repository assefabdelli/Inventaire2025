package com.example.demo.controller;

import com.example.demo.dto.DeploymentTaskDto;
import com.example.demo.entite.DeploymentTask;
import com.example.demo.service.DeploymentTaskService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.net.URI;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/deployment-tasks")
public class DeploymentTaskController {
    private final DeploymentTaskService service;
    public DeploymentTaskController(DeploymentTaskService service) { this.service = service; }

    @GetMapping
    public List<DeploymentTaskDto> list() {
        return service.findAll().stream().map(DeploymentTaskController::toDto).collect(Collectors.toList());
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
        return dto;
    }
}


