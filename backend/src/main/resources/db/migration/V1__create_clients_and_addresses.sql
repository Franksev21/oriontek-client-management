-- V1__create_clients_and_addresses.sql
-- OrionTek: Client Management System

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE clients (
    id            UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    first_name    VARCHAR(100)        NOT NULL,
    last_name     VARCHAR(100)        NOT NULL,
    email         VARCHAR(255)        NOT NULL UNIQUE,
    phone         VARCHAR(20),
    created_at    TIMESTAMP           NOT NULL DEFAULT NOW(),
    updated_at    TIMESTAMP           NOT NULL DEFAULT NOW(),
    deleted_at    TIMESTAMP           NULL
);

CREATE TABLE addresses (
    id            UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    client_id     UUID                NOT NULL REFERENCES clients(id) ON DELETE CASCADE,
    street        VARCHAR(255)        NOT NULL,
    city          VARCHAR(100)        NOT NULL,
    state         VARCHAR(100),
    country       VARCHAR(100)        NOT NULL,
    zip_code      VARCHAR(20),
    is_primary    BOOLEAN             NOT NULL DEFAULT FALSE,
    created_at    TIMESTAMP           NOT NULL DEFAULT NOW(),
    updated_at    TIMESTAMP           NOT NULL DEFAULT NOW(),
    deleted_at    TIMESTAMP           NULL
);

-- Indexes for performance
CREATE INDEX idx_clients_email        ON clients(email) WHERE deleted_at IS NULL;
CREATE INDEX idx_clients_name         ON clients(first_name, last_name) WHERE deleted_at IS NULL;
CREATE INDEX idx_clients_deleted_at   ON clients(deleted_at);
CREATE INDEX idx_addresses_client_id  ON addresses(client_id) WHERE deleted_at IS NULL;

-- Seed data for testing
INSERT INTO clients (id, first_name, last_name, email, phone) VALUES
    ('a1b2c3d4-0000-0000-0000-000000000001', 'María',   'Rodríguez', 'maria@oriontek.com',  '809-555-0101'),
    ('a1b2c3d4-0000-0000-0000-000000000002', 'Juan',    'Pérez',     'juan@oriontek.com',   '809-555-0102'),
    ('a1b2c3d4-0000-0000-0000-000000000003', 'Ana',     'García',    'ana@oriontek.com',    '809-555-0103'),
    ('a1b2c3d4-0000-0000-0000-000000000004', 'Carlos',  'Martínez',  'carlos@oriontek.com', '809-555-0104'),
    ('a1b2c3d4-0000-0000-0000-000000000005', 'Laura',   'Sánchez',   'laura@oriontek.com',  '809-555-0105');

INSERT INTO addresses (client_id, street, city, state, country, zip_code, is_primary) VALUES
    ('a1b2c3d4-0000-0000-0000-000000000001', 'Calle Duarte 45',        'Santo Domingo', 'Distrito Nacional', 'República Dominicana', '10101', TRUE),
    ('a1b2c3d4-0000-0000-0000-000000000001', 'Av. 27 de Febrero 120',  'Santiago',      'Santiago',          'República Dominicana', '51000', FALSE),
    ('a1b2c3d4-0000-0000-0000-000000000002', 'Calle El Conde 88',      'Santo Domingo', 'Distrito Nacional', 'República Dominicana', '10100', TRUE),
    ('a1b2c3d4-0000-0000-0000-000000000003', 'Av. Independencia 200',  'Santo Domingo', 'Distrito Nacional', 'República Dominicana', '10205', TRUE),
    ('a1b2c3d4-0000-0000-0000-000000000003', 'Calle Las Mercedes 15',  'La Romana',     'La Romana',         'República Dominicana', '22000', FALSE),
    ('a1b2c3d4-0000-0000-0000-000000000003', 'Bulevar del Atlántico',  'Puerto Plata',  'Puerto Plata',      'República Dominicana', '57000', FALSE),
    ('a1b2c3d4-0000-0000-0000-000000000004', 'Calle Hostos 9',         'Santo Domingo', 'Distrito Nacional', 'República Dominicana', '10103', TRUE),
    ('a1b2c3d4-0000-0000-0000-000000000005', 'Av. Winston Churchill',  'Santo Domingo', 'Distrito Nacional', 'República Dominicana', '10148', TRUE);
