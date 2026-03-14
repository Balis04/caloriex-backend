CREATE TABLE coach_profiles (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL UNIQUE,
    training_started_at DATE,
    short_description TEXT,
    training_format VARCHAR(20),
    price_from INTEGER,
    price_to INTEGER,
    currency VARCHAR(10),
    max_capacity INTEGER,
    contact_note TEXT,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE,
    CONSTRAINT fk_coach_profiles_user
        FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT chk_coach_profiles_training_format
        CHECK (training_format IS NULL OR training_format IN ('ONLINE', 'HYBRID', 'IN_PERSON')),
    CONSTRAINT chk_coach_profiles_currency
        CHECK (currency IS NULL OR currency IN ('HUF', 'EUR', 'USD'))
);

CREATE TABLE coach_availabilities (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    coach_profile_id UUID NOT NULL,
    day_of_week VARCHAR(20) NOT NULL,
    is_available BOOLEAN NOT NULL DEFAULT FALSE,
    start_time TIME,
    end_time TIME,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE,
    CONSTRAINT fk_coach_availabilities_profile
        FOREIGN KEY (coach_profile_id) REFERENCES coach_profiles(id) ON DELETE CASCADE,
    CONSTRAINT uq_coach_availabilities_profile_day
        UNIQUE (coach_profile_id, day_of_week),
    CONSTRAINT chk_coach_availabilities_day_of_week
        CHECK (day_of_week IN ('MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY', 'FRIDAY', 'SATURDAY', 'SUNDAY')),
    CONSTRAINT chk_coach_availabilities_time_order
        CHECK (
            (is_available = FALSE AND start_time IS NULL AND end_time IS NULL)
            OR (is_available = TRUE AND start_time IS NOT NULL AND end_time IS NOT NULL AND start_time < end_time)
        )
);

CREATE TABLE coach_certificates (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    coach_profile_id UUID NOT NULL,
    file_name VARCHAR(255) NOT NULL,
    certificate_name VARCHAR(255),
    issuer VARCHAR(255),
    issued_at TIMESTAMP WITH TIME ZONE,
    file_url VARCHAR(500) NOT NULL,
    content_type VARCHAR(100),
    file_size_bytes BIGINT,
    uploaded_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE,
    CONSTRAINT fk_coach_certificates_profile
        FOREIGN KEY (coach_profile_id) REFERENCES coach_profiles(id) ON DELETE CASCADE
);

CREATE INDEX idx_coach_availabilities_profile_id ON coach_availabilities (coach_profile_id);
CREATE INDEX idx_coach_certificates_profile_id ON coach_certificates (coach_profile_id);
