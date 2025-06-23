CREATE TABLE energy_usage (
                              hour TIMESTAMP PRIMARY KEY,
                              community_produced DOUBLE PRECISION NOT NULL,
                              community_used DOUBLE PRECISION NOT NULL,
                              grid_used DOUBLE PRECISION NOT NULL
);

CREATE TABLE current_percentage (
                                    hour TIMESTAMP PRIMARY KEY,
                                    community_depleted DOUBLE PRECISION,
                                    grid_portion DOUBLE PRECISION
);
