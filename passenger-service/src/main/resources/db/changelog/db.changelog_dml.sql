-- liquibase formatted sql

-- changeset author:dml-001
INSERT INTO passengers (name, email, phone) VALUES
                                                ('Иван', 'ivan.ivanov@example.com', '+375291234567'),
                                                ('Петр', 'petr.petrov@example.com', '+375332345678'),
                                                ('Анна', 'anna.sidorova@example.com', '+375443456789'),
                                                ('Мария', 'maria.kovaleva@example.com', '+375254567890'),
                                                ('Сергей', 'sergey.novikov@example.com', '+375295678901')
ON CONFLICT (email) DO NOTHING;

-- changeset author:dml-002 splitStatements:false runOnChange:true
CREATE OR REPLACE FUNCTION update_updated_at_column()
    RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- changeset author:dml-003 runOnChange:true
DROP TRIGGER IF EXISTS update_passengers_updated_at ON passengers;

-- changeset author:dml-004 runOnChange:true
CREATE TRIGGER update_passengers_updated_at
    BEFORE UPDATE ON passengers
    FOR EACH ROW
EXECUTE FUNCTION update_updated_at_column();
