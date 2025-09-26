-- V1__create_dlt_events.sql

CREATE TABLE IF NOT EXISTS dlt_events (
                  id                 UUID PRIMARY KEY,
                  original_topic     TEXT        NOT NULL,
                  original_partition INTEGER     NOT NULL,
                  original_offset    BIGINT      NOT NULL,
                  msg_key            TEXT,
                  payload            TEXT       NOT NULL,
                  exception_class    TEXT,
                  exception_message  TEXT,
                  received_at        TIMESTAMPTZ NOT NULL DEFAULT NOW()
    );


-- Cepat filter per topik & waktu (paling sering dipakai di list/monitoring)
CREATE INDEX IF NOT EXISTS idx_dlt_events_topic_received_at
    ON dlt_events (original_topic, received_at DESC);

-- Mempermudah locate posisi asli saat replay/analisa
CREATE INDEX IF NOT EXISTS idx_dlt_events_origin_pos
    ON dlt_events (original_topic, original_partition, original_offset);

-- Kadang perlu listing terbaru lintas topik
CREATE INDEX IF NOT EXISTS idx_dlt_events_received_at
    ON dlt_events (received_at DESC);
