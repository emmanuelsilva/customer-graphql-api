CREATE TABLE IF NOT EXISTS customers (
    id serial PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    state VARCHAR(255) NOT NULL
);