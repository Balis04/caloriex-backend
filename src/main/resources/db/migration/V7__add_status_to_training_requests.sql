ALTER TABLE training_requests
    ADD COLUMN status VARCHAR(20) NOT NULL DEFAULT 'PENDING';

ALTER TABLE training_requests
    ADD CONSTRAINT chk_training_requests_status
        CHECK (status IN ('PENDING', 'APPROVED', 'REJECTED'));
