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
    private int cpu;

    @Column(nullable = false)
    private int ramGB;

    @Column(nullable = false)
    private int diskGB;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private VMStatus status = VMStatus.STOPPED;

    @Column(nullable = false)
    private String os;

    @ManyToOne(optional = false)
    @JoinColumn(name = "hardware_id", nullable = false)
    private Hardware hardware;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getCpu() { return cpu; }
    public void setCpu(int cpu) { this.cpu = cpu; }

    public int getRamGB() { return ramGB; }
    public void setRamGB(int ramGB) { this.ramGB = ramGB; }

    public int getDiskGB() { return diskGB; }
    public void setDiskGB(int diskGB) { this.diskGB = diskGB; }

    public VMStatus getStatus() { return status; }
    public void setStatus(VMStatus status) { this.status = status; }

    public String getOs() { return os; }
    public void setOs(String os) { this.os = os; }

    public Hardware getHardware() { return hardware; }
    public void setHardware(Hardware hardware) { this.hardware = hardware; }
}


