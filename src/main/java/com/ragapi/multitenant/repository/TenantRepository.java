package com.ragapi.multitenant.repository;

import com.ragapi.multitenant.model.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface TenantRepository extends JpaRepository<Tenant, UUID>
{
    Optional<Tenant> findByEmail(String email);
    boolean existsByEmail(String email);
    boolean existsByTenantName(String tenantName);
}