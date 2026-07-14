-- Stock máximo y punto de reposición en productos
ALTER TABLE productos ADD COLUMN IF NOT EXISTS stock_maximo INTEGER;
ALTER TABLE productos ADD COLUMN IF NOT EXISTS punto_reposicion INTEGER;

-- Órdenes de Compra
CREATE TABLE ordenes_compra (
    id BIGSERIAL PRIMARY KEY,
    numero_orden VARCHAR(20) NOT NULL UNIQUE,
    proveedor_id BIGINT REFERENCES proveedores(id),
    fecha TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    estado VARCHAR(20) NOT NULL DEFAULT 'BORRADOR',
    total DECIMAL(12,2) NOT NULL DEFAULT 0,
    observaciones VARCHAR(500),
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_modificacion TIMESTAMP,
    creado_por VARCHAR(50),
    modificado_por VARCHAR(50),
    activo BOOLEAN DEFAULT TRUE,
    fecha_eliminacion TIMESTAMP,
    eliminado_por VARCHAR(50)
);

CREATE TABLE ordenes_compra_productos (
    id BIGSERIAL PRIMARY KEY,
    orden_compra_id BIGINT NOT NULL REFERENCES ordenes_compra(id),
    producto_id BIGINT NOT NULL REFERENCES productos(id),
    cantidad INTEGER NOT NULL,
    precio_unitario DECIMAL(10,2) NOT NULL,
    subtotal DECIMAL(12,2) NOT NULL
);

CREATE INDEX idx_oc_proveedor ON ordenes_compra(proveedor_id);
CREATE INDEX idx_oc_estado ON ordenes_compra(estado);
CREATE INDEX idx_ocp_orden ON ordenes_compra_productos(orden_compra_id);
CREATE INDEX idx_ocp_producto ON ordenes_compra_productos(producto_id);
