package com.example.demo.service;

import com.example.demo.entite.DeploymentTask;
import com.example.demo.entite.User;
import com.example.demo.entite.VirtualMachine;
import com.example.demo.repository.DeploymentTaskRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.VirtualMachineRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.time.Instant;
import java.util.List;

@Service
public class DeploymentTaskService {
    private final DeploymentTaskRepository taskRepository;
    private final VirtualMachineRepository vmRepository;
    private final UserRepository userRepository;

    public DeploymentTaskService(DeploymentTaskRepository taskRepository, VirtualMachineRepository vmRepository, UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.vmRepository = vmRepository;
        this.userRepository = userRepository;
    }

    public List<DeploymentTask> findAll() { return taskRepository.findAll(); }

    public DeploymentTask findByIdOrThrow(Long id) {
        return taskRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    public DeploymentTask create(DeploymentTask task, Long vmId, Long requestedById) {
        VirtualMachine vm = vmRepository.findById(vmId).orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid vmId"));
        User user = userRepository.findById(requestedById).orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid requestedById"));
        task.setVm(vm);
        task.setRequestedBy(user);
        if (task.getCreatedAt() == null) task.setCreatedAt(Instant.now());
        return taskRepository.save(task);
    }

    public DeploymentTask update(Long id, DeploymentTask updated, Long vmId, Long requestedById) {
        DeploymentTask existing = findByIdOrThrow(id);
        if (vmId != null) {
            VirtualMachine vm = vmRepository.findById(vmId).orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid vmId"));
            existing.setVm(vm);
        }
        if (requestedById != null) {
            User user = userRepository.findById(requestedById).orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid requestedById"));
            existing.setRequestedBy(user);
        }
        if (updated.getStatus() != null) existing.setStatus(updated.getStatus());
        if (updated.getCreatedAt() != null) existing.setCreatedAt(updated.getCreatedAt());
        existing.setCompletedAt(updated.getCompletedAt());
        return taskRepository.save(existing);
    }

    public void delete(Long id) { taskRepository.deleteById(id); }
}


