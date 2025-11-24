package com.example.demo.repository;

import com.example.demo.entite.DeploymentTask;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface DeploymentTaskRepository extends JpaRepository<DeploymentTask, Long> {
    List<DeploymentTask> findByDepartmentId(Long departmentId);
}


