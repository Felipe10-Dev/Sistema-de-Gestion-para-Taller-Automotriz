-- V003: Refactor - Unificar auditoria en todas las tablas
-- Agrega columnas de auditoria faltantes a tablas que ahora extienden BaseEntity

-- ============================================================
-- 1. Roles - agrega columnas de auditoria completas
-- ============================================================
ALTER TABLE roles ADD COLUMN IF NOT EXISTS fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE roles ADD COLUMN IF NOT EXISTS fecha_modificacion TIMESTAMP;
ALTER TABLE roles ADD COLUMN IF NOT EXISTS creado_por VARCHAR(50);
ALTER TABLE roles ADD COLUMN IF NOT EXISTS modificado_por VARCHAR(50);
ALTER TABLE roles ADD COLUMN IF NOT EXISTS activo BOOLEAN DEFAULT TRUE;

-- ============================================================
-- 2. Categorias - ya tiene activo, agrega columnas faltantes
-- ============================================================
ALTER TABLE categorias ADD COLUMN IF NOT EXISTS fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE categorias ADD COLUMN IF NOT EXISTS fecha_modificacion TIMESTAMP;
ALTER TABLE categorias ADD COLUMN IF NOT EXISTS creado_por VARCHAR(50);
ALTER TABLE categorias ADD COLUMN IF NOT EXISTS modificado_por VARCHAR(50);

-- ============================================================
-- 3. Servicios - ya tiene activo, agrega columnas faltantes
-- ============================================================
ALTER TABLE servicios ADD COLUMN IF NOT EXISTS fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE servicios ADD COLUMN IF NOT EXISTS fecha_modificacion TIMESTAMP;
ALTER TABLE servicios ADD COLUMN IF NOT EXISTS creado_por VARCHAR(50);
ALTER TABLE servicios ADD COLUMN IF NOT EXISTS modificado_por VARCHAR(50);

-- ============================================================
-- 4. Productos - ya tiene activo, agrega columnas faltantes
-- ============================================================
ALTER TABLE productos ADD COLUMN IF NOT EXISTS fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE productos ADD COLUMN IF NOT EXISTS fecha_modificacion TIMESTAMP;
ALTER TABLE productos ADD COLUMN IF NOT EXISTS creado_por VARCHAR(50);
ALTER TABLE productos ADD COLUMN IF NOT EXISTS modificado_por VARCHAR(50);

-- ============================================================
-- 5. Inventario - agrega columnas de auditoria completas
-- ============================================================
ALTER TABLE inventario ADD COLUMN IF NOT EXISTS fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE inventario ADD COLUMN IF NOT EXISTS fecha_modificacion TIMESTAMP;
ALTER TABLE inventario ADD COLUMN IF NOT EXISTS creado_por VARCHAR(50);
ALTER TABLE inventario ADD COLUMN IF NOT EXISTS modificado_por VARCHAR(50);
ALTER TABLE inventario ADD COLUMN IF NOT EXISTS activo BOOLEAN DEFAULT TRUE;

-- ============================================================
-- 6. Movimientos inventario - ya tiene usuario y fecha_movimiento
--    Agrega columnas de auditoria (independientes de los campos de negocio)
-- ============================================================
ALTER TABLE movimientos_inventario ADD COLUMN IF NOT EXISTS fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE movimientos_inventario ADD COLUMN IF NOT EXISTS fecha_modificacion TIMESTAMP;
ALTER TABLE movimientos_inventario ADD COLUMN IF NOT EXISTS creado_por VARCHAR(50);
ALTER TABLE movimientos_inventario ADD COLUMN IF NOT EXISTS modificado_por VARCHAR(50);
ALTER TABLE movimientos_inventario ADD COLUMN IF NOT EXISTS activo BOOLEAN DEFAULT TRUE;

-- ============================================================
-- 7. Ordenes servicios (detalle) - agrega columnas de auditoria
-- ============================================================
ALTER TABLE ordenes_servicios ADD COLUMN IF NOT EXISTS fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE ordenes_servicios ADD COLUMN IF NOT EXISTS fecha_modificacion TIMESTAMP;
ALTER TABLE ordenes_servicios ADD COLUMN IF NOT EXISTS creado_por VARCHAR(50);
ALTER TABLE ordenes_servicios ADD COLUMN IF NOT EXISTS modificado_por VARCHAR(50);
ALTER TABLE ordenes_servicios ADD COLUMN IF NOT EXISTS activo BOOLEAN DEFAULT TRUE;

