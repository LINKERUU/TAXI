--changeset author:ddl-001
CREATE TYPE trip_status AS ENUM (
    'CREATED', 'ACCEPTED', 'DRIVER_EN_ROUTE',
    'PASSENGER_ON_BOARD', 'IN_PROGRESS', 'COMPLETED', 'CANCELLED'
    );

CREATE TABLE IF NOT EXISTS outbox_event
(
    id           BIGSERIAL PRIMARY KEY,
    aggregate_id BIGINT                     NOT NULL,
    event_type   VARCHAR(255)               NOT NULL,
    payload      TEXT,
    created_at   TIMESTAMP(6) DEFAULT NOW() NOT NULL,
    processed    BOOLEAN      DEFAULT FALSE
);

CREATE TABLE IF NOT EXISTS trips
(
    id                   BIGSERIAL PRIMARY KEY,
    driver_id            BIGINT         NOT NULL,
    passenger_id         BIGINT         NOT NULL,
    pickup_city          VARCHAR(100)   NOT NULL,
    pickup_street        VARCHAR(200)   NOT NULL,
    pickup_building      VARCHAR(20)    NOT NULL,
    destination_city     VARCHAR(100)   NOT NULL,
    destination_street   VARCHAR(200)   NOT NULL,
    destination_building VARCHAR(20)    NOT NULL,
    status               trip_status    NOT NULL DEFAULT 'CREATED',
    order_date_time      TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    price                DECIMAL(10, 2) NOT NULL
);

--changeset author:ddl-002
CREATE INDEX idx_trips_driver_id ON trips (driver_id);
CREATE INDEX idx_trips_passenger_id ON trips (passenger_id);
CREATE INDEX idx_trips_order_date_time ON trips (order_date_time);
CREATE INDEX idx_trips_driver_status ON trips (driver_id, status);
CREATE INDEX idx_trips_passenger_status ON trips (passenger_id, status);