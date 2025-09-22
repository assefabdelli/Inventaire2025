package com.example.demo.service;

import com.example.demo.entite.User;
import com.example.demo.repository.UserRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository repository;

    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    public List<User> findAll() { return repository.findAll(); }

    public Optional<User> findById(Long id) { return repository.findById(id); }

    public User save(User user) { return repository.save(user); }

    public boolean existsById(Long id) { return repository.existsById(id); }

    public void deleteById(Long id) { repository.deleteById(id); }
}


