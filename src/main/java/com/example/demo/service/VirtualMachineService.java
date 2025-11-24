package com.example.demo.service;

import com.example.demo.entite.Hardware;
import com.example.demo.entite.VirtualMachine;
import com.example.demo.repository.HardwareRepository;
import com.example.demo.repository.VirtualMachineRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;

@Service
public class VirtualMachineService {
    private final VirtualMachineRepository vmRepository;
    private final HardwareRepository hardwareRepository;

    public VirtualMachineService(VirtualMachineRepository vmRepository, HardwareRepository hardwareRepository) {
        this.vmRepository = vmRepository;
        this.hardwareRepository = hardwareRepository;
    }

    public List<VirtualMachine> findAll() { return vmRepository.findAll(); }

    public List<VirtualMachine> findByDepartmentId(Long departmentId) {
        return vmRepository.findByDepartmentId(departmentId);
    }

    public VirtualMachine findByIdOrThrow(Long id) {
        return vmRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    public VirtualMachine create(VirtualMachine vm, Long hardwareId) {
        if (hardwareId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Hardware ID is required");
        }
        Hardware hw = hardwareRepository.findById(hardwareId).orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid hardwareId"));
        vm.setHardware(hw);
        return vmRepository.save(vm);
    }

    public VirtualMachine update(Long id, VirtualMachine updated, Long hardwareId) {
        VirtualMachine existing = findByIdOrThrow(id);
        if (hardwareId != null) {
            Hardware hw = hardwareRepository.findById(hardwareId).orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid hardwareId"));
            existing.setHardware(hw);
        }
        existing.setName(updated.getName());
        existing.setHostname(updated.getHostname());
        existing.setIpAddress(updated.getIpAddress());
        existing.setOperatingSystem(updated.getOperatingSystem());
        existing.setVcpu(updated.getVcpu());
        existing.setVram(updated.getVram());
        existing.setDiskSize(updated.getDiskSize());
        existing.setStatus(updated.getStatus());
        return vmRepository.save(existing);
    }

    public void delete(Long id) { vmRepository.deleteById(id); }
}


