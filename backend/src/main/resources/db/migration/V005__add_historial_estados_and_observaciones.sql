CREATE TABLE orden_historial_estados (
    id BIGSERIAL PRIMARY KEY,
    orden_id BIGINT NOT NULL REFERENCES ordenes_trabajo(id),
    estado_anterior VARCHAR(30) NOT NULL,
    estado_nuevo VARCHAR(30) NOT NULL,
    usuario VARCHAR(50) NOT NULL,
    fecha TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    observacion VARCHAR(500)
);

CREATE INDEX idx_historial_orden_id ON orden_historial_estados(orden_id);

CREATE TABLE orden_observaciones (
    id BIGSERIAL PRIMARY KEY,
    orden_id BIGINT NOT NULL REFERENCES ordenes_trabajo(id),
    usuario VARCHAR(50) NOT NULL,
    fecha TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    comentario VARCHAR(2000) NOT NULL
);

CREATE INDEX idx_observaciones_orden_id ON orden_observaciones(orden_id);
