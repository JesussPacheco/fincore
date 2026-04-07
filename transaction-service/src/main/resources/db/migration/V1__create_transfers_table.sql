CREATE TABLE transfers (
                           id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                           source_account_id   UUID            NOT NULL,
                           target_account_id   UUID            NOT NULL,
                           amount              DECIMAL(15,2)   NOT NULL CHECK (amount > 0),
                           currency            VARCHAR(3)      NOT NULL,
                           status              VARCHAR(20)     NOT NULL DEFAULT 'PENDING',
                           idempotency_key     VARCHAR(255)    NOT NULL UNIQUE,
                           failure_reason      VARCHAR(500),
                           created_at          TIMESTAMP       NOT NULL DEFAULT NOW(),
                           updated_at          TIMESTAMP       NOT NULL DEFAULT NOW(),
                           version             BIGINT          NOT NULL DEFAULT 0
);

CREATE INDEX idx_transfers_source     ON transfers(source_account_id);
CREATE INDEX idx_transfers_status     ON transfers(status);
CREATE INDEX idx_transfers_idempotency ON transfers(idempotency_key);