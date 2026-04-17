CREATE TABLE coach_profiles (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL UNIQUE,
    training_started_at DATE not null ,
    short_description TEXT not null ,
    training_format VARCHAR(20) not null ,
    price_from INTEGER not null ,
    price_to INTEGER not null ,
    currency VARCHAR(10) not null ,
    max_capacity INTEGER not null ,
    contact_note TEXT not null ,
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
    start_time TIME not null ,
    end_time TIME not null ,
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
    certificate_name VARCHAR(255) not null ,
    issuer VARCHAR(255) not null ,
    issued_at TIMESTAMP WITH TIME ZONE not null ,
    file_url VARCHAR(500) NOT NULL,
    content_type VARCHAR(100) not null ,
    file_size_bytes BIGINT not null ,
    uploaded_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE,
    CONSTRAINT fk_coach_certificates_profile
        FOREIGN KEY (coach_profile_id) REFERENCES coach_profiles(id) ON DELETE CASCADE
);

CREATE INDEX idx_coach_availabilities_profile_id ON coach_availabilities (coach_profile_id);
CREATE INDEX idx_coach_certificates_profile_id ON coach_certificates (coach_profile_id);
