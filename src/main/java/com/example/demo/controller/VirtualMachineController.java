package com.example.demo.controller;

import com.example.demo.dto.VirtualMachineDto;
import com.example.demo.entite.VirtualMachine;
import com.example.demo.service.VirtualMachineService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/vms")
public class VirtualMachineController {
    private final VirtualMachineService service;
    public VirtualMachineController(VirtualMachineService service) { this.service = service; }

    @GetMapping
    public List<VirtualMachineDto> list() {
        return service.findAll().stream().map(VirtualMachineController::toDto).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<VirtualMachineDto> get(@PathVariable Long id) {
        return ResponseEntity.ok(toDto(service.findByIdOrThrow(id)));
    }

    @PostMapping
    public ResponseEntity<VirtualMachineDto> create(@RequestBody VirtualMachineDto dto) {
        VirtualMachine e = new VirtualMachine();
        e.setName(dto.name);
        e.setCpu(dto.cpu);
        e.setRamGB(dto.ramGB);
        e.setDiskGB(dto.diskGB);
        e.setStatus(dto.status);
        e.setOs(dto.os);
        VirtualMachine saved = service.create(e, dto.hardwareId);
        return ResponseEntity.created(URI.create("/api/vms/" + saved.getId())).body(toDto(saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<VirtualMachineDto> update(@PathVariable Long id, @RequestBody VirtualMachineDto dto) {
        VirtualMachine updated = new VirtualMachine();
        updated.setName(dto.name);
        updated.setCpu(dto.cpu);
        updated.setRamGB(dto.ramGB);
        updated.setDiskGB(dto.diskGB);
        updated.setStatus(dto.status);
        updated.setOs(dto.os);
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
        dto.cpu = e.getCpu();
        dto.ramGB = e.getRamGB();
        dto.diskGB = e.getDiskGB();
        dto.status = e.getStatus();
        dto.os = e.getOs();
        dto.hardwareId = e.getHardware() != null ? e.getHardware().getId() : null;
        return dto;
    }
}


