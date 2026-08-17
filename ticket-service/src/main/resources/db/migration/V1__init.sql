CREATE TABLE events (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(1000),
    date_time TIMESTAMP NOT NULL,
    location VARCHAR(255) NOT NULL,
    total_tickets INTEGER NOT NULL,
    available_tickets INTEGER NOT NULL,
    price NUMERIC(10, 2) NOT NULL
);

CREATE TABLE reservations (
    id BIGSERIAL PRIMARY KEY,
    event_id BIGINT NOT NULL,
    quantity INTEGER NOT NULL,
    total_amount NUMERIC(10, 2) NOT NULL,
    status VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    expires_at TIMESTAMP NOT NULL
);

CREATE TABLE outbox_events (
    id BIGSERIAL PRIMARY KEY,
    aggregate_type VARCHAR(255) NOT NULL,
    aggregate_id BIGINT NOT NULL,
    event_type VARCHAR(255) NOT NULL,
    payload VARCHAR(2000) NOT NULL,
    created_at TIMESTAMP NOT NULL
);
