package com.example.demo.repository;

import com.example.demo.entite.VirtualMachine;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface VirtualMachineRepository extends JpaRepository<VirtualMachine, Long> {
    List<VirtualMachine> findByDepartmentId(Long departmentId);
}


