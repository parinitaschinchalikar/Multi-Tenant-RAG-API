package com.ragapi.multitenant.repository;

import com.ragapi.multitenant.model.Document;
import com.ragapi.multitenant.model.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface DocumentRepository extends JpaRepository<Document, UUID>
{
    List<Document> findAllByTenant(Tenant tenant);
    long countByTenant(Tenant tenant);
}