-- liquibase formatted sql

-- changeset author:1
CREATE TABLE IF NOT EXISTS ratings (
    id BIGSERIAL PRIMARY KEY,
    trip_id BIGINT NOT NULL,
    rater_type VARCHAR(20) NOT NULL CHECK (rater_type IN ('DRIVER', 'PASSENGER')),
    score INTEGER NOT NULL CHECK (score BETWEEN 1 AND 5),
    comment VARCHAR(1000),

    UNIQUE(trip_id, rater_type)

    );

-- changeset author:2
CREATE INDEX idx_ratings_trip_id ON ratings(trip_id);
