CREATE TABLE training_requests (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    requester_user_id UUID NOT NULL,
    coach_profile_id UUID NOT NULL,
    weekly_training_count INTEGER NOT NULL,
    session_duration_minutes INTEGER NOT NULL,
    preferred_location VARCHAR(255) NOT NULL,
    coach_response TEXT,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    request_description TEXT not null ,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE,
    CONSTRAINT fk_training_requests_requester_user
        FOREIGN KEY (requester_user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_training_requests_coach_profile
        FOREIGN KEY (coach_profile_id) REFERENCES coach_profiles(id) ON DELETE CASCADE,
    CONSTRAINT chk_training_requests_weekly_training_count
        CHECK (weekly_training_count BETWEEN 1 AND 14),
    CONSTRAINT chk_training_requests_session_duration_minutes
        CHECK (session_duration_minutes BETWEEN 15 AND 480),
    CONSTRAINT chk_training_requests_status
        CHECK (status IN ('PENDING', 'APPROVED', 'REJECTED', 'CLOSED'))
);

CREATE INDEX idx_training_requests_requester_user_id ON training_requests (requester_user_id);
CREATE INDEX idx_training_requests_coach_profile_id ON training_requests (coach_profile_id);
