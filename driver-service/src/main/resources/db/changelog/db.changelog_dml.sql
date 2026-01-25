-- liquibase formatted sql

-- changeset author:dml-001
INSERT INTO cars (brand, color, license_plate) VALUES
                                                   ('Volkswagen', 'Белый', '1234 AB-1'),
                                                   ('Toyota', 'Черный', '5678 CD-2'),
                                                   ('Mercedes', 'Серый', '9012 EF-3'),
                                                   ('BMW', 'Синий', '3456 GH-4'),
                                                   ('Audi', 'Красный', '7890 IJ-5')
    ON CONFLICT (license_plate) DO NOTHING;

-- changeset author:dml-002
INSERT INTO drivers (name, email, phone, car_id) VALUES
                                                     ('Иван', 'ivan.petrov@example.com', '+375291234567', 1),
                                                     ('Алексей', 'alexey.sidorov@example.com', '+375332345678', 2),
                                                     ('Мария', 'maria.ivanova@example.com', '+375443456789', 3),
                                                     ('Дмитрий', 'dmitry.kozlov@example.com', '+375254567890', 4),
                                                     ('Елена', 'elena.smirnova@example.com', '+375295678901', 5)
    ON CONFLICT (email) DO NOTHING;
