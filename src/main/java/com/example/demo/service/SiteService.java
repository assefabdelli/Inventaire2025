package com.example.demo.service;

import com.example.demo.entite.Site;
import com.example.demo.repository.SiteRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class SiteService {
    private final SiteRepository repository;

    public SiteService(SiteRepository repository) {
        this.repository = repository;
    }

    public List<Site> findAll() { return repository.findAll(); }

    public List<Site> findByDepartmentId(Long departmentId) {
        return repository.findByDepartmentId(departmentId);
    }

    public Optional<Site> findById(Long id) { return repository.findById(id); }

    public Site save(Site site) { return repository.save(site); }

    public boolean existsById(Long id) { return repository.existsById(id); }

    public void deleteById(Long id) { repository.deleteById(id); }
}


