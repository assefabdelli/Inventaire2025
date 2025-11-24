package com.example.demo.controller;

import com.example.demo.dto.HardwareDto;
import com.example.demo.entite.Department;
import com.example.demo.entite.Hardware;
import com.example.demo.repository.DepartmentRepository;
import com.example.demo.service.HardwareService;
import com.example.demo.service.UserContextService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/hardware")
@CrossOrigin(origins = "*")
public class HardwareController {
    private final HardwareService service;
    private final DepartmentRepository departmentRepository;
    private final UserContextService userContextService;
    
    public HardwareController(HardwareService service, DepartmentRepository departmentRepository, UserContextService userContextService) { 
        this.service = service;
        this.departmentRepository = departmentRepository;
        this.userContextService = userContextService;
    }

    @GetMapping
    public ResponseEntity<?> list(
            @RequestParam(required = false) Long departmentId,
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        
        try {
            // Set user context if provided
            if (userId != null) {
                userContextService.setCurrentUserId(userId);
            }
            
            // If not admin, force filter by user's department
            if (userId != null && !userContextService.isAdmin()) {
                Long userDeptId = userContextService.getCurrentUserDepartmentId();
                if (userDeptId != null) {
                    return ResponseEntity.ok(service.findByDepartmentId(userDeptId).stream()
                            .map(HardwareController::toDto).collect(Collectors.toList()));
                }
            }
            
            // Admin can filter by department or see all
            if (departmentId != null) {
                return ResponseEntity.ok(service.findByDepartmentId(departmentId).stream()
                        .map(HardwareController::toDto).collect(Collectors.toList()));
            }
            return ResponseEntity.ok(service.findAll().stream()
                    .map(HardwareController::toDto).collect(Collectors.toList()));
        } catch (Exception e) {
            System.err.println("Error loading hardware: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.ok(List.of()); // Return empty list instead of error
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<HardwareDto> get(@PathVariable Long id) {
        return ResponseEntity.ok(toDto(service.findByIdOrThrow(id)));
    }

    @PostMapping
    public ResponseEntity<HardwareDto> create(@RequestBody HardwareDto dto) {
        Hardware e = new Hardware();
        e.setName(dto.name);
        e.setType(dto.type);
        e.setModel(dto.model);
        e.setSerialNumber(dto.serialNumber);
        e.setIpAddress(dto.ipAddress);
        e.setCpuCores(dto.cpuCores);
        e.setRamGb(dto.ramGb);
        e.setStorageGb(dto.storageGb);
        e.setStatus(dto.status);
        e.setPurchaseDate(dto.purchaseDate);
        e.setWarrantyEndDate(dto.warrantyEndDate);
        if (dto.departmentId != null) {
            Department dept = departmentRepository.findById(dto.departmentId)
                .orElseThrow(() -> new RuntimeException("Department not found"));
            e.setDepartment(dept);
        }
        Hardware saved = service.create(e, dto.siteId);
        return ResponseEntity.created(URI.create("/api/hardware/" + saved.getId())).body(toDto(saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<HardwareDto> update(@PathVariable Long id, @RequestBody HardwareDto dto) {
        Hardware updated = new Hardware();
        updated.setName(dto.name);
        updated.setType(dto.type);
        updated.setModel(dto.model);
        updated.setSerialNumber(dto.serialNumber);
        updated.setIpAddress(dto.ipAddress);
        updated.setCpuCores(dto.cpuCores);
        updated.setRamGb(dto.ramGb);
        updated.setStorageGb(dto.storageGb);
        updated.setStatus(dto.status);
        updated.setPurchaseDate(dto.purchaseDate);
        updated.setWarrantyEndDate(dto.warrantyEndDate);
        if (dto.departmentId != null) {
            Department dept = departmentRepository.findById(dto.departmentId)
                .orElseThrow(() -> new RuntimeException("Department not found"));
            updated.setDepartment(dept);
        }
        Hardware saved = service.update(id, updated, dto.siteId);
        return ResponseEntity.ok(toDto(saved));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    private static HardwareDto toDto(Hardware e) {
        HardwareDto dto = new HardwareDto();
        dto.id = e.getId();
        dto.name = e.getName();
        dto.type = e.getType();
        dto.model = e.getModel();
        dto.serialNumber = e.getSerialNumber();
        dto.ipAddress = e.getIpAddress();
        dto.cpuCores = e.getCpuCores();
        dto.ramGb = e.getRamGb();
        dto.storageGb = e.getStorageGb();
        dto.status = e.getStatus();
        dto.purchaseDate = e.getPurchaseDate();
        dto.warrantyEndDate = e.getWarrantyEndDate();
        dto.siteId = e.getSite() != null ? e.getSite().getId() : null;
        dto.departmentId = e.getDepartment() != null ? e.getDepartment().getId() : null;
        return dto;
    }
}


