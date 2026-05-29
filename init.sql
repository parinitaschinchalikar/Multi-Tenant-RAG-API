-- Enable UUID generation
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- Tenant table
CREATE TABLE IF NOT EXISTS tenants
(
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_name VARCHAR(100) UNIQUE NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT NOW()
);

-- Document registry
CREATE TABLE IF NOT EXISTS documents
(
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id UUID REFERENCES tenants(id) ON DELETE CASCADE,
    file_name VARCHAR(255) NOT NULL,
    chunk_count INT DEFAULT 0,
    status VARCHAR(50) DEFAULT 'processing',
    uploaded_at TIMESTAMP DEFAULT NOW()
);

-- Audit log
CREATE TABLE IF NOT EXISTS audit_logs
(
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id UUID REFERENCES tenants(id) ON DELETE CASCADE,
    question TEXT NOT NULL,
    answer TEXT,
    chunks_retrieved INT DEFAULT 0,
    queried_at TIMESTAMP DEFAULT NOW()
);