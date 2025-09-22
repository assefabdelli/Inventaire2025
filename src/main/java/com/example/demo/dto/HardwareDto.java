package com.example.demo.dto;

import com.example.demo.enums.HardwareStatus;
import com.example.demo.enums.HardwareType;
import java.time.LocalDate;

public class HardwareDto {
    public Long id;
    public HardwareType type;
    public String model;
    public String serialNumber;
    public HardwareStatus status;
    public LocalDate purchaseDate;
    public LocalDate warrantyEndDate;
    public Long siteId;
}


