package com.example.demo.controller;

import com.example.demo.dto.UserDto;
import com.example.demo.entite.User;
import com.example.demo.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService service;
    private final PasswordEncoder passwordEncoder;
    
    public UserController(UserService service, PasswordEncoder passwordEncoder) { 
        this.service = service;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping
    public List<UserDto> list() {
        return service.findAll().stream().map(UserController::toDto).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> get(@PathVariable Long id) {
        return service.findById(id).map(UserController::toDto).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<UserDto> create(@RequestBody UserDto dto) {
        User entity = new User();
        entity.setUsername(dto.username);
        entity.setEmail(dto.email);
        entity.setEnabled(dto.enabled);
        entity.setRole(dto.role);
        // Hash the password using BCrypt
        String rawPassword = dto.passwordHash != null ? dto.passwordHash : "defaultPassword123";
        entity.setPasswordHash(passwordEncoder.encode(rawPassword));
        User saved = service.save(entity);
        return ResponseEntity.created(URI.create("/api/users/" + saved.getId())).body(toDto(saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDto> update(@PathVariable Long id, @RequestBody UserDto dto) {
        return service.findById(id).map(existing -> {
            existing.setUsername(dto.username);
            existing.setEmail(dto.email);
            existing.setEnabled(dto.enabled);
            existing.setRole(dto.role);
            // Update and hash password only if provided
            if (dto.passwordHash != null && !dto.passwordHash.isEmpty()) {
                existing.setPasswordHash(passwordEncoder.encode(dto.passwordHash));
            }
            User saved = service.save(existing);
            return ResponseEntity.ok(toDto(saved));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
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
        return dto;
    }
}


