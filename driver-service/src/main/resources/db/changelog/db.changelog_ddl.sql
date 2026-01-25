-- liquibase formatted sql

-- changeset author:ddl-001
CREATE TABLE IF NOT EXISTS cars (
                                    id BIGSERIAL PRIMARY KEY,
                                    brand VARCHAR(50) NOT NULL,
    color VARCHAR(30) NOT NULL,
    license_plate VARCHAR(20) NOT NULL UNIQUE
    );

-- changeset author:ddl-002
CREATE TABLE IF NOT EXISTS drivers (
                                       id BIGSERIAL PRIMARY KEY,
                                       name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    phone VARCHAR(20) NOT NULL UNIQUE,  -- Добавил UNIQUE как в модели
    car_id BIGINT NOT NULL UNIQUE,
    deleted BOOLEAN DEFAULT FALSE NOT NULL,
    CONSTRAINT fk_driver_car FOREIGN KEY (car_id)
    REFERENCES cars(id) ON DELETE CASCADE
    );

-- changeset author:ddl-003
-- Индексы
CREATE INDEX IF NOT EXISTS idx_drivers_email ON drivers(email);
CREATE INDEX IF NOT EXISTS idx_cars_license_plate ON cars(license_plate);

-- changeset author:ddl-004
-- Проверочные ограничения
ALTER TABLE drivers
    ADD CONSTRAINT chk_driver_phone_format
        CHECK (phone ~ '^\+375(29|33|44|25)\d{7}$');

ALTER TABLE drivers
    ADD CONSTRAINT chk_driver_email_format
        CHECK (email ~* '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$');

ALTER TABLE cars
    ADD CONSTRAINT chk_car_license_plate_format
        CHECK (license_plate ~ '^[0-9]{4} [A-Z]{2}-[1-7]$');