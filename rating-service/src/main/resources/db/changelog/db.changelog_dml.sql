-- liquibase formatted sql

-- changeset author:1
INSERT INTO ratings (trip_id, rater_type, score, comment) VALUES
(1, 'PASSENGER',  5, 'Отличный водитель, аккуратно вел машину'),
(1, 'DRIVER', 4, 'Вежливый пассажир, вовремя вышел'),
(2, 'PASSENGER',  4, 'Хорошая поездка, но немного спешил'),
(2, 'DRIVER',  5, 'Идеальный пассажир'),
(3, 'PASSENGER', 3, 'Машина не очень чистая'),
(3, 'DRIVER', 2, 'Опоздал на 15 минут'),
(4, 'PASSENGER',  5, 'Супер! Рекомендую'),
(4, 'DRIVER',  4, 'Все хорошо'),
(5, 'PASSENGER',  1, 'Ужасный сервис, водитель грубил'),
(5, 'DRIVER',  1, 'Пассажир был пьян и вел себя неадекватно')
ON CONFLICT DO NOTHING;