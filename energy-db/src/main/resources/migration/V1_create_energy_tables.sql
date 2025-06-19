CREATE TABLE energy_usage (
                              hour TIMESTAMP PRIMARY KEY,
                              community_produced DOUBLE PRECISION NOT NULL,
                              community_used DOUBLE PRECISION NOT NULL,
                              grid_used DOUBLE PRECISION NOT NULL
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
