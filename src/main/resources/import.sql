-- =============================================
-- EXTENSIONS
-- =============================================

CREATE EXTENSION IF NOT EXISTS vector;

-- =============================================
-- INDEXES
-- =============================================

CREATE INDEX IF NOT EXISTS idx_chunks_embedding
    ON document_chunks USING hnsw (embedding vector_cosine_ops);

-- =============================================
-- USERS
-- =============================================

INSERT INTO users (id, tenant_id, email, name, avatar_url, role, active, created_at, updated_at, last_login_at)
VALUES
    (gen_random_uuid(), null, 'admin@rag.com',   'Admin User',     null, 'ADMIN', true,  now(), now(), null),
    (gen_random_uuid(), null, 'mario@rag.com',   'Mario Rossi',    null, 'USER',  true,  now(), now(), null),
    (gen_random_uuid(), null, 'giulia@rag.com',  'Giulia Bianchi', null, 'USER',  true,  now(), now(), null),
    (gen_random_uuid(), null, 'luca@rag.com',    'Luca Verdi',     null, 'USER',  false, now(), now(), null);