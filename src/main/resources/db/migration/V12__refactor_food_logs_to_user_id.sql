ALTER TABLE food_logs
    ADD COLUMN user_id UUID;

UPDATE food_logs fl
SET user_id = u.id
FROM users u
WHERE fl.auth0_id = u.auth0_id;

ALTER TABLE food_logs
    ALTER COLUMN user_id SET NOT NULL;

ALTER TABLE food_logs
    ADD CONSTRAINT fk_food_logs_user_id
        FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE;

ALTER TABLE food_logs
    ADD COLUMN updated_by_user_id UUID;

UPDATE food_logs fl
SET updated_by_user_id = u.id
FROM users u
WHERE fl.updated_by = u.auth0_id;

ALTER TABLE food_logs
    DROP COLUMN updated_by;

ALTER TABLE food_logs
    RENAME COLUMN updated_by_user_id TO updated_by;

ALTER TABLE food_logs
    DROP COLUMN auth0_id;

DROP INDEX IF EXISTS idx_auth0_date;
DROP INDEX IF EXISTS idx_auth_id_consumed;

CREATE INDEX idx_food_logs_user_id_consumed_at ON food_logs (user_id, consumed_at);
