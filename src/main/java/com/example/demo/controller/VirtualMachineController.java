package com.example.demo.controller;

import com.example.demo.dto.VirtualMachineDto;
import com.example.demo.entite.Department;
import com.example.demo.entite.VirtualMachine;
import com.example.demo.repository.DepartmentRepository;
import com.example.demo.service.UserContextService;
import com.example.demo.service.VirtualMachineService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/virtual-machines")
@CrossOrigin(origins = "*")
public class VirtualMachineController {
    private final VirtualMachineService service;
    private final DepartmentRepository departmentRepository;
    private final UserContextService userContextService;
    
    public VirtualMachineController(VirtualMachineService service, DepartmentRepository departmentRepository, UserContextService userContextService) { 
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
            if (userId != null && !userContextService.isAdmin()) {
                Long userDeptId = userContextService.getCurrentUserDepartmentId();
                if (userDeptId != null) {
                    return ResponseEntity.ok(service.findByDepartmentId(userDeptId).stream()
                            .map(VirtualMachineController::toDto).collect(Collectors.toList()));
                }
            }
            
            // Admin can filter or see all
            if (departmentId != null) {
                return ResponseEntity.ok(service.findByDepartmentId(departmentId).stream()
                        .map(VirtualMachineController::toDto).collect(Collectors.toList()));
            }
            return ResponseEntity.ok(service.findAll().stream()
                    .map(VirtualMachineController::toDto).collect(Collectors.toList()));
        } catch (Exception e) {
            System.err.println("Error loading virtual machines: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.ok(List.of()); // Return empty list instead of error
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<VirtualMachineDto> get(@PathVariable Long id) {
        return ResponseEntity.ok(toDto(service.findByIdOrThrow(id)));
    }

    @PostMapping
    public ResponseEntity<VirtualMachineDto> create(@RequestBody VirtualMachineDto dto) {
        VirtualMachine e = new VirtualMachine();
        e.setName(dto.name);
        e.setHostname(dto.hostname);
        e.setIpAddress(dto.ipAddress);
        e.setOperatingSystem(dto.operatingSystem);
        e.setVcpu(dto.vcpu);
        e.setVram(dto.vram);
        e.setDiskSize(dto.diskSize);
        e.setStatus(dto.status);
        if (dto.departmentId != null) {
            Department dept = departmentRepository.findById(dto.departmentId)
                .orElseThrow(() -> new RuntimeException("Department not found"));
            e.setDepartment(dept);
        }
        VirtualMachine saved = service.create(e, dto.hardwareId);
        return ResponseEntity.created(URI.create("/api/virtual-machines/" + saved.getId())).body(toDto(saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<VirtualMachineDto> update(@PathVariable Long id, @RequestBody VirtualMachineDto dto) {
        VirtualMachine updated = new VirtualMachine();
        updated.setName(dto.name);
        updated.setHostname(dto.hostname);
        updated.setIpAddress(dto.ipAddress);
        updated.setOperatingSystem(dto.operatingSystem);
        updated.setVcpu(dto.vcpu);
        updated.setVram(dto.vram);
        updated.setDiskSize(dto.diskSize);
        updated.setStatus(dto.status);
        if (dto.departmentId != null) {
            Department dept = departmentRepository.findById(dto.departmentId)
                .orElseThrow(() -> new RuntimeException("Department not found"));
            updated.setDepartment(dept);
        }
        VirtualMachine saved = service.update(id, updated, dto.hardwareId);
        return ResponseEntity.ok(toDto(saved));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    private static VirtualMachineDto toDto(VirtualMachine e) {
        VirtualMachineDto dto = new VirtualMachineDto();
        dto.id = e.getId();
        dto.name = e.getName();
        dto.hostname = e.getHostname();
        dto.ipAddress = e.getIpAddress();
        dto.operatingSystem = e.getOperatingSystem();
        dto.vcpu = e.getVcpu();
        dto.vram = e.getVram();
        dto.diskSize = e.getDiskSize();
        dto.status = e.getStatus();
        dto.hardwareId = e.getHardware() != null ? e.getHardware().getId() : null;
        dto.departmentId = e.getDepartment() != null ? e.getDepartment().getId() : null;
        return dto;
    }
}


