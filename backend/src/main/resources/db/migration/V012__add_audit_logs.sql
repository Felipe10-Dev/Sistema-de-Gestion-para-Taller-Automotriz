CREATE TABLE audit_logs (
    id BIGSERIAL PRIMARY KEY,
    timestamp TIMESTAMP NOT NULL DEFAULT NOW(),
    username VARCHAR(100),
    empresa_id BIGINT,
    sede_id BIGINT,
    ip VARCHAR(45),
    action VARCHAR(100) NOT NULL,
    resource VARCHAR(255),
    resource_id VARCHAR(100),
    http_method VARCHAR(10),
    http_status INT,
    duration_ms BIGINT,
    success BOOLEAN NOT NULL DEFAULT TRUE,
    details TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_audit_logs_timestamp ON audit_logs(timestamp DESC);
CREATE INDEX idx_audit_logs_username ON audit_logs(username);
CREATE INDEX idx_audit_logs_empresa_id ON audit_logs(empresa_id);
CREATE INDEX idx_audit_logs_action ON audit_logs(action);
CREATE INDEX idx_audit_logs_success ON audit_logs(success);
