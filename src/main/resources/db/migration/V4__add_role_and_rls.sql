ALTER TABLE user_accounts ADD COLUMN role VARCHAR(20) NOT NULL DEFAULT 'WORKER';
ALTER TABLE user_accounts ALTER COLUMN role DROP DEFAULT;
ALTER TABLE user_accounts
    ADD CONSTRAINT chk_user_accounts_role CHECK (role IN ('WORKER', 'EMPLOYER', 'ADMIN'));

ALTER TABLE user_accounts DROP CONSTRAINT chk_user_accounts_status;
ALTER TABLE user_accounts
    ADD CONSTRAINT chk_user_accounts_status
        CHECK (account_status IN ('ACTIVE', 'LOCKED', 'DISABLED', 'PASSWORD_EXPIRED'));

ALTER TABLE user_accounts ENABLE ROW LEVEL SECURITY;