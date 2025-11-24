package com.example.demo.repository;

import com.example.demo.entite.Site;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SiteRepository extends JpaRepository<Site, Long> {
    List<Site> findByDepartmentId(Long departmentId);
}


