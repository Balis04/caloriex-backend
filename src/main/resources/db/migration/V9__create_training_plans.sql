CREATE TABLE training_plans (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    training_request_id UUID NOT NULL UNIQUE,
    coach_user_id UUID NOT NULL,
    requester_user_id UUID NOT NULL,
    plan_name VARCHAR(255),
    file_name VARCHAR(255) NOT NULL,
    file_url VARCHAR(500) NOT NULL,
    content_type VARCHAR(100),
    file_size_bytes BIGINT,
    uploaded_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE,
    CONSTRAINT fk_training_plans_training_request
        FOREIGN KEY (training_request_id) REFERENCES training_requests(id) ON DELETE CASCADE,
    CONSTRAINT fk_training_plans_coach_user
        FOREIGN KEY (coach_user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_training_plans_requester_user
        FOREIGN KEY (requester_user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE INDEX idx_training_plans_coach_user_id ON training_plans (coach_user_id);
CREATE INDEX idx_training_plans_requester_user_id ON training_plans (requester_user_id);

ALTER TABLE training_requests
    DROP CONSTRAINT IF EXISTS chk_training_requests_status;

ALTER TABLE training_requests
    ADD CONSTRAINT chk_training_requests_status
        CHECK (status IN ('PENDING', 'APPROVED', 'REJECTED', 'CLOSED'));
