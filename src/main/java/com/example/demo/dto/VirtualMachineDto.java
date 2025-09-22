package com.example.demo.dto;

import com.example.demo.enums.VMStatus;

public class VirtualMachineDto {
    public Long id;
    public String name;
    public int cpu;
    public int ramGB;
    public int diskGB;
    public VMStatus status;
    public String os;
    public Long hardwareId;
}


