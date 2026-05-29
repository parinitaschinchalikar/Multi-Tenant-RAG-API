package com.ragapi.multitenant.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name="tenants")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Tenant
{
    @Id
    @Column(updatable=false, nullable=false)
    private UUID id;

    @Column(name="tenant_name", unique=true, nullable=false)
    private String tenantName;

    @Column(unique=true, nullable=false)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist()
    {
        this.createdAt = LocalDateTime.now();
    }
}
