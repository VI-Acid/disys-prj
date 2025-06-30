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


ALTER TABLE energy_usage
    ALTER COLUMN hour TYPE timestamp(0) without time zone;

ALTER TABLE current_percentage
    ALTER COLUMN hour TYPE timestamp(0) without time zone;
