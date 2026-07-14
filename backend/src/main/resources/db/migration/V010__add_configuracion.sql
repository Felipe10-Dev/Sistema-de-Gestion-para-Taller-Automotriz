-- Información de la empresa
CREATE TABLE IF NOT EXISTS configuracion_empresa (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(200),
    razon_social VARCHAR(200),
    nit VARCHAR(20),
    direccion VARCHAR(200),
    ciudad VARCHAR(100),
    telefono VARCHAR(20),
    email VARCHAR(150),
    sitio_web VARCHAR(200),
    logo VARCHAR(500),
    moneda VARCHAR(10) DEFAULT 'COP',
    zona_horaria VARCHAR(50) DEFAULT 'America/Bogota',
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_modificacion TIMESTAMP,
    activo BOOLEAN DEFAULT TRUE
);

-- Parámetros globales del sistema
CREATE TABLE IF NOT EXISTS parametros_sistema (
    id BIGSERIAL PRIMARY KEY,
    codigo VARCHAR(50) NOT NULL UNIQUE,
    nombre VARCHAR(200) NOT NULL,
    valor TEXT NOT NULL,
    descripcion VARCHAR(500),
    tipo VARCHAR(20) NOT NULL DEFAULT 'STRING',
    activo BOOLEAN DEFAULT TRUE
);

-- Configuración de numeración automática
CREATE TABLE IF NOT EXISTS configuracion_numeracion (
    id BIGSERIAL PRIMARY KEY,
    modulo VARCHAR(30) NOT NULL UNIQUE,
    prefijo VARCHAR(10) DEFAULT '',
    sufijo VARCHAR(10) DEFAULT '',
    longitud INTEGER NOT NULL DEFAULT 8,
    numero_actual INTEGER NOT NULL DEFAULT 0,
    numero_inicial INTEGER NOT NULL DEFAULT 1,
    reinicio_anual BOOLEAN DEFAULT FALSE,
    activo BOOLEAN DEFAULT TRUE
);

-- Configuración de impuestos
CREATE TABLE IF NOT EXISTS configuracion_impuestos (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    porcentaje DECIMAL(5,2) NOT NULL,
    activo BOOLEAN DEFAULT TRUE,
    aplicacion_por_defecto BOOLEAN DEFAULT FALSE
);

-- Horarios de atención
CREATE TABLE IF NOT EXISTS horarios_atencion (
    id BIGSERIAL PRIMARY KEY,
    dia_semana VARCHAR(10) NOT NULL,
    hora_apertura TIME NOT NULL,
    hora_cierre TIME NOT NULL,
    activo BOOLEAN DEFAULT TRUE
);

-- Días festivos personalizados
CREATE TABLE IF NOT EXISTS dias_festivos (
    id BIGSERIAL PRIMARY KEY,
    fecha DATE NOT NULL,
    descripcion VARCHAR(200),
    activo BOOLEAN DEFAULT TRUE
);

-- Auditoría global (nunca se elimina)
CREATE TABLE IF NOT EXISTS auditoria_global (
    id BIGSERIAL PRIMARY KEY,
    usuario VARCHAR(50) NOT NULL,
    fecha TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    accion VARCHAR(50) NOT NULL,
    modulo VARCHAR(50) NOT NULL,
    entidad VARCHAR(100),
    entidad_id BIGINT,
    valor_anterior TEXT,
    valor_nuevo TEXT
);

CREATE INDEX IF NOT EXISTS idx_auditoria_usuario ON auditoria_global(usuario);
CREATE INDEX IF NOT EXISTS idx_auditoria_fecha ON auditoria_global(fecha);
CREATE INDEX IF NOT EXISTS idx_auditoria_modulo ON auditoria_global(modulo);

-- Registro de backups
CREATE TABLE IF NOT EXISTS backups_registro (
    id BIGSERIAL PRIMARY KEY,
    fecha TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    usuario VARCHAR(50),
    tamanio VARCHAR(20),
    estado VARCHAR(20) NOT NULL DEFAULT 'EXITOSO',
    observaciones VARCHAR(500)
);

-- Permisos configurables por módulo
CREATE TABLE IF NOT EXISTS permisos_modulo (
    id BIGSERIAL PRIMARY KEY,
    rol_id BIGINT NOT NULL REFERENCES roles(id),
    modulo VARCHAR(50) NOT NULL,
    permiso VARCHAR(20) NOT NULL,
    activo BOOLEAN DEFAULT TRUE
);

CREATE UNIQUE INDEX IF NOT EXISTS idx_permiso_rol_mod_perm ON permisos_modulo(rol_id, modulo, permiso);

