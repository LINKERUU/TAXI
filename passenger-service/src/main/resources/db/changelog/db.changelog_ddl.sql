-- liquibase formatted sql

--changeset author:ddl-001
CREATE TABLE IF NOT EXISTS passengers (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(255) NOT NULL,
    phone VARCHAR(13) NOT NULL,
    deleted BOOLEAN DEFAULT FALSE NOT NULL
);

--changeset author:ddl-002
CREATE UNIQUE INDEX ux_passengers_email_active
    ON passengers(email)
    WHERE deleted=false;

--changeset author:ddl-003
CREATE  UNIQUE INDEX ux_passengers_phone_active
    ON passengers(phone)
    WHERE deleted=false;