package com.example.demo.dto;

import com.example.demo.enums.VMStatus;

public class VirtualMachineDto {
    public Long id;
    public String name;
    public String hostname;
    public String ipAddress;
    public String operatingSystem;
    public int vcpu;
    public int vram;
    public int diskSize;
    public VMStatus status;
    public Long hardwareId;
}


