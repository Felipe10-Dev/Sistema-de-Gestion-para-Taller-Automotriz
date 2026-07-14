-- ============================================================
-- Sprint 9: Arquitectura Multiempresa y Multisede
-- ============================================================

-- 1. Core tenant table
CREATE TABLE IF NOT EXISTS empresas (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(200) NOT NULL,
    razon_social VARCHAR(200),
    nit VARCHAR(20),
    direccion VARCHAR(200),
    ciudad VARCHAR(100),
    telefono VARCHAR(20),
    email VARCHAR(150),
    activo BOOLEAN DEFAULT TRUE,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 2. Sede table (each empresa has many sedes)
CREATE TABLE IF NOT EXISTS sedes (
    id BIGSERIAL PRIMARY KEY,
    empresa_id BIGINT NOT NULL REFERENCES empresas(id),
    nombre VARCHAR(200) NOT NULL,
    direccion VARCHAR(200),
    ciudad VARCHAR(100),
    telefono VARCHAR(20),
    activo BOOLEAN DEFAULT TRUE
);

-- 3. Transferencias entre sedes
CREATE TABLE IF NOT EXISTS transferencias_inventario (
    id BIGSERIAL PRIMARY KEY,
    empresa_id BIGINT NOT NULL REFERENCES empresas(id),
    sede_id BIGINT REFERENCES sedes(id),
    sede_origen_id BIGINT NOT NULL REFERENCES sedes(id),
    sede_destino_id BIGINT NOT NULL REFERENCES sedes(id),
    fecha TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    usuario VARCHAR(50),
    observaciones VARCHAR(500)
);

CREATE TABLE IF NOT EXISTS transferencia_productos (
    id BIGSERIAL PRIMARY KEY,
    transferencia_id BIGINT NOT NULL REFERENCES transferencias_inventario(id),
    producto_id BIGINT NOT NULL REFERENCES productos(id),
    cantidad INTEGER NOT NULL
);

-- ============================================================
-- Insert default empresa from existing config
-- ============================================================
INSERT INTO empresas (nombre, razon_social, nit, direccion, ciudad, telefono, email)
SELECT COALESCE(nombre, 'Mi Serviteca'), COALESCE(razon_social, 'Mi Serviteca SAS'),
       COALESCE(nit, '900000000-0'), direccion, ciudad, telefono, email
FROM configuracion_empresa WHERE activo = TRUE
LIMIT 1;

-- If no empresa was inserted (no config data), create default
INSERT INTO empresas (nombre, razon_social, nit)
SELECT 'Mi Serviteca', 'Mi Serviteca SAS', '900000000-0'
WHERE NOT EXISTS (SELECT 1 FROM empresas);

-- Insert default sede
INSERT INTO sedes (empresa_id, nombre, direccion, ciudad, telefono)
SELECT e.id, 'Sede Principal', e.direccion, e.ciudad, e.telefono
FROM empresas e
WHERE NOT EXISTS (SELECT 1 FROM sedes WHERE empresa_id = e.id)
LIMIT 1;

-- ============================================================
-- Add empresa_id and sede_id to all existing tables
-- ============================================================

-- --- BaseEntity tables (14 tables need empresa_id) ---
ALTER TABLE roles ADD COLUMN IF NOT EXISTS empresa_id BIGINT REFERENCES empresas(id);
UPDATE roles SET empresa_id = (SELECT id FROM empresas LIMIT 1) WHERE empresa_id IS NULL;
ALTER TABLE roles ALTER COLUMN empresa_id SET NOT NULL;

ALTER TABLE usuarios ADD COLUMN IF NOT EXISTS empresa_id BIGINT REFERENCES empresas(id);
ALTER TABLE usuarios ADD COLUMN IF NOT EXISTS sede_id BIGINT REFERENCES sedes(id);
UPDATE usuarios SET empresa_id = (SELECT id FROM empresas LIMIT 1) WHERE empresa_id IS NULL;
UPDATE usuarios SET sede_id = (SELECT id FROM sedes LIMIT 1) WHERE sede_id IS NULL;
ALTER TABLE usuarios ALTER COLUMN empresa_id SET NOT NULL;
ALTER TABLE usuarios ALTER COLUMN sede_id SET NOT NULL;

ALTER TABLE categorias ADD COLUMN IF NOT EXISTS empresa_id BIGINT REFERENCES empresas(id);
UPDATE categorias SET empresa_id = (SELECT id FROM empresas LIMIT 1) WHERE empresa_id IS NULL;
ALTER TABLE categorias ALTER COLUMN empresa_id SET NOT NULL;

ALTER TABLE servicios ADD COLUMN IF NOT EXISTS empresa_id BIGINT REFERENCES empresas(id);
UPDATE servicios SET empresa_id = (SELECT id FROM empresas LIMIT 1) WHERE empresa_id IS NULL;
ALTER TABLE servicios ALTER COLUMN empresa_id SET NOT NULL;

ALTER TABLE proveedores ADD COLUMN IF NOT EXISTS empresa_id BIGINT REFERENCES empresas(id);
UPDATE proveedores SET empresa_id = (SELECT id FROM empresas LIMIT 1) WHERE empresa_id IS NULL;
ALTER TABLE proveedores ALTER COLUMN empresa_id SET NOT NULL;

ALTER TABLE productos ADD COLUMN IF NOT EXISTS empresa_id BIGINT REFERENCES empresas(id);
UPDATE productos SET empresa_id = (SELECT id FROM empresas LIMIT 1) WHERE empresa_id IS NULL;
ALTER TABLE productos ALTER COLUMN empresa_id SET NOT NULL;

ALTER TABLE inventario ADD COLUMN IF NOT EXISTS empresa_id BIGINT REFERENCES empresas(id);
ALTER TABLE inventario ADD COLUMN IF NOT EXISTS sede_id BIGINT REFERENCES sedes(id);
UPDATE inventario SET empresa_id = (SELECT id FROM empresas LIMIT 1) WHERE empresa_id IS NULL;
UPDATE inventario SET sede_id = (SELECT id FROM sedes LIMIT 1) WHERE sede_id IS NULL;
ALTER TABLE inventario ALTER COLUMN empresa_id SET NOT NULL;
ALTER TABLE inventario ALTER COLUMN sede_id SET NOT NULL;

ALTER TABLE movimientos_inventario ADD COLUMN IF NOT EXISTS empresa_id BIGINT REFERENCES empresas(id);
ALTER TABLE movimientos_inventario ADD COLUMN IF NOT EXISTS sede_id BIGINT REFERENCES sedes(id);
UPDATE movimientos_inventario SET empresa_id = (SELECT id FROM empresas LIMIT 1) WHERE empresa_id IS NULL;
UPDATE movimientos_inventario SET sede_id = (SELECT id FROM sedes LIMIT 1) WHERE sede_id IS NULL;
ALTER TABLE movimientos_inventario ALTER COLUMN empresa_id SET NOT NULL;
ALTER TABLE movimientos_inventario ALTER COLUMN sede_id SET NOT NULL;

ALTER TABLE clientes ADD COLUMN IF NOT EXISTS empresa_id BIGINT REFERENCES empresas(id);
UPDATE clientes SET empresa_id = (SELECT id FROM empresas LIMIT 1) WHERE empresa_id IS NULL;
ALTER TABLE clientes ALTER COLUMN empresa_id SET NOT NULL;

ALTER TABLE vehiculos ADD COLUMN IF NOT EXISTS empresa_id BIGINT REFERENCES empresas(id);
UPDATE vehiculos SET empresa_id = (SELECT id FROM empresas LIMIT 1) WHERE empresa_id IS NULL;
ALTER TABLE vehiculos ALTER COLUMN empresa_id SET NOT NULL;

ALTER TABLE ordenes_trabajo ADD COLUMN IF NOT EXISTS empresa_id BIGINT REFERENCES empresas(id);
ALTER TABLE ordenes_trabajo ADD COLUMN IF NOT EXISTS sede_id BIGINT REFERENCES sedes(id);
UPDATE ordenes_trabajo SET empresa_id = (SELECT id FROM empresas LIMIT 1) WHERE empresa_id IS NULL;
UPDATE ordenes_trabajo SET sede_id = (SELECT id FROM sedes LIMIT 1) WHERE sede_id IS NULL;
ALTER TABLE ordenes_trabajo ALTER COLUMN empresa_id SET NOT NULL;
ALTER TABLE ordenes_trabajo ALTER COLUMN sede_id SET NOT NULL;

ALTER TABLE ordenes_servicios ADD COLUMN IF NOT EXISTS empresa_id BIGINT REFERENCES empresas(id);
UPDATE ordenes_servicios SET empresa_id = (SELECT id FROM empresas LIMIT 1) WHERE empresa_id IS NULL;
ALTER TABLE ordenes_servicios ALTER COLUMN empresa_id SET NOT NULL;

ALTER TABLE ordenes_productos ADD COLUMN IF NOT EXISTS empresa_id BIGINT REFERENCES empresas(id);
UPDATE ordenes_productos SET empresa_id = (SELECT id FROM empresas LIMIT 1) WHERE empresa_id IS NULL;
ALTER TABLE ordenes_productos ALTER COLUMN empresa_id SET NOT NULL;

ALTER TABLE ordenes_compra ADD COLUMN IF NOT EXISTS empresa_id BIGINT REFERENCES empresas(id);
ALTER TABLE ordenes_compra ADD COLUMN IF NOT EXISTS sede_id BIGINT REFERENCES sedes(id);
UPDATE ordenes_compra SET empresa_id = (SELECT id FROM empresas LIMIT 1) WHERE empresa_id IS NULL;
UPDATE ordenes_compra SET sede_id = (SELECT id FROM sedes LIMIT 1) WHERE sede_id IS NULL;
ALTER TABLE ordenes_compra ALTER COLUMN empresa_id SET NOT NULL;
ALTER TABLE ordenes_compra ALTER COLUMN sede_id SET NOT NULL;

-- --- Non-BaseEntity tables (need empresa_id) ---
ALTER TABLE ordenes_compra_productos ADD COLUMN IF NOT EXISTS empresa_id BIGINT REFERENCES empresas(id);
UPDATE ordenes_compra_productos SET empresa_id = (SELECT id FROM empresas LIMIT 1) WHERE empresa_id IS NULL;
ALTER TABLE ordenes_compra_productos ALTER COLUMN empresa_id SET NOT NULL;
ALTER TABLE ordenes_compra_productos ADD COLUMN IF NOT EXISTS sede_id BIGINT REFERENCES sedes(id);

ALTER TABLE orden_historial_estados ADD COLUMN IF NOT EXISTS empresa_id BIGINT REFERENCES empresas(id);
UPDATE orden_historial_estados SET empresa_id = (SELECT id FROM empresas LIMIT 1) WHERE empresa_id IS NULL;
ALTER TABLE orden_historial_estados ALTER COLUMN empresa_id SET NOT NULL;

ALTER TABLE orden_observaciones ADD COLUMN IF NOT EXISTS empresa_id BIGINT REFERENCES empresas(id);
UPDATE orden_observaciones SET empresa_id = (SELECT id FROM empresas LIMIT 1) WHERE empresa_id IS NULL;
ALTER TABLE orden_observaciones ALTER COLUMN empresa_id SET NOT NULL;

ALTER TABLE metodos_pago ADD COLUMN IF NOT EXISTS empresa_id BIGINT REFERENCES empresas(id);
UPDATE metodos_pago SET empresa_id = (SELECT id FROM empresas LIMIT 1) WHERE empresa_id IS NULL;
ALTER TABLE metodos_pago ALTER COLUMN empresa_id SET NOT NULL;

ALTER TABLE caja ADD COLUMN IF NOT EXISTS empresa_id BIGINT REFERENCES empresas(id);
ALTER TABLE caja ADD COLUMN IF NOT EXISTS sede_id BIGINT REFERENCES sedes(id);
UPDATE caja SET empresa_id = (SELECT id FROM empresas LIMIT 1) WHERE empresa_id IS NULL;
UPDATE caja SET sede_id = (SELECT id FROM sedes LIMIT 1) WHERE sede_id IS NULL;
ALTER TABLE caja ALTER COLUMN empresa_id SET NOT NULL;
ALTER TABLE caja ALTER COLUMN sede_id SET NOT NULL;

ALTER TABLE pagos ADD COLUMN IF NOT EXISTS empresa_id BIGINT REFERENCES empresas(id);
ALTER TABLE pagos ADD COLUMN IF NOT EXISTS sede_id BIGINT REFERENCES sedes(id);
UPDATE pagos SET empresa_id = (SELECT id FROM empresas LIMIT 1) WHERE empresa_id IS NULL;
UPDATE pagos SET sede_id = (SELECT id FROM sedes LIMIT 1) WHERE sede_id IS NULL;
ALTER TABLE pagos ALTER COLUMN empresa_id SET NOT NULL;
ALTER TABLE pagos ALTER COLUMN sede_id SET NOT NULL;

ALTER TABLE movimientos_caja ADD COLUMN IF NOT EXISTS empresa_id BIGINT REFERENCES empresas(id);
ALTER TABLE movimientos_caja ADD COLUMN IF NOT EXISTS sede_id BIGINT REFERENCES sedes(id);
UPDATE movimientos_caja SET empresa_id = (SELECT id FROM empresas LIMIT 1) WHERE empresa_id IS NULL;
UPDATE movimientos_caja SET sede_id = (SELECT id FROM sedes LIMIT 1) WHERE sede_id IS NULL;
ALTER TABLE movimientos_caja ALTER COLUMN empresa_id SET NOT NULL;
ALTER TABLE movimientos_caja ALTER COLUMN sede_id SET NOT NULL;

ALTER TABLE mantenimiento_recomendaciones ADD COLUMN IF NOT EXISTS empresa_id BIGINT REFERENCES empresas(id);
UPDATE mantenimiento_recomendaciones SET empresa_id = (SELECT id FROM empresas LIMIT 1) WHERE empresa_id IS NULL;
ALTER TABLE mantenimiento_recomendaciones ALTER COLUMN empresa_id SET NOT NULL;

ALTER TABLE notas_crm ADD COLUMN IF NOT EXISTS empresa_id BIGINT REFERENCES empresas(id);
UPDATE notas_crm SET empresa_id = (SELECT id FROM empresas LIMIT 1) WHERE empresa_id IS NULL;
ALTER TABLE notas_crm ALTER COLUMN empresa_id SET NOT NULL;

-- --- V010 config tables (need empresa_id) ---
ALTER TABLE configuracion_empresa ADD COLUMN IF NOT EXISTS empresa_id BIGINT REFERENCES empresas(id);
UPDATE configuracion_empresa SET empresa_id = (SELECT id FROM empresas LIMIT 1) WHERE empresa_id IS NULL;
ALTER TABLE configuracion_empresa ALTER COLUMN empresa_id SET NOT NULL;

ALTER TABLE parametros_sistema ADD COLUMN IF NOT EXISTS empresa_id BIGINT REFERENCES empresas(id);
UPDATE parametros_sistema SET empresa_id = (SELECT id FROM empresas LIMIT 1) WHERE empresa_id IS NULL;
ALTER TABLE parametros_sistema ALTER COLUMN empresa_id SET NOT NULL;

ALTER TABLE configuracion_numeracion ADD COLUMN IF NOT EXISTS empresa_id BIGINT REFERENCES empresas(id);
UPDATE configuracion_numeracion SET empresa_id = (SELECT id FROM empresas LIMIT 1) WHERE empresa_id IS NULL;
ALTER TABLE configuracion_numeracion ALTER COLUMN empresa_id SET NOT NULL;

ALTER TABLE configuracion_impuestos ADD COLUMN IF NOT EXISTS empresa_id BIGINT REFERENCES empresas(id);
UPDATE configuracion_impuestos SET empresa_id = (SELECT id FROM empresas LIMIT 1) WHERE empresa_id IS NULL;
ALTER TABLE configuracion_impuestos ALTER COLUMN empresa_id SET NOT NULL;

ALTER TABLE horarios_atencion ADD COLUMN IF NOT EXISTS empresa_id BIGINT REFERENCES empresas(id);
UPDATE horarios_atencion SET empresa_id = (SELECT id FROM empresas LIMIT 1) WHERE empresa_id IS NULL;
ALTER TABLE horarios_atencion ALTER COLUMN empresa_id SET NOT NULL;

ALTER TABLE dias_festivos ADD COLUMN IF NOT EXISTS empresa_id BIGINT REFERENCES empresas(id);
UPDATE dias_festivos SET empresa_id = (SELECT id FROM empresas LIMIT 1) WHERE empresa_id IS NULL;
ALTER TABLE dias_festivos ALTER COLUMN empresa_id SET NOT NULL;

ALTER TABLE auditoria_global ADD COLUMN IF NOT EXISTS empresa_id BIGINT REFERENCES empresas(id);
UPDATE auditoria_global SET empresa_id = (SELECT id FROM empresas LIMIT 1) WHERE empresa_id IS NULL;
ALTER TABLE auditoria_global ALTER COLUMN empresa_id SET NOT NULL;

ALTER TABLE backups_registro ADD COLUMN IF NOT EXISTS empresa_id BIGINT REFERENCES empresas(id);
UPDATE backups_registro SET empresa_id = (SELECT id FROM empresas LIMIT 1) WHERE empresa_id IS NULL;
ALTER TABLE backups_registro ALTER COLUMN empresa_id SET NOT NULL;

ALTER TABLE permisos_modulo ADD COLUMN IF NOT EXISTS empresa_id BIGINT REFERENCES empresas(id);
UPDATE permisos_modulo SET empresa_id = (SELECT id FROM empresas LIMIT 1) WHERE empresa_id IS NULL;
ALTER TABLE permisos_modulo ALTER COLUMN empresa_id SET NOT NULL;

-- ============================================================
-- Update existing unique constraints to include empresa_id
-- ============================================================
-- Drop old unique constraints and add new ones that include empresa_id
-- Note: Some DBs don't allow dropping FK constraints this way, but since this
-- is a fresh migration and tables may have been recreated, we handle gracefully.

-- roles: nombre was unique per empresa now
-- We keep old unique for backward compat; add composite
CREATE UNIQUE INDEX IF NOT EXISTS idx_roles_nombre_empresa ON roles(nombre, empresa_id);

-- clientes: numero_documento was unique globally, now per empresa
DROP INDEX IF EXISTS idx_clientes_numero_documento;
CREATE UNIQUE INDEX IF NOT EXISTS idx_clientes_numero_doc_empresa ON clientes(numero_documento, empresa_id);

-- productos: codigo was unique globally, now per empresa
DROP INDEX IF EXISTS idx_productos_codigo;
CREATE UNIQUE INDEX IF NOT EXISTS idx_productos_codigo_empresa ON productos(codigo, empresa_id);

-- vehiculos: placa was unique globally, now per empresa
DROP INDEX IF EXISTS idx_vehiculos_placa;
CREATE UNIQUE INDEX IF NOT EXISTS idx_vehiculos_placa_empresa ON vehiculos(placa, empresa_id);

-- ordenes_trabajo: numero_orden was unique globally, now per empresa
DROP INDEX IF EXISTS idx_ordenes_trabajo_numero_orden;
CREATE UNIQUE INDEX IF NOT EXISTS idx_ordenes_trabajo_numero_empresa ON ordenes_trabajo(numero_orden, empresa_id);

-- proveedores: nit was unique globally, now per empresa
DROP INDEX IF EXISTS idx_proveedores_nit;
CREATE UNIQUE INDEX IF NOT EXISTS idx_proveedores_nit_empresa ON proveedores(nit, empresa_id);

-- usuarios: username was unique globally, now per empresa
DROP INDEX IF EXISTS idx_usuarios_username;
CREATE UNIQUE INDEX IF NOT EXISTS idx_usuarios_username_empresa ON usuarios(username, empresa_id);

-- parametros_sistema: codigo was unique globally, now per empresa
DROP INDEX IF EXISTS idx_parametros_sistema_codigo;
CREATE UNIQUE INDEX IF NOT EXISTS idx_param_sistema_codigo_empresa ON parametros_sistema(codigo, empresa_id);

-- configuracion_numeracion: modulo was unique globally, now per empresa
DROP INDEX IF EXISTS idx_configuracion_numeracion_modulo;
CREATE UNIQUE INDEX IF NOT EXISTS idx_num_modulo_empresa ON configuracion_numeracion(modulo, empresa_id);

-- metodos_pago: nombre was unique globally, now per empresa
DROP INDEX IF EXISTS idx_metodos_pago_nombre;
CREATE UNIQUE INDEX IF NOT EXISTS idx_met_pago_nombre_empresa ON metodos_pago(nombre, empresa_id);

-- ============================================================
-- Indexes for performance
-- ============================================================
-- ============================================================
-- Add sede_id to remaining legacy tables (nullable for most)
-- ============================================================
ALTER TABLE categorias ADD COLUMN IF NOT EXISTS sede_id BIGINT REFERENCES sedes(id);
ALTER TABLE clientes ADD COLUMN IF NOT EXISTS sede_id BIGINT REFERENCES sedes(id);
ALTER TABLE productos ADD COLUMN IF NOT EXISTS sede_id BIGINT REFERENCES sedes(id);
ALTER TABLE proveedores ADD COLUMN IF NOT EXISTS sede_id BIGINT REFERENCES sedes(id);
ALTER TABLE roles ADD COLUMN IF NOT EXISTS sede_id BIGINT REFERENCES sedes(id);
ALTER TABLE servicios ADD COLUMN IF NOT EXISTS sede_id BIGINT REFERENCES sedes(id);
ALTER TABLE vehiculos ADD COLUMN IF NOT EXISTS sede_id BIGINT REFERENCES sedes(id);
ALTER TABLE ordenes_productos ADD COLUMN IF NOT EXISTS sede_id BIGINT REFERENCES sedes(id);
ALTER TABLE ordenes_servicios ADD COLUMN IF NOT EXISTS sede_id BIGINT REFERENCES sedes(id);
ALTER TABLE orden_historial_estados ADD COLUMN IF NOT EXISTS sede_id BIGINT REFERENCES sedes(id);
ALTER TABLE orden_observaciones ADD COLUMN IF NOT EXISTS sede_id BIGINT REFERENCES sedes(id);
ALTER TABLE configuracion_empresa ADD COLUMN IF NOT EXISTS sede_id BIGINT REFERENCES sedes(id);
ALTER TABLE parametros_sistema ADD COLUMN IF NOT EXISTS sede_id BIGINT REFERENCES sedes(id);
ALTER TABLE configuracion_numeracion ADD COLUMN IF NOT EXISTS sede_id BIGINT REFERENCES sedes(id);
ALTER TABLE configuracion_impuestos ADD COLUMN IF NOT EXISTS sede_id BIGINT REFERENCES sedes(id);
ALTER TABLE horarios_atencion ADD COLUMN IF NOT EXISTS sede_id BIGINT REFERENCES sedes(id);
ALTER TABLE dias_festivos ADD COLUMN IF NOT EXISTS sede_id BIGINT REFERENCES sedes(id);
ALTER TABLE auditoria_global ADD COLUMN IF NOT EXISTS sede_id BIGINT REFERENCES sedes(id);
ALTER TABLE backups_registro ADD COLUMN IF NOT EXISTS sede_id BIGINT REFERENCES sedes(id);
ALTER TABLE permisos_modulo ADD COLUMN IF NOT EXISTS sede_id BIGINT REFERENCES sedes(id);
ALTER TABLE notas_crm ADD COLUMN IF NOT EXISTS sede_id BIGINT REFERENCES sedes(id);
ALTER TABLE mantenimiento_recomendaciones ADD COLUMN IF NOT EXISTS sede_id BIGINT REFERENCES sedes(id);
ALTER TABLE metodos_pago ADD COLUMN IF NOT EXISTS sede_id BIGINT REFERENCES sedes(id);

CREATE INDEX IF NOT EXISTS idx_empresas_nit ON empresas(nit);
CREATE INDEX IF NOT EXISTS idx_sedes_empresa ON sedes(empresa_id);
