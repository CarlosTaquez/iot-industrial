ALTER TABLE operator_decisions ADD COLUMN IF NOT EXISTS decision_id VARCHAR(36);
UPDATE operator_decisions SET decision_id = gen_random_uuid()::text WHERE decision_id IS NULL;
ALTER TABLE operator_decisions ALTER COLUMN decision_id SET NOT NULL;
CREATE UNIQUE INDEX IF NOT EXISTS uq_operator_decisions_decision_id ON operator_decisions (decision_id);
ALTER TABLE operator_decisions ADD COLUMN IF NOT EXISTS status VARCHAR(20) NOT NULL DEFAULT 'DISPATCHED';
ALTER TABLE alerts ADD COLUMN IF NOT EXISTS status VARCHAR(20) NOT NULL DEFAULT 'PENDING';
CREATE TABLE IF NOT EXISTS alert_resolutions (
    alert_id VARCHAR(64) PRIMARY KEY,
    decision_id VARCHAR(36) NOT NULL UNIQUE,
    resolved_at TIMESTAMPTZ NOT NULL DEFAULT now()
);
INSERT INTO alert_resolutions (alert_id, decision_id)
SELECT DISTINCT ON (alert_id) alert_id, decision_id
FROM operator_decisions
WHERE alert_id IS NOT NULL
ORDER BY alert_id, decided_at DESC NULLS LAST, id DESC
ON CONFLICT (alert_id) DO NOTHING;
CREATE TABLE IF NOT EXISTS decision_outbox (
    id BIGSERIAL PRIMARY KEY,
    decision_id VARCHAR(36) NOT NULL,
    exchange_name VARCHAR(100) NOT NULL,
    routing_key VARCHAR(100) NOT NULL,
    payload JSONB NOT NULL,
    delay_ms BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    published_at TIMESTAMPTZ,
    attempts INTEGER NOT NULL DEFAULT 0,
    last_error VARCHAR(1000)
);
CREATE INDEX IF NOT EXISTS idx_decision_outbox_pending ON decision_outbox (created_at) WHERE published_at IS NULL;
CREATE TABLE IF NOT EXISTS command_receipts (
    id VARCHAR(120) PRIMARY KEY,
    decision_id VARCHAR(36) NOT NULL,
    worker_name VARCHAR(32) NOT NULL,
    processed_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    UNIQUE (decision_id, worker_name)
);
