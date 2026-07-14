CREATE TABLE metodos_pago (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL UNIQUE,
    activo BOOLEAN NOT NULL DEFAULT TRUE
);

INSERT INTO metodos_pago (nombre) VALUES ('Efectivo');
INSERT INTO metodos_pago (nombre) VALUES ('Tarjeta Débito');
INSERT INTO metodos_pago (nombre) VALUES ('Tarjeta Crédito');
INSERT INTO metodos_pago (nombre) VALUES ('Transferencia');
INSERT INTO metodos_pago (nombre) VALUES ('Nequi');
INSERT INTO metodos_pago (nombre) VALUES ('Daviplata');
INSERT INTO metodos_pago (nombre) VALUES ('Otro');

CREATE TABLE caja (
    id BIGSERIAL PRIMARY KEY,
    usuario VARCHAR(50) NOT NULL,
    fecha_apertura TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_cierre TIMESTAMP,
    monto_inicial DECIMAL(12,2) NOT NULL DEFAULT 0,
    total_ingresos DECIMAL(12,2) NOT NULL DEFAULT 0,
    total_egresos DECIMAL(12,2) NOT NULL DEFAULT 0,
    total_esperado DECIMAL(12,2) NOT NULL DEFAULT 0,
    monto_contado DECIMAL(12,2),
    diferencia DECIMAL(12,2),
    observaciones VARCHAR(500),
    estado VARCHAR(20) NOT NULL DEFAULT 'ABIERTA'
);

CREATE INDEX idx_caja_usuario ON caja(usuario);
CREATE INDEX idx_caja_estado ON caja(estado);

CREATE TABLE pagos (
    id BIGSERIAL PRIMARY KEY,
    orden_id BIGINT NOT NULL REFERENCES ordenes_trabajo(id),
    usuario VARCHAR(50) NOT NULL,
    fecha TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    metodo_pago_id BIGINT NOT NULL REFERENCES metodos_pago(id),
    valor DECIMAL(12,2) NOT NULL,
    observacion VARCHAR(500),
    pago_anulado BOOLEAN NOT NULL DEFAULT FALSE,
    pago_original_id BIGINT REFERENCES pagos(id)
);

CREATE INDEX idx_pagos_orden ON pagos(orden_id);
CREATE INDEX idx_pagos_fecha ON pagos(fecha);

ALTER TABLE ordenes_trabajo ADD COLUMN estado_financiero VARCHAR(20) NOT NULL DEFAULT 'SIN_PAGAR';

CREATE TABLE movimientos_caja (
    id BIGSERIAL PRIMARY KEY,
    caja_id BIGINT REFERENCES caja(id),
    tipo VARCHAR(30) NOT NULL,
    descripcion VARCHAR(500),
    monto DECIMAL(12,2) NOT NULL DEFAULT 0,
    usuario VARCHAR(50) NOT NULL,
    fecha TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    orden_id BIGINT REFERENCES ordenes_trabajo(id),
    metodo_pago_id BIGINT REFERENCES metodos_pago(id)
);

CREATE INDEX idx_mov_caja_id ON movimientos_caja(caja_id);
CREATE INDEX idx_mov_caja_fecha ON movimientos_caja(fecha);
