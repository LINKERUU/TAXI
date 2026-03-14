-- liquibase formatted sql

-- changeset author:dml-001
INSERT INTO passengers(name, email, phone)
VALUES ('Иван', 'ivan.ivanov@example.com', '+375291234567'),
       ('Петр', 'petr.petrov@example.com', '+375332345678'),
       ('Анна', 'anna.sidorova@example.com', '+375443456789'),
       ('Мария', 'maria.kovaleva@example.com', '+375254567890'),
       ('Сергей', 'sergey.novikov@example.com', '+375295678901')
ON CONFLICT DO NOTHING;
