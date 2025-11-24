package com.example.demo.service;

import com.example.demo.entite.Hardware;
import com.example.demo.entite.Site;
import com.example.demo.repository.HardwareRepository;
import com.example.demo.repository.SiteRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;

@Service
public class HardwareService {
    private final HardwareRepository hardwareRepository;
    private final SiteRepository siteRepository;

    public HardwareService(HardwareRepository hardwareRepository, SiteRepository siteRepository) {
        this.hardwareRepository = hardwareRepository;
        this.siteRepository = siteRepository;
    }

    public List<Hardware> findAll() { return hardwareRepository.findAll(); }

    public List<Hardware> findByDepartmentId(Long departmentId) {
        return hardwareRepository.findByDepartmentId(departmentId);
    }

    public Hardware findByIdOrThrow(Long id) {
        return hardwareRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    public Hardware create(Hardware hardware, Long siteId) {
        Site site = siteRepository.findById(siteId).orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid siteId"));
        hardware.setSite(site);
        return hardwareRepository.save(hardware);
    }

    public Hardware update(Long id, Hardware updated, Long siteId) {
        Hardware existing = findByIdOrThrow(id);
        if (siteId != null) {
            Site site = siteRepository.findById(siteId).orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid siteId"));
            existing.setSite(site);
        }
        existing.setName(updated.getName());
        existing.setType(updated.getType());
        existing.setModel(updated.getModel());
        existing.setSerialNumber(updated.getSerialNumber());
        existing.setIpAddress(updated.getIpAddress());
        existing.setCpuCores(updated.getCpuCores());
        existing.setRamGb(updated.getRamGb());
        existing.setStorageGb(updated.getStorageGb());
        existing.setStatus(updated.getStatus());
        existing.setPurchaseDate(updated.getPurchaseDate());
        existing.setWarrantyEndDate(updated.getWarrantyEndDate());
        return hardwareRepository.save(existing);
    }

    public void delete(Long id) { hardwareRepository.deleteById(id); }
}


