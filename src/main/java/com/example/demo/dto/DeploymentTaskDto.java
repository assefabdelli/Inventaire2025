package com.example.demo.dto;

import com.example.demo.enums.DeploymentStatus;
import java.time.Instant;

public class DeploymentTaskDto {
    public Long id;
    public Long vmId;
    public Long requestedById;
    public DeploymentStatus status;
    public Instant createdAt;
    public Instant completedAt;
}


