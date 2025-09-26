ALTER TABLE dlt_events
    ADD COLUMN IF NOT EXISTS status        TEXT NOT NULL DEFAULT 'NEW',
    ADD COLUMN IF NOT EXISTS replayed_at   TIMESTAMPTZ NULL,
    ADD COLUMN IF NOT EXISTS replayed_by   TEXT NULL;

CREATE INDEX IF NOT EXISTS idx_dlt_events_status
    ON dlt_events(status);

-- kalau mau sering filter per topic+status+time:
CREATE INDEX IF NOT EXISTS idx_dlt_events_topic_status_time
    ON dlt_events(original_topic, status, received_at DESC);
