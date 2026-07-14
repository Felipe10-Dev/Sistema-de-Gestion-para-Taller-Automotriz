-- CRM: Clasificación automática de clientes
ALTER TABLE clientes ADD COLUMN IF NOT EXISTS clasificacion VARCHAR(20) DEFAULT 'NUEVO';

-- Notas internas CRM (inmutables, nunca se eliminan)
CREATE TABLE IF NOT EXISTS notas_crm (
    id BIGSERIAL PRIMARY KEY,
    cliente_id BIGINT NOT NULL REFERENCES clientes(id),
    usuario VARCHAR(50) NOT NULL,
    fecha TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    comentario TEXT NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_notas_crm_cliente ON notas_crm(cliente_id);
