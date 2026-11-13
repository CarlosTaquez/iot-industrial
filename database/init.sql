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
    created_at       TIMESTAMPTZ  NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS operator_decisions (
    id           BIGSERIAL PRIMARY KEY,
    sensor_id    VARCHAR(20),
    action       VARCHAR(50)  NOT NULL,   -- APAGADO_INMEDIATO, etc.
    routing_key  VARCHAR(100),
    alert_id     VARCHAR(64),
    payload      JSONB        NOT NULL,
    decided_at   TIMESTAMPTZ  NOT NULL DEFAULT now()
);

CREATE INDEX IF NOT EXISTS idx_readings_sensor_time ON sensor_readings (sensor_id, recorded_at DESC);
CREATE INDEX IF NOT EXISTS idx_alerts_sensor_time   ON alerts (sensor_id, created_at DESC);
CREATE INDEX IF NOT EXISTS idx_alerts_time          ON alerts (created_at DESC);
CREATE INDEX IF NOT EXISTS idx_decisions_time       ON operator_decisions (decided_at DESC);
CREATE UNIQUE INDEX IF NOT EXISTS uq_alerts_alert_id ON alerts (alert_id);
CREATE INDEX IF NOT EXISTS idx_decisions_alert ON operator_decisions (alert_id);