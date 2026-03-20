CREATE TABLE custom_foods (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    auth0_id VARCHAR(255) NOT NULL,
    name VARCHAR(255) NOT NULL,
    calories DOUBLE PRECISION NOT NULL,
    fat DOUBLE PRECISION NOT NULL,
    carbohydrates DOUBLE PRECISION NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE INDEX idx_custom_foods_auth0_id ON custom_foods(auth0_id);
