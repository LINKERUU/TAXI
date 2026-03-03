-- liquibase formatted sql

-- changeset author:ddl-001
CREATE TABLE IF NOT EXISTS cars (
    id BIGSERIAL PRIMARY KEY,
    brand VARCHAR(50) NOT NULL,
    color VARCHAR(30) NOT NULL,
    license_plate VARCHAR(9) NOT NULL UNIQUE
    );

-- changeset author:ddl-002
CREATE TABLE IF NOT EXISTS drivers (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL,
    phone VARCHAR(13) NOT NULL,
    deleted BOOLEAN DEFAULT FALSE NOT NULL,
    car_id BIGINT NOT NULL UNIQUE,
    CONSTRAINT fk_driver_car FOREIGN KEY (car_id)
    REFERENCES cars(id) ON DELETE CASCADE
    );

-- changeset author:ddl-003
CREATE INDEX IF NOT EXISTS idx_drivers_email
    ON drivers(email);

CREATE INDEX IF NOT EXISTS idx_cars_license_plate
    ON cars(license_plate);

CREATE UNIQUE INDEX ux_drivers_email_active
    ON drivers(email)
    WHERE deleted = false;

CREATE UNIQUE INDEX ux_drivers_phone_active
    ON drivers(phone)
    WHERE deleted = false;


