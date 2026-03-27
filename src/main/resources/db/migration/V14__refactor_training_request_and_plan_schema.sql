ALTER TABLE training_requests
    RENAME COLUMN description TO request_description;

ALTER TABLE training_requests
    RENAME COLUMN coach_note TO coach_response;

ALTER TABLE training_plans
    RENAME COLUMN description TO plan_description;

DROP INDEX IF EXISTS idx_training_plans_coach_user_id;
DROP INDEX IF EXISTS idx_training_plans_requester_user_id;

ALTER TABLE training_plans
    DROP CONSTRAINT IF EXISTS fk_training_plans_coach_user;

ALTER TABLE training_plans
    DROP CONSTRAINT IF EXISTS fk_training_plans_requester_user;

ALTER TABLE training_plans
    DROP COLUMN IF EXISTS coach_user_id;

ALTER TABLE training_plans
    DROP COLUMN IF EXISTS requester_user_id;
