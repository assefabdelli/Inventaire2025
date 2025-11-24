package com.example.demo.controller;

import com.example.demo.dto.SiteDto;
import com.example.demo.entite.Department;
import com.example.demo.entite.Site;
import com.example.demo.repository.DepartmentRepository;
import com.example.demo.service.SiteService;
import com.example.demo.service.UserContextService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/sites")
@CrossOrigin(origins = "*")
public class SiteController {
    private final SiteService service;
    private final DepartmentRepository departmentRepository;
    private final UserContextService userContextService;
    
    public SiteController(SiteService service, DepartmentRepository departmentRepository, UserContextService userContextService) { 
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
                            .map(SiteController::toDto).collect(Collectors.toList()));
                }
            }
            
            // Admin can filter or see all
            if (departmentId != null) {
                return ResponseEntity.ok(service.findByDepartmentId(departmentId).stream()
                        .map(SiteController::toDto).collect(Collectors.toList()));
            }
            return ResponseEntity.ok(service.findAll().stream()
                    .map(SiteController::toDto).collect(Collectors.toList()));
        } catch (Exception e) {
            System.err.println("Error loading sites: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.ok(List.of()); // Return empty list instead of error
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<SiteDto> get(@PathVariable Long id) {
        return service.findById(id).map(SiteController::toDto).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<SiteDto> create(@RequestBody SiteDto dto) {
        Site e = new Site();
        e.setName(dto.name);
        e.setAddress(dto.address);
        e.setCity(dto.city);
        e.setCountry(dto.country);
        if (dto.departmentId != null) {
            Department dept = departmentRepository.findById(dto.departmentId)
                .orElseThrow(() -> new RuntimeException("Department not found"));
            e.setDepartment(dept);
        }
        Site saved = service.save(e);
        return ResponseEntity.created(URI.create("/api/sites/" + saved.getId())).body(toDto(saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SiteDto> update(@PathVariable Long id, @RequestBody SiteDto dto) {
        return service.findById(id).map(e -> {
            e.setName(dto.name);
            e.setAddress(dto.address);
            e.setCity(dto.city);
            e.setCountry(dto.country);
            if (dto.departmentId != null) {
                Department dept = departmentRepository.findById(dto.departmentId)
                    .orElseThrow(() -> new RuntimeException("Department not found"));
                e.setDepartment(dept);
            }
            return ResponseEntity.ok(toDto(service.save(e)));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!service.existsById(id)) return ResponseEntity.notFound().build();
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    private static SiteDto toDto(Site e) {
        SiteDto dto = new SiteDto();
        dto.id = e.getId();
        dto.name = e.getName();
        dto.address = e.getAddress();
        dto.city = e.getCity();
        dto.country = e.getCountry();
        dto.departmentId = e.getDepartment() != null ? e.getDepartment().getId() : null;
        return dto;
    }
}


