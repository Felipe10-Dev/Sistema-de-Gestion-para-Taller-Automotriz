-- V014: Agregar motivo_eliminacion para eliminación segura con trazabilidad

-- ============================================================
-- 1. Tablas que extienden BaseEntity (ya tienen fecha_eliminacion, eliminado_por)
-- ============================================================
ALTER TABLE roles ADD COLUMN IF NOT EXISTS motivo_eliminacion VARCHAR(500);
ALTER TABLE usuarios ADD COLUMN IF NOT EXISTS motivo_eliminacion VARCHAR(500);
ALTER TABLE categorias ADD COLUMN IF NOT EXISTS motivo_eliminacion VARCHAR(500);
ALTER TABLE servicios ADD COLUMN IF NOT EXISTS motivo_eliminacion VARCHAR(500);
ALTER TABLE proveedores ADD COLUMN IF NOT EXISTS motivo_eliminacion VARCHAR(500);
ALTER TABLE productos ADD COLUMN IF NOT EXISTS motivo_eliminacion VARCHAR(500);
ALTER TABLE inventario ADD COLUMN IF NOT EXISTS motivo_eliminacion VARCHAR(500);
ALTER TABLE movimientos_inventario ADD COLUMN IF NOT EXISTS motivo_eliminacion VARCHAR(500);
ALTER TABLE clientes ADD COLUMN IF NOT EXISTS motivo_eliminacion VARCHAR(500);
ALTER TABLE vehiculos ADD COLUMN IF NOT EXISTS motivo_eliminacion VARCHAR(500);
ALTER TABLE ordenes_trabajo ADD COLUMN IF NOT EXISTS motivo_eliminacion VARCHAR(500);
ALTER TABLE ordenes_servicios ADD COLUMN IF NOT EXISTS motivo_eliminacion VARCHAR(500);
ALTER TABLE ordenes_productos ADD COLUMN IF NOT EXISTS motivo_eliminacion VARCHAR(500);
ALTER TABLE ordenes_compra ADD COLUMN IF NOT EXISTS motivo_eliminacion VARCHAR(500);
ALTER TABLE ordenes_compra_productos ADD COLUMN IF NOT EXISTS motivo_eliminacion VARCHAR(500);
ALTER TABLE orden_historial_estados ADD COLUMN IF NOT EXISTS motivo_eliminacion VARCHAR(500);
ALTER TABLE orden_observaciones ADD COLUMN IF NOT EXISTS motivo_eliminacion VARCHAR(500);
ALTER TABLE metodos_pago ADD COLUMN IF NOT EXISTS motivo_eliminacion VARCHAR(500);
ALTER TABLE caja ADD COLUMN IF NOT EXISTS motivo_eliminacion VARCHAR(500);
ALTER TABLE pagos ADD COLUMN IF NOT EXISTS motivo_eliminacion VARCHAR(500);
ALTER TABLE movimientos_caja ADD COLUMN IF NOT EXISTS motivo_eliminacion VARCHAR(500);
ALTER TABLE mantenimiento_recomendaciones ADD COLUMN IF NOT EXISTS motivo_eliminacion VARCHAR(500);
ALTER TABLE notas_crm ADD COLUMN IF NOT EXISTS motivo_eliminacion VARCHAR(500);
ALTER TABLE configuracion_empresa ADD COLUMN IF NOT EXISTS motivo_eliminacion VARCHAR(500);
ALTER TABLE parametros_sistema ADD COLUMN IF NOT EXISTS motivo_eliminacion VARCHAR(500);
ALTER TABLE configuracion_numeracion ADD COLUMN IF NOT EXISTS motivo_eliminacion VARCHAR(500);
ALTER TABLE configuracion_impuestos ADD COLUMN IF NOT EXISTS motivo_eliminacion VARCHAR(500);
ALTER TABLE horarios_atencion ADD COLUMN IF NOT EXISTS motivo_eliminacion VARCHAR(500);
ALTER TABLE dias_festivos ADD COLUMN IF NOT EXISTS motivo_eliminacion VARCHAR(500);
ALTER TABLE auditoria_global ADD COLUMN IF NOT EXISTS motivo_eliminacion VARCHAR(500);
ALTER TABLE backups_registro ADD COLUMN IF NOT EXISTS motivo_eliminacion VARCHAR(500);
ALTER TABLE permisos_modulo ADD COLUMN IF NOT EXISTS motivo_eliminacion VARCHAR(500);

-- Tablas multiempresa
ALTER TABLE empresas ADD COLUMN IF NOT EXISTS motivo_eliminacion VARCHAR(500);
ALTER TABLE sedes ADD COLUMN IF NOT EXISTS motivo_eliminacion VARCHAR(500);
ALTER TABLE transferencias_inventario ADD COLUMN IF NOT EXISTS motivo_eliminacion VARCHAR(500);
ALTER TABLE transferencia_productos ADD COLUMN IF NOT EXISTS motivo_eliminacion VARCHAR(500);

-- ============================================================
-- 2. Agregar fecha_eliminacion y eliminado_por a tablas que no los tienen
-- (dias_festivos, permisos_modulo, empresas, sedes, mantenimiento_recomendaciones)
-- ============================================================
ALTER TABLE dias_festivos ADD COLUMN IF NOT EXISTS fecha_eliminacion TIMESTAMP;
ALTER TABLE dias_festivos ADD COLUMN IF NOT EXISTS eliminado_por VARCHAR(100);

ALTER TABLE permisos_modulo ADD COLUMN IF NOT EXISTS fecha_eliminacion TIMESTAMP;
ALTER TABLE permisos_modulo ADD COLUMN IF NOT EXISTS eliminado_por VARCHAR(100);

ALTER TABLE empresas ADD COLUMN IF NOT EXISTS fecha_eliminacion TIMESTAMP;
ALTER TABLE empresas ADD COLUMN IF NOT EXISTS eliminado_por VARCHAR(100);

ALTER TABLE sedes ADD COLUMN IF NOT EXISTS fecha_eliminacion TIMESTAMP;
ALTER TABLE sedes ADD COLUMN IF NOT EXISTS eliminado_por VARCHAR(100);

ALTER TABLE mantenimiento_recomendaciones ADD COLUMN IF NOT EXISTS fecha_eliminacion TIMESTAMP;
ALTER TABLE mantenimiento_recomendaciones ADD COLUMN IF NOT EXISTS eliminado_por VARCHAR(100);
