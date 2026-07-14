-- Ficha Técnica: nuevos campos en vehiculos
ALTER TABLE vehiculos ADD COLUMN IF NOT EXISTS motor VARCHAR(50);
ALTER TABLE vehiculos ADD COLUMN IF NOT EXISTS combustible VARCHAR(30);
ALTER TABLE vehiculos ADD COLUMN IF NOT EXISTS vin VARCHAR(20);
ALTER TABLE vehiculos ADD COLUMN IF NOT EXISTS anio INTEGER;

-- Mantenimiento Preventivo
CREATE TABLE mantenimiento_recomendaciones (
    id BIGSERIAL PRIMARY KEY,
    vehiculo_id BIGINT NOT NULL REFERENCES vehiculos(id),
    tipo VARCHAR(30) NOT NULL,
    descripcion VARCHAR(500),
    tipo_programacion VARCHAR(20) NOT NULL DEFAULT 'AMBOS',
    intervalo_kilometraje INTEGER,
    intervalo_dias INTEGER,
    activo BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE INDEX idx_mant_recom_vehiculo ON mantenimiento_recomendaciones(vehiculo_id);
