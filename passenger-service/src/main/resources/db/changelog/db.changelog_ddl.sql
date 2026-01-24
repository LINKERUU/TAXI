-- liquibase formatted sql

--changeset author:ddl-001
CREATE TABLE IF NOT EXISTS passengers (
                                          id BIGSERIAL PRIMARY KEY,
                                          name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    phone VARCHAR(20) NOT NULL,
    deleted BOOLEAN DEFAULT FALSE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    );

--changeset author:ddl-002
CREATE INDEX IF NOT EXISTS idx_passengers_email ON passengers(email);

--changeset author:ddl-003
ALTER TABLE passengers
    ADD CONSTRAINT chk_passenger_email_format
        CHECK (email ~* '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$');

--changeset author:ddl-004
ALTER TABLE passengers
    ADD CONSTRAINT chk_passenger_name_length
        CHECK (LENGTH(name) >= 2 AND LENGTH(name) <= 100);
