-- ============================================================
-- V015: Performance indexes
-- Foreign Keys, WHERE clauses, and composite multi-tenant indexes
-- ============================================================

-- FK columns
CREATE INDEX IF NOT EXISTS idx_servicios_categoria ON servicios(categoria_id);
CREATE INDEX IF NOT EXISTS idx_productos_proveedor ON productos(proveedor_id);
CREATE INDEX IF NOT EXISTS idx_os_servicio ON ordenes_servicios(servicio_id);
CREATE INDEX IF NOT EXISTS idx_op_producto ON ordenes_productos(producto_id);
CREATE INDEX IF NOT EXISTS idx_ot_tecnico ON ordenes_trabajo(tecnico_id);
CREATE INDEX IF NOT EXISTS idx_pagos_metodo_pago ON pagos(metodo_pago_id);
CREATE INDEX IF NOT EXISTS idx_pagos_original ON pagos(pago_original_id);
CREATE INDEX IF NOT EXISTS idx_mc_orden ON movimientos_caja(orden_id);
CREATE INDEX IF NOT EXISTS idx_mc_metodo_pago ON movimientos_caja(metodo_pago_id);
CREATE INDEX IF NOT EXISTS idx_ti_sede_origen ON transferencias_inventario(sede_origen_id);
CREATE INDEX IF NOT EXISTS idx_ti_sede_destino ON transferencias_inventario(sede_destino_id);
CREATE INDEX IF NOT EXISTS idx_tp_transferencia ON transferencia_productos(transferencia_id);
CREATE INDEX IF NOT EXISTS idx_tp_producto ON transferencia_productos(producto_id);
CREATE INDEX IF NOT EXISTS idx_aud_global_entidad ON auditoria_global(entidad, entidad_id);

-- WHERE clause columns
CREATE INDEX IF NOT EXISTS idx_ot_estado_financiero ON ordenes_trabajo(estado_financiero);
CREATE INDEX IF NOT EXISTS idx_clientes_clasificacion ON clientes(clasificacion);
CREATE INDEX IF NOT EXISTS idx_oc_fecha ON ordenes_compra(fecha);
CREATE INDEX IF NOT EXISTS idx_mi_tipo ON movimientos_inventario(tipo_movimiento);

-- Composite multi-tenant indexes (most common query patterns)
CREATE INDEX IF NOT EXISTS idx_ot_empresa_estado ON ordenes_trabajo(empresa_id, estado);
CREATE INDEX IF NOT EXISTS idx_ot_empresa_estado_fin ON ordenes_trabajo(empresa_id, estado_financiero);
CREATE INDEX IF NOT EXISTS idx_ot_empresa_fecha ON ordenes_trabajo(empresa_id, fecha_ingreso DESC);
CREATE INDEX IF NOT EXISTS idx_ot_cliente_fecha ON ordenes_trabajo(cliente_id, fecha_ingreso DESC);
CREATE INDEX IF NOT EXISTS idx_ot_vehiculo_fecha ON ordenes_trabajo(vehiculo_id, fecha_ingreso DESC);
CREATE INDEX IF NOT EXISTS idx_clientes_empresa_activo ON clientes(empresa_id, activo);
CREATE INDEX IF NOT EXISTS idx_productos_empresa_activo ON productos(empresa_id, activo);
CREATE INDEX IF NOT EXISTS idx_productos_activo_categoria ON productos(activo, categoria_id);
CREATE INDEX IF NOT EXISTS idx_productos_activo_proveedor ON productos(activo, proveedor_id);
CREATE INDEX IF NOT EXISTS idx_pagos_empresa_orden ON pagos(empresa_id, orden_id);
CREATE INDEX IF NOT EXISTS idx_pagos_empresa_fecha ON pagos(empresa_id, fecha);
CREATE INDEX IF NOT EXISTS idx_oc_empresa_estado_fecha ON ordenes_compra(empresa_id, estado, fecha);
CREATE INDEX IF NOT EXISTS idx_oc_empresa_proveedor ON ordenes_compra(empresa_id, proveedor_id);
CREATE INDEX IF NOT EXISTS idx_mi_empresa_producto_fecha ON movimientos_inventario(empresa_id, producto_id, fecha_movimiento DESC);
CREATE INDEX IF NOT EXISTS idx_aud_global_empresa_modulo ON auditoria_global(empresa_id, modulo);
