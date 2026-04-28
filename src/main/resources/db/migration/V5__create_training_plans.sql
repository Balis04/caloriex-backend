CREATE TABLE training_plans (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    training_request_id UUID NOT NULL UNIQUE,
    plan_name VARCHAR(255) not null ,
    file_name VARCHAR(255) NOT NULL,
    storage_key VARCHAR(500) NOT NULL,
    content_type VARCHAR(100) not null ,
    file_size_bytes BIGINT not null ,
    plan_description TEXT not null ,
    uploaded_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE,
    CONSTRAINT fk_training_plans_training_request
        FOREIGN KEY (training_request_id) REFERENCES training_requests(id) ON DELETE CASCADE
);