-- ============================================================
-- 8. Ordenes productos (detalle) - agrega columnas de auditoria
-- ============================================================
ALTER TABLE ordenes_productos ADD COLUMN IF NOT EXISTS fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE ordenes_productos ADD COLUMN IF NOT EXISTS fecha_modificacion TIMESTAMP;
ALTER TABLE ordenes_productos ADD COLUMN IF NOT EXISTS creado_por VARCHAR(50);
ALTER TABLE ordenes_productos ADD COLUMN IF NOT EXISTS modificado_por VARCHAR(50);
ALTER TABLE ordenes_productos ADD COLUMN IF NOT EXISTS activo BOOLEAN DEFAULT TRUE;

-- ============================================================
-- 9. Agregar columnas de eliminacion a todas las tablas que extienden BaseEntity
-- ============================================================
ALTER TABLE categorias ADD COLUMN IF NOT EXISTS fecha_eliminacion TIMESTAMP;
ALTER TABLE categorias ADD COLUMN IF NOT EXISTS eliminado_por VARCHAR(50);

ALTER TABLE servicios ADD COLUMN IF NOT EXISTS fecha_eliminacion TIMESTAMP;
ALTER TABLE servicios ADD COLUMN IF NOT EXISTS eliminado_por VARCHAR(50);

ALTER TABLE productos ADD COLUMN IF NOT EXISTS fecha_eliminacion TIMESTAMP;
ALTER TABLE productos ADD COLUMN IF NOT EXISTS eliminado_por VARCHAR(50);

ALTER TABLE roles ADD COLUMN IF NOT EXISTS fecha_eliminacion TIMESTAMP;
ALTER TABLE roles ADD COLUMN IF NOT EXISTS eliminado_por VARCHAR(50);

ALTER TABLE clientes ADD COLUMN IF NOT EXISTS fecha_eliminacion TIMESTAMP;
ALTER TABLE clientes ADD COLUMN IF NOT EXISTS eliminado_por VARCHAR(50);

ALTER TABLE vehiculos ADD COLUMN IF NOT EXISTS fecha_eliminacion TIMESTAMP;
ALTER TABLE vehiculos ADD COLUMN IF NOT EXISTS eliminado_por VARCHAR(50);

ALTER TABLE usuarios ADD COLUMN IF NOT EXISTS fecha_eliminacion TIMESTAMP;
ALTER TABLE usuarios ADD COLUMN IF NOT EXISTS eliminado_por VARCHAR(50);

ALTER TABLE ordenes_trabajo ADD COLUMN IF NOT EXISTS fecha_eliminacion TIMESTAMP;
ALTER TABLE ordenes_trabajo ADD COLUMN IF NOT EXISTS eliminado_por VARCHAR(50);

ALTER TABLE ordenes_servicios ADD COLUMN IF NOT EXISTS fecha_eliminacion TIMESTAMP;
ALTER TABLE ordenes_servicios ADD COLUMN IF NOT EXISTS eliminado_por VARCHAR(50);

ALTER TABLE ordenes_productos ADD COLUMN IF NOT EXISTS fecha_eliminacion TIMESTAMP;
ALTER TABLE ordenes_productos ADD COLUMN IF NOT EXISTS eliminado_por VARCHAR(50);

ALTER TABLE inventario ADD COLUMN IF NOT EXISTS fecha_eliminacion TIMESTAMP;
ALTER TABLE inventario ADD COLUMN IF NOT EXISTS eliminado_por VARCHAR(50);

ALTER TABLE movimientos_inventario ADD COLUMN IF NOT EXISTS fecha_eliminacion TIMESTAMP;
ALTER TABLE movimientos_inventario ADD COLUMN IF NOT EXISTS eliminado_por VARCHAR(50);

ALTER TABLE proveedores ADD COLUMN IF NOT EXISTS fecha_eliminacion TIMESTAMP;
ALTER TABLE proveedores ADD COLUMN IF NOT EXISTS eliminado_por VARCHAR(50);

-- ============================================================
-- 10. Aumentar longitud de numero_orden para soportar UUID
-- ============================================================
ALTER TABLE ordenes_trabajo ALTER COLUMN numero_orden TYPE VARCHAR(30);

-- ============================================================
-- 11. Nuevos indices para mejorar rendimiento de consultas
-- ============================================================
CREATE INDEX IF NOT EXISTS idx_servicios_nombre ON servicios(nombre);
CREATE INDEX IF NOT EXISTS idx_productos_nombre ON productos(nombre);
CREATE INDEX IF NOT EXISTS idx_clientes_email ON clientes(email);
CREATE INDEX IF NOT EXISTS idx_usuarios_email ON usuarios(email);
CREATE INDEX IF NOT EXISTS idx_ordenes_servicios_orden ON ordenes_servicios(orden_id);
CREATE INDEX IF NOT EXISTS idx_ordenes_productos_orden ON ordenes_productos(orden_id);
CREATE INDEX IF NOT EXISTS idx_movimientos_fecha ON movimientos_inventario(fecha_movimiento);
CREATE INDEX IF NOT EXISTS idx_ordenes_fecha_salida ON ordenes_trabajo(fecha_salida);
