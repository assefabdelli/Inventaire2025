package com.example.demo.entite;

import com.example.demo.enums.HardwareStatus;
import com.example.demo.enums.HardwareType;
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
import java.time.LocalDate;

@Entity
@Table(name = "hardware")
public class Hardware {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private HardwareType type;

    @Column(nullable = false)
    private String model;

    @Column(nullable = false)
    private String serialNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private HardwareStatus status = HardwareStatus.OPERATIONAL;

    @Column(nullable = false)
    private LocalDate purchaseDate;

    @Column(nullable = false)
    private LocalDate warrantyEndDate;

    @ManyToOne(optional = false)
    @JoinColumn(name = "site_id", nullable = false)
    private Site site;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public HardwareType getType() { return type; }
    public void setType(HardwareType type) { this.type = type; }

    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }

    public String getSerialNumber() { return serialNumber; }
    public void setSerialNumber(String serialNumber) { this.serialNumber = serialNumber; }

    public HardwareStatus getStatus() { return status; }
    public void setStatus(HardwareStatus status) { this.status = status; }

    public LocalDate getPurchaseDate() { return purchaseDate; }
    public void setPurchaseDate(LocalDate purchaseDate) { this.purchaseDate = purchaseDate; }

    public LocalDate getWarrantyEndDate() { return warrantyEndDate; }
    public void setWarrantyEndDate(LocalDate warrantyEndDate) { this.warrantyEndDate = warrantyEndDate; }

    public Site getSite() { return site; }
    public void setSite(Site site) { this.site = site; }
}


