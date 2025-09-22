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
@RequestMapping("/api/tasks")
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
        e.setStatus(dto.status);
        e.setCreatedAt(dto.createdAt != null ? dto.createdAt : Instant.now());
        e.setCompletedAt(dto.completedAt);
        DeploymentTask saved = service.create(e, dto.vmId, dto.requestedById);
        return ResponseEntity.created(URI.create("/api/tasks/" + saved.getId())).body(toDto(saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DeploymentTaskDto> update(@PathVariable Long id, @RequestBody DeploymentTaskDto dto) {
        DeploymentTask updated = new DeploymentTask();
        updated.setStatus(dto.status);
        updated.setCreatedAt(dto.createdAt);
        updated.setCompletedAt(dto.completedAt);
        DeploymentTask saved = service.update(id, updated, dto.vmId, dto.requestedById);
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
        dto.vmId = e.getVm() != null ? e.getVm().getId() : null;
        dto.requestedById = e.getRequestedBy() != null ? e.getRequestedBy().getId() : null;
        dto.status = e.getStatus();
        dto.createdAt = e.getCreatedAt();
        dto.completedAt = e.getCompletedAt();
        return dto;
    }
}


