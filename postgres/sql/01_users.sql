CREATE EXTENSION postgis;
CREATE TABLE users
(
    id         SERIAL PRIMARY KEY,
    first_name VARCHAR(50),
    last_name  VARCHAR(50),
    interests jsonb
);