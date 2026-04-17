CREATE TABLE food_logs (
                           id UUID PRIMARY KEY default gen_random_uuid(),
                           user_id UUID NOT NULL,
                           meal_time VARCHAR(50) NOT NULL,
                           food_name VARCHAR(255) NOT NULL,
                           amount DOUBLE PRECISION not null ,
                           unit VARCHAR(50) NOT NULL,
                           calories DOUBLE PRECISION not null ,
                           protein DOUBLE PRECISION not null ,
                           carbohydrates DOUBLE PRECISION not null ,
                           fat DOUBLE PRECISION not null ,
                           created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                           consumed_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                           updated_at TIMESTAMP
);

CREATE INDEX idx_food_logs_user_id_consumed_at ON food_logs (user_id, consumed_at);