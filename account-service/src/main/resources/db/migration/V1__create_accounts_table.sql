CREATE TABLE accounts (
                          id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                          account_number  VARCHAR(20)  NOT NULL UNIQUE,
                          user_id         UUID         NOT NULL,
                          type            VARCHAR(20)  NOT NULL,
                          currency        VARCHAR(3)   NOT NULL,
                          balance         DECIMAL(15,2) NOT NULL DEFAULT 0.00,
                          status          VARCHAR(20)  NOT NULL DEFAULT 'ACTIVE',
                          created_at      TIMESTAMP    NOT NULL DEFAULT NOW(),
                          updated_at      TIMESTAMP    NOT NULL DEFAULT NOW(),
                          version         BIGINT       NOT NULL DEFAULT 0
);

CREATE INDEX idx_accounts_user_id ON accounts(user_id);
CREATE INDEX idx_accounts_number ON accounts(account_number);
CREATE INDEX idx_accounts_status ON accounts(status);