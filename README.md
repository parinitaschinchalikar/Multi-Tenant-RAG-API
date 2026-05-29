# Multi-Tenant Document Intelligence API

A production-grade Spring Boot service that lets multiple clients (tenants) upload their own documents and query them via a secure REST API. Each tenant gets a fully isolated knowledge base — no data leakage across tenants. Built for enterprise RAG deployment.

---

## What It Does

Enterprise AI deployments almost always require data isolation between clients. This service provides RAG-as-a-service with proper multi-tenancy: each tenant uploads their documents, gets their own vector namespace, and can only ever query their own data. Authentication is JWT-based, and every query is audit-logged.

**Core capabilities:**
- Tenant registration and JWT-based authentication
- Document upload → automatic chunking → embedding → isolated vector storage
- Query endpoint that retrieves from the correct tenant's namespace only
- LLM-generated answers grounded in retrieved document chunks with citations
- Audit log of every query per tenant
- Admin dashboard endpoint with usage stats per tenant

---

## Architecture

```
Client Request
      │
      ▼
┌──────────────────┐
│   API Gateway    │  Spring Boot · JWT Auth Filter
│   (Spring Boot)  │
└──────┬───────────┘
       │
  ┌────┴─────────────────────┐
  │                          │
  ▼                          ▼
┌──────────────┐    ┌─────────────────────┐
│  Document    │    │   Query Service      │
│  Service     │    │                     │
│  - Chunk     │    │  1. Embed query      │
│  - Embed     │    │  2. Search tenant    │
│  - Store     │    │     namespace        │
└──────┬───────┘    │  3. LLM generates   │
       │            │     answer           │
       ▼            └──────────┬──────────┘
┌──────────────┐               │
│   Milvus     │◄──────────────┘
│ Vector Store │  Per-tenant namespaces
│ (namespaced) │
└──────────────┘
       │
       ▼
┌──────────────┐
│  PostgreSQL  │  Tenant registry · Document metadata · Audit log
└──────────────┘
```

---

## Tech Stack

| Layer | Technology |
|---|---|
| Backend Framework | Spring Boot 3.x |
| Security | Spring Security · JWT |
| LLM Integration | LangChain4j |
| Language Model | OpenAI GPT-4o |
| Vector Store | Milvus |
| Embeddings | OpenAI text-embedding-3-small |
| Database | PostgreSQL |
| ORM | Hibernate / Spring Data JPA |
| Containerization | Docker · Docker Compose |
| Build Tool | Maven |

---

## Features

- **Tenant isolation** — each tenant's vectors stored in a separate Milvus collection/namespace
- **JWT auth** — stateless authentication, token issued on login, validated on every request
- **Auto-chunking** — uploaded documents split into configurable chunk sizes with overlap
- **Cited answers** — LLM responses include source document + chunk references
- **Audit logging** — every query logged with tenant ID, timestamp, retrieved chunks, and response
- **Admin endpoints** — query volume, document count, storage usage per tenant
- **Docker Compose** — one command to run the entire stack locally

---

## Project Structure

```
multi-tenant-rag-api/
├── src/main/java/com/ragapi/
│   ├── config/
│   │   ├── SecurityConfig.java       # JWT filter chain
│   │   ├── MilvusConfig.java         # Vector store connection
│   │   └── JwtConfig.java
│   ├── controller/
│   │   ├── AuthController.java       # Register, login
│   │   ├── DocumentController.java   # Upload, list, delete docs
│   │   ├── QueryController.java      # Ask questions
│   │   └── AdminController.java      # Tenant stats
│   ├── service/
│   │   ├── TenantService.java        # Tenant management
│   │   ├── DocumentService.java      # Chunking + embedding + storage
│   │   ├── QueryService.java         # Retrieval + LLM generation
│   │   └── AuditService.java         # Query logging
│   ├── repository/
│   │   ├── TenantRepository.java
│   │   ├── DocumentRepository.java
│   │   └── AuditLogRepository.java
│   ├── model/
│   │   ├── Tenant.java
│   │   ├── Document.java
│   │   └── AuditLog.java
│   └── dto/
│       ├── QueryRequest.java
│       └── QueryResponse.java
├── src/main/resources/
│   └── application.yml
├── docker-compose.yml            # Postgres + Milvus + App
├── Dockerfile
└── pom.xml
```

---

## Getting Started

### Prerequisites
- Java 17+
- Docker + Docker Compose
- OpenAI API key

### Run with Docker Compose

```bash
# Clone the repo
git clone https://github.com/parinitaschinchalikar/multi-tenant-rag-api.git
cd multi-tenant-rag-api

# Configure environment
cp .env.example .env
# Add OPENAI_API_KEY, JWT_SECRET to .env

# Start everything (Postgres + Milvus + App)
docker-compose up --build
```

API available at `http://localhost:8080`

---

## API Reference

### Register a tenant
```http
POST /auth/register
Content-Type: application/json

{
  "tenantName": "acme-corp",
  "email": "admin@acme.com",
  "password": "securepassword"
}
```

**Response:**
```json
{
  "tenantId": "tenant_acme_corp",
  "token": "eyJhbGciOiJIUzI1NiJ9..."
}
```

### Upload a document
```http
POST /documents/upload
Authorization: Bearer <token>
Content-Type: multipart/form-data

file: quarterly_report.pdf
```

**Response:**
```json
{
  "documentId": "doc_8f3a2c",
  "fileName": "quarterly_report.pdf",
  "chunks": 42,
  "status": "indexed"
}
```

### Query your knowledge base
```http
POST /query
Authorization: Bearer <token>
Content-Type: application/json

{
  "question": "What was the revenue growth in Q3?",
  "topK": 3
}
```

**Response:**
```json
{
  "answer": "Q3 revenue grew by 18% year-over-year, reaching $4.2M according to the quarterly report.",
  "sources": [
    {
      "documentId": "doc_8f3a2c",
      "fileName": "quarterly_report.pdf",
      "chunkIndex": 12,
      "relevanceScore": 0.91
    }
  ],
  "queryId": "q_log_1029"
}
```

### Admin — tenant usage stats
```http
GET /admin/stats
Authorization: Bearer <admin_token>
```

**Response:**
```json
{
  "tenants": [
    {
      "tenantId": "tenant_acme_corp",
      "documentsUploaded": 14,
      "totalChunks": 892,
      "queriesLast30Days": 340
    }
  ]
}
```

---

## Environment Variables

```env
OPENAI_API_KEY=your_openai_api_key
JWT_SECRET=your_jwt_secret_min_32_chars
MILVUS_HOST=localhost
MILVUS_PORT=19530
POSTGRES_URL=jdbc:postgresql://localhost:5432/ragdb
POSTGRES_USER=raguser
POSTGRES_PASSWORD=ragpassword
CHUNK_SIZE=512
CHUNK_OVERLAP=50
```

---

## License

MIT
