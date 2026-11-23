CREATE TABLE IF NOT EXISTS sensor_readings (
    id           BIGSERIAL PRIMARY KEY,
    sensor_id    VARCHAR(20)      NOT NULL,
    vibration    DOUBLE PRECISION NOT NULL,
    recorded_at  TIMESTAMPTZ      NOT NULL,
    stored_at    TIMESTAMPTZ      NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS alerts (
    id               BIGSERIAL PRIMARY KEY,
    sensor_id        VARCHAR(20)  NOT NULL,
    severity         VARCHAR(10)  NOT NULL,   -- CRITICAL | WARNING
    vibration_value  DOUBLE PRECISION,
    payload          JSONB        NOT NULL,   -- mensaje original completo
    alert_id         VARCHAR(64),
    occurred_at      TIMESTAMPTZ,
    status           VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    created_at       TIMESTAMPTZ  NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS operator_decisions (
    id           BIGSERIAL PRIMARY KEY,
    decision_id  VARCHAR(36)  NOT NULL UNIQUE,
    sensor_id    VARCHAR(20),
    action       VARCHAR(50)  NOT NULL,   -- APAGADO_INMEDIATO, etc.
    routing_key  VARCHAR(100),
    alert_id     VARCHAR(64),
    payload      JSONB        NOT NULL,
    status       VARCHAR(20)  NOT NULL DEFAULT 'DISPATCHED',
    decided_at   TIMESTAMPTZ  NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS alert_resolutions (
    alert_id    VARCHAR(64) PRIMARY KEY,
    decision_id VARCHAR(36) NOT NULL UNIQUE,
    resolved_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS decision_outbox (
    id           BIGSERIAL PRIMARY KEY,
    decision_id  VARCHAR(36) NOT NULL,
    exchange_name VARCHAR(100) NOT NULL,
    routing_key  VARCHAR(100) NOT NULL,
    payload      JSONB NOT NULL,
    delay_ms     BIGINT NOT NULL DEFAULT 0,
    created_at   TIMESTAMPTZ NOT NULL DEFAULT now(),
    published_at TIMESTAMPTZ,
    attempts     INTEGER NOT NULL DEFAULT 0,
    last_error   VARCHAR(1000)
);
CREATE INDEX IF NOT EXISTS idx_decision_outbox_pending ON decision_outbox (created_at) WHERE published_at IS NULL;

CREATE TABLE IF NOT EXISTS command_receipts (
    id           VARCHAR(120) PRIMARY KEY,
    decision_id  VARCHAR(36) NOT NULL,
    worker_name  VARCHAR(32) NOT NULL,
    processed_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    UNIQUE (decision_id, worker_name)
);

CREATE INDEX IF NOT EXISTS idx_readings_sensor_time ON sensor_readings (sensor_id, recorded_at DESC);
CREATE INDEX IF NOT EXISTS idx_alerts_sensor_time   ON alerts (sensor_id, created_at DESC);
CREATE INDEX IF NOT EXISTS idx_alerts_time          ON alerts (created_at DESC);
CREATE INDEX IF NOT EXISTS idx_decisions_time       ON operator_decisions (decided_at DESC);
CREATE UNIQUE INDEX IF NOT EXISTS uq_alerts_alert_id ON alerts (alert_id);
CREATE INDEX IF NOT EXISTS idx_decisions_alert ON operator_decisions (alert_id);
