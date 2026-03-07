-- liquibase formatted sql

-- changeset author:1

INSERT INTO trips (
    driver_id,
    passenger_id,
    pickup_city,
    pickup_street,
    pickup_building,
    destination_city,
    destination_street,
    destination_building,
    status,
    order_date_time,
    price
) VALUES

(
    1, 1,
    'Минск', 'Ленина', '10',
    'Минск', 'Независимости', '50',
    'CREATED',
    NOW() - INTERVAL '10 minutes',
    15.50
),
(
    2, 3,
    'Гомель', 'Советская', '25',
    'Гомель', 'Ильича', '30',
    'ACCEPTED',
    NOW() - INTERVAL '30 minutes',
    12.75
),
(
    3, 2,
    'Брест', 'Московская', '15',
    'Минск', 'Притыцкого', '70',
    'DRIVER_EN_ROUTE',
    NOW() - INTERVAL '1 hour',
    45.20
),
(
    4, 4,
    'Витебск', 'Кирова', '5',
    'Витебск', 'Замковая', '12',
    'PASSENGER_ON_BOARD',
    NOW() - INTERVAL '45 minutes',
    8.90
),
(
    5, 5,
    'Могилев', 'Первомайская', '33',
    'Могилев', 'Ленинская', '21',
    'IN_PROGRESS',
    NOW() - INTERVAL '20 minutes',
    10.30
),

(
    1, 2,
    'Минск', 'Кальварийская', '40',
    'Минск', 'Тимирязева', '15',
    'COMPLETED',
    NOW() - INTERVAL '2 hours',
    18.00
),
(
    3, 1,
    'Брест', 'Гоголя', '8',
    'Брест', 'Пушкинская', '42',
    'COMPLETED',
    NOW() - INTERVAL '3 hours',
    9.50
),


(
    2, 4,
    'Гомель', 'Барыкина', '17',
    'Гомель', 'Фрунзе', '33',
    'CANCELLED',
    NOW() - INTERVAL '1 hour 30 minutes',
    14.20
),
(
    5, 3,
    'Могилев', 'Челюскинцев', '26',
    'Могилев', 'Пионерская', '9',
    'CANCELLED',
    NOW() - INTERVAL '40 minutes',
    11.80
),


(
    1, 5,
    'Минск', 'Притыцкого', '65',
    'Минск', 'аэропорт', '1',
    'CREATED',
    NOW() - INTERVAL '5 minutes',
    25.00
),
(
    4, 2,
    'Гродно', 'Ожешко', '22',
    'Гродно', 'Советская', '47',
    'ACCEPTED',
    NOW() - INTERVAL '15 minutes',
    7.50
)
    ON CONFLICT (id) DO NOTHING;
