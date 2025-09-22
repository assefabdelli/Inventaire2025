package com.example.demo.entite;

import com.example.demo.enums.DeploymentStatus;
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
import java.time.Instant;

@Entity
@Table(name = "deployment_task")
public class DeploymentTask {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "vm_id", nullable = false)
    private VirtualMachine vm;

    @ManyToOne(optional = false)
    @JoinColumn(name = "requested_by", nullable = false)
    private User requestedBy;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private DeploymentStatus status = DeploymentStatus.PENDING;

    @Column(nullable = false)
    private Instant createdAt = Instant.now();

    @Column
    private Instant completedAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public VirtualMachine getVm() { return vm; }
    public void setVm(VirtualMachine vm) { this.vm = vm; }

    public User getRequestedBy() { return requestedBy; }
    public void setRequestedBy(User requestedBy) { this.requestedBy = requestedBy; }

    public DeploymentStatus getStatus() { return status; }
    public void setStatus(DeploymentStatus status) { this.status = status; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getCompletedAt() { return completedAt; }
    public void setCompletedAt(Instant completedAt) { this.completedAt = completedAt; }
}


