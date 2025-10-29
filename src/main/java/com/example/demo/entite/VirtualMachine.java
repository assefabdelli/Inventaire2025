package com.example.demo.entite;

import com.example.demo.enums.VMStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "virtual_machine")
public class VirtualMachine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String hostname;

    @Column
    private String ipAddress;

    @Column(nullable = false)
    private String operatingSystem;

    @Column(nullable = false)
    private int vcpu;

    @Column(nullable = false)
    private int vram;

    @Column(nullable = false)
    private int diskSize;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private VMStatus status = VMStatus.STOPPED;

    @ManyToOne(optional = false)
    @JoinColumn(name = "hardware_id", nullable = false)
    private Hardware hardware;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getHostname() { return hostname; }
    public void setHostname(String hostname) { this.hostname = hostname; }

    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }

    public String getOperatingSystem() { return operatingSystem; }
    public void setOperatingSystem(String operatingSystem) { this.operatingSystem = operatingSystem; }

    public int getVcpu() { return vcpu; }
    public void setVcpu(int vcpu) { this.vcpu = vcpu; }

    public int getVram() { return vram; }
    public void setVram(int vram) { this.vram = vram; }

    public int getDiskSize() { return diskSize; }
    public void setDiskSize(int diskSize) { this.diskSize = diskSize; }

    public VMStatus getStatus() { return status; }
    public void setStatus(VMStatus status) { this.status = status; }

    public Hardware getHardware() { return hardware; }
    public void setHardware(Hardware hardware) { this.hardware = hardware; }
}


