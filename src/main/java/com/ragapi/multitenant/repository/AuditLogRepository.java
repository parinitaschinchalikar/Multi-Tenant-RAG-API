package com.ragapi.multitenant.repository;

import com.ragapi.multitenant.model.AuditLog;
import com.ragapi.multitenant.model.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface AuditLogRepository extends JpaRepository<AuditLog, UUID>
{
    List<AuditLog> findAllByTenant(Tenant tenant);
    long countByTenant(Tenant tenant);
}