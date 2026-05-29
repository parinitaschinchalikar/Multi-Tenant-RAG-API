package com.ragapi.multitenant.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "audit_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditLog {

    @Id
    @GeneratedValue(generator = "UUID")
    @Column(updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;

    @Column(nullable = false)
    private String question;

    @Column(columnDefinition = "TEXT")
    private String answer;

    @Column(name = "chunks_retrieved")
    private Integer chunksRetrieved;

    @Column(name = "queried_at")
    private LocalDateTime queriedAt;

    @PrePersist
    public void prePersist() {
        this.queriedAt = LocalDateTime.now();
    }
}