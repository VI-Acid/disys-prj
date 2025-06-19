CREATE TABLE energy_usage (
                              id SERIAL PRIMARY KEY,
                              timestamp TIMESTAMP NOT NULL,
                              kwh DOUBLE PRECISION NOT NULL
);

CREATE TABLE energy_production (
                                   id SERIAL PRIMARY KEY,
                                   timestamp TIMESTAMP NOT NULL,
                                   kwh DOUBLE PRECISION NOT NULL
);

CREATE TABLE current_percentage (
                                    id SERIAL PRIMARY KEY,
                                    timestamp TIMESTAMP NOT NULL,
                                    percentage DOUBLE PRECISION NOT NULL
);