-- ============================================================
-- Seed Data
-- ============================================================

-- Empresa por defecto
INSERT INTO configuracion_empresa (nombre, razon_social, nit, moneda, zona_horaria)
VALUES ('Mi Serviteca', 'Mi Serviteca SAS', '900000000-0', 'COP', 'America/Bogota');

-- Parámetros globales
INSERT INTO parametros_sistema (codigo, nombre, valor, tipo, descripcion) VALUES
('IVA_DEFECTO', 'IVA por defecto', '19', 'DECIMAL', 'Porcentaje de IVA aplicado por defecto'),
('DESCUENTO_MAXIMO', 'Descuento máximo permitido', '30', 'DECIMAL', 'Porcentaje máximo de descuento permitido'),
('DIAS_INACTIVO', 'Días para cliente inactivo', '365', 'INTEGER', 'Días sin visitas para considerar cliente inactivo'),
('MONTO_MINIMO_VIP', 'Monto mínimo para cliente VIP', '5000000', 'DECIMAL', 'Total gastado mínimo para clasificar como VIP'),
('ORDENES_FRECUENTE', 'Órdenes mínimas para cliente frecuente', '5', 'INTEGER', 'Número mínimo de órdenes para clasificar como frecuente'),
('KM_CAMBIO_ACEITE', 'Kilómetros para cambio de aceite', '5000', 'INTEGER', 'Kilómetros por defecto para cambio de aceite'),
('DIAS_MANTENIMIENTO', 'Días para mantenimiento preventivo', '180', 'INTEGER', 'Días por defecto para mantenimiento preventivo'),
('MONEDA_DEFECTO', 'Moneda del sistema', 'COP', 'STRING', 'Moneda utilizada en el sistema'),
('ZONA_HORARIA', 'Zona horaria del sistema', 'America/Bogota', 'STRING', 'Zona horaria predeterminada'),
('ALERTA_KM_PRONTO', 'Km para alerta PRONTO', '1000', 'INTEGER', 'Kilómetros restantes para alerta PRONTO'),
('ALERTA_KM_PROXIMO', 'Km para alerta PROXIMO', '500', 'INTEGER', 'Kilómetros restantes para alerta PROXIMO'),
('ALERTA_DIAS_PRONTO', 'Días para alerta PRONTO', '30', 'INTEGER', 'Días restantes para alerta PRONTO'),
('ALERTA_DIAS_PROXIMO', 'Días para alerta PROXIMO', '15', 'INTEGER', 'Días restantes para alerta PROXIMO');

-- Numeración automática
INSERT INTO configuracion_numeracion (modulo, prefijo, longitud, numero_inicial, numero_actual) VALUES
('ORDEN_TRABAJO', 'ORD-', 8, 1, 15),
('ORDEN_COMPRA', 'OC-', 8, 1, 0),
('PAGO', 'PAG-', 8, 1, 0),
('CAJA', 'CAJ-', 8, 1, 0);

-- Impuestos
INSERT INTO configuracion_impuestos (nombre, porcentaje, activo, aplicacion_por_defecto) VALUES
('IVA 19%', 19.00, true, true),
('IVA 5%', 5.00, true, false),
('Exento', 0.00, true, false);

-- Horarios de atención
INSERT INTO horarios_atencion (dia_semana, hora_apertura, hora_cierre, activo) VALUES
('LUNES', '08:00', '18:00', true),
('MARTES', '08:00', '18:00', true),
('MIERCOLES', '08:00', '18:00', true),
('JUEVES', '08:00', '18:00', true),
('VIERNES', '08:00', '18:00', true),
('SABADO', '08:00', '13:00', true),
('DOMINGO', '08:00', '13:00', false);

-- Permisos completos para ADMIN
INSERT INTO permisos_modulo (rol_id, modulo, permiso)
SELECT r.id, m.modulo, p.permiso
FROM roles r
CROSS JOIN (VALUES ('CLIENTES'), ('VEHICULOS'), ('ORDENES'), ('COMPRAS'), ('INVENTARIO'),
                  ('CAJA'), ('PAGOS'), ('USUARIOS'), ('SERVICIOS'), ('PRODUCTOS'),
                  ('PROVEEDORES'), ('CONFIGURACION'), ('CRM'), ('REPORTES')) AS m(modulo)
CROSS JOIN (VALUES ('CREAR'), ('LEER'), ('ACTUALIZAR'), ('ELIMINAR')) AS p(permiso)
WHERE r.nombre = 'ADMIN';
