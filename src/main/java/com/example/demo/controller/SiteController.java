package com.example.demo.controller;

import com.example.demo.dto.SiteDto;
import com.example.demo.entite.Site;
import com.example.demo.service.SiteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/sites")
public class SiteController {
    private final SiteService service;
    public SiteController(SiteService service) { this.service = service; }

    @GetMapping
    public List<SiteDto> list() {
        return service.findAll().stream().map(SiteController::toDto).collect(Collectors.toList());
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
        return dto;
    }
}


