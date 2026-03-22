CREATE TABLE custom_foods (
                              id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                              user_id UUID NOT NULL,
                              name VARCHAR(255) NOT NULL,
                              calories DOUBLE PRECISION NOT NULL,
                              protein DOUBLE PRECISION NOT NULL,
                              fat DOUBLE PRECISION NOT NULL,
                              carbohydrates DOUBLE PRECISION NOT NULL,
                              created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                              updated_at TIMESTAMP,

                              CONSTRAINT fk_custom_foods_user
                                  FOREIGN KEY (user_id) REFERENCES users(id)
                                      ON DELETE CASCADE
);

CREATE INDEX idx_custom_foods_user_id ON custom_foods(user_id);
