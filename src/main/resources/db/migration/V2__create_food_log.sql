CREATE TABLE food_logs (
                           id UUID PRIMARY KEY default gen_random_uuid(),
                           auth0_id VARCHAR(255) NOT NULL,
                           meal_time VARCHAR(50) NOT NULL,
                           food_name VARCHAR(255) NOT NULL,
                           amount DOUBLE PRECISION,
                           unit VARCHAR(50) NOT NULL,
                           calories DOUBLE PRECISION,
                           protein DOUBLE PRECISION,
                           carbohydrates DOUBLE PRECISION,
                           fat DOUBLE PRECISION,
                           created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                           consumed_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                           updated_at TIMESTAMP,
                           updated_by VARCHAR(255)
);

CREATE INDEX idx_auth0_date ON food_logs (auth0_id, consumed_at);