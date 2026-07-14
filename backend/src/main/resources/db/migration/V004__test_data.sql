-- V004: Datos de prueba para validación MVP
-- Compatible con PostgreSQL y H2 (MODE=PostgreSQL)
-- NOTA: H2 no soporta INTERVAL 'X days', se usa DATEADD o aritmética simple

-- ============================================================
-- 1. PROVEEDORES (5)
-- ============================================================
INSERT INTO proveedores (nit, nombre, contacto, telefono, email, direccion, fecha_creacion, activo) VALUES
('900123456-1', 'Distribuidora Autopartes SAS', 'Carlos Mendez', '3001234567', 'carlos@distautopartes.com', 'Cra 30 #45-12, Bogotá', CURRENT_TIMESTAMP, true),
('900123457-2', 'Lubricantes del Valle SAS', 'Maria Gutierrez', '3102345678', 'maria@lubricantesvalle.com', 'Av 4N #23-45, Cali', CURRENT_TIMESTAMP, true),
('900123458-3', 'Filtros y Accesorios LTDA', 'Pedro Ramirez', '3203456789', 'pedro@filtrosyaccesorios.com', 'Clle 70 #12-34, Medellín', CURRENT_TIMESTAMP, true),
('900123459-4', 'Herramientas Profesionales SAS', 'Ana Martinez', '3014567890', 'ana@herramientaspro.com', 'Av El Dorado #68-90, Bogotá', CURRENT_TIMESTAMP, true),
('900123460-5', 'Repuestos El Motor SAS', 'Jorge Lopez', '3155678901', 'jorge@repuestoselmotor.com', 'Cra 50 #80-20, Barranquilla', CURRENT_TIMESTAMP, true);

-- ============================================================
-- 2. CATEGORIAS (5)
-- ============================================================
INSERT INTO categorias (nombre, descripcion, fecha_creacion, activo) VALUES
('Aceites y Lubricantes', 'Aceites de motor, transmisión, hidráulicos y lubricantes', CURRENT_TIMESTAMP, true),
('Filtros', 'Filtros de aceite, aire, combustible y cabina', CURRENT_TIMESTAMP, true),
('Pastillas y Frenos', 'Pastillas, discos, líquido de frenos y componentes', CURRENT_TIMESTAMP, true),
('Suspensión y Dirección', 'Amortiguadores, rótulas, bieletas y cremalleras', CURRENT_TIMESTAMP, true),
('Eléctrico', 'Baterías, bujías, alternadores y sensores', CURRENT_TIMESTAMP, true);

-- ============================================================
-- 3. SERVICIOS (10)
-- ============================================================
INSERT INTO servicios (nombre, descripcion, precio, duracion_estimada_minutos, categoria_id, fecha_creacion, activo) VALUES
('Cambio de Aceite y Filtro', 'Cambio de aceite de motor más filtro de aceite. Incluye hasta 5 litros.', 85000, 30, 1, CURRENT_TIMESTAMP, true),
('Alineación y Balanceo', 'Alineación computarizada + balanceo dinámico de 4 ruedas', 120000, 45, 4, CURRENT_TIMESTAMP, true),
('Revisión General Preventiva', 'Revisión completa de 50 puntos: frenos, suspensión, motor, eléctrico', 95000, 60, NULL, CURRENT_TIMESTAMP, true),
('Cambio de Pastillas de Freno', 'Cambio de pastillas de freno delanteras o traseras. No incluye discos.', 180000, 60, 3, CURRENT_TIMESTAMP, true),
('Rotación de Neumáticos', 'Rotación cruzada de 4 neumáticos + verificación de presión', 35000, 20, 4, CURRENT_TIMESTAMP, true),
('Escaneo Computarizado', 'Diagnóstico electrónico con escáner profesional', 70000, 30, 5, CURRENT_TIMESTAMP, true),
('Cambio de Bujías', 'Cambio de 4 bujías. Incluye verificación de bobinas.', 130000, 45, 5, CURRENT_TIMESTAMP, true),
('Limpieza de Inyectores', 'Limpieza ultrasónica de inyectores + aditivo', 160000, 90, 1, CURRENT_TIMESTAMP, true),
('Cambio de Amortiguadores', 'Cambio de 2 amortiguadores (eje delantero o trasero)', 250000, 120, 4, CURRENT_TIMESTAMP, true),
('Revisión de Aire Acondicionado', 'Verificación de presión, recarga de gas y limpieza de condensador', 140000, 60, 5, CURRENT_TIMESTAMP, true);

-- ============================================================
-- 4. PRODUCTOS (15)
-- ============================================================
INSERT INTO productos (codigo, nombre, descripcion, precio_compra, precio_venta, stock_minimo, categoria_id, proveedor_id, fecha_creacion, activo) VALUES
('ACE-001', 'Aceite Motor 20W50 Galón', 'Aceite multigrado 20W50 para motor gasolina - 4 litros', 45000, 72000, 10, 1, 1, CURRENT_TIMESTAMP, true),
('ACE-002', 'Aceite Motor 10W40 Galón', 'Aceite multigrado 10W40 para motor gasolina - 4 litros', 48000, 78000, 10, 1, 1, CURRENT_TIMESTAMP, true),
('FIL-001', 'Filtro de Aceite', 'Filtro de aceite estándar para vehículo particular', 8500, 18000, 15, 2, 3, CURRENT_TIMESTAMP, true),
('FIL-002', 'Filtro de Aire', 'Filtro de aire para motor estándar', 12000, 25000, 10, 2, 3, CURRENT_TIMESTAMP, true),
('PAS-001', 'Pastillas de Freno Delanteras', 'Pastillas de freno semimetálicas para vehículo particular', 35000, 75000, 8, 3, 2, CURRENT_TIMESTAMP, true),
('PAS-002', 'Pastillas de Freno Traseras', 'Pastillas de freno semimetálicas para eje trasero', 32000, 70000, 8, 3, 2, CURRENT_TIMESTAMP, true),
('SUS-001', 'Amortiguador Delantero', 'Amortiguador hidráulico para vehículo particular', 55000, 120000, 5, 4, 5, CURRENT_TIMESTAMP, true),
('SUS-002', 'Amortiguador Trasero', 'Amortiguador hidráulico para eje trasero', 52000, 115000, 5, 4, 5, CURRENT_TIMESTAMP, true),
('ELE-001', 'Batería 45Ah', 'Batería de 45 amperios para vehículo particular', 120000, 220000, 6, 5, 1, CURRENT_TIMESTAMP, true),
('ELE-002', 'Bujía NGK Estándar', 'Bujía de encendido estándar (unidad)', 8000, 18000, 20, 5, 4, CURRENT_TIMESTAMP, true),
('LUB-001', 'Líquido de Frenos DOT4', 'Líquido de frenos DOT4 - 500ml', 12000, 28000, 10, 3, 2, CURRENT_TIMESTAMP, true),
('ACE-003', 'Aceite Hidráulico', 'Aceite para dirección hidráulica - 1 litro', 15000, 32000, 8, 1, 1, CURRENT_TIMESTAMP, true),
('FIL-003', 'Filtro de Combustible', 'Filtro de combustible en línea estándar', 9500, 22000, 10, 2, 3, CURRENT_TIMESTAMP, true),
('SUS-003', 'Rótula de Dirección', 'Rótula de dirección estándar', 18000, 42000, 6, 4, 5, CURRENT_TIMESTAMP, true),
('REF-001', 'Refrigerante Motor Galón', 'Refrigerante concentrado para radiador - 4 litros', 25000, 45000, 8, 1, 1, CURRENT_TIMESTAMP, true);

-- ============================================================
-- 5. INVENTARIO (15 registros - one per product)
-- ============================================================
INSERT INTO inventario (producto_id, cantidad_actual, cantidad_minima, ubicacion, fecha_creacion, activo)
SELECT p.id,
       CASE p.codigo
           WHEN 'ACE-001' THEN 25 WHEN 'ACE-002' THEN 20
           WHEN 'FIL-001' THEN 40 WHEN 'FIL-002' THEN 30
           WHEN 'PAS-001' THEN 12 WHEN 'PAS-002' THEN 10
           WHEN 'SUS-001' THEN 6  WHEN 'SUS-002' THEN 4
           WHEN 'ELE-001' THEN 8  WHEN 'ELE-002' THEN 50
           WHEN 'LUB-001' THEN 15 WHEN 'ACE-003' THEN 12
           WHEN 'FIL-003' THEN 20 WHEN 'SUS-003' THEN 8
           WHEN 'REF-001' THEN 14
           ELSE 10
       END,
       p.stock_minimo,
       CASE p.codigo
           WHEN 'ACE-001' THEN 'Estante A-1' WHEN 'ACE-002' THEN 'Estante A-2'
           WHEN 'FIL-001' THEN 'Estante B-1' WHEN 'FIL-002' THEN 'Estante B-2'
           WHEN 'PAS-001' THEN 'Estante C-1' WHEN 'PAS-002' THEN 'Estante C-2'
           WHEN 'SUS-001' THEN 'Estante D-1' WHEN 'SUS-002' THEN 'Estante D-2'
           WHEN 'ELE-001' THEN 'Estante E-1' WHEN 'ELE-002' THEN 'Estante E-3'
           WHEN 'LUB-001' THEN 'Estante C-3' WHEN 'ACE-003' THEN 'Estante A-3'
           WHEN 'FIL-003' THEN 'Estante B-3' WHEN 'SUS-003' THEN 'Estante D-3'
           WHEN 'REF-001' THEN 'Estante A-4'
           ELSE 'Bodega'
       END,
       CURRENT_TIMESTAMP, true
FROM productos p;

-- ============================================================
-- 6. CLIENTES (20)
-- ============================================================
INSERT INTO clientes (tipo_documento, numero_documento, nombre, apellido, telefono, email, direccion, fecha_creacion, activo) VALUES
('CC', '1012345678', 'Juan', 'Rodriguez', '3101112233', 'juan.rodriguez@email.com', 'Cra 15 #45-67, Bogotá', CURRENT_TIMESTAMP, true),
('CC', '1023456789', 'Maria', 'Lopez', '3112223344', 'maria.lopez@email.com', 'Cll 72 #20-30, Bogotá', CURRENT_TIMESTAMP, true),
('CC', '1034567890', 'Carlos', 'Martinez', '3123334455', 'carlos.mtz@email.com', 'Av 68 #55-10, Bogotá', CURRENT_TIMESTAMP, true),
('CC', '1045678901', 'Ana', 'Garcia', '3134445566', 'ana.garcia@email.com', 'Cra 7 #32-15, Bogotá', CURRENT_TIMESTAMP, true),
('CC', '1056789012', 'Pedro', 'Sanchez', '3145556677', 'pedro.sanchez@email.com', 'Cll 100 #15-40, Bogotá', CURRENT_TIMESTAMP, true),
('CC', '1067890123', 'Laura', 'Ramirez', '3156667788', 'laura.ramirez@email.com', 'Av Suba #120-30, Bogotá', CURRENT_TIMESTAMP, true),
('CC', '1078901234', 'Andres', 'Torres', '3167778899', 'andres.torres@email.com', 'Cra 50 #80-15, Bogotá', CURRENT_TIMESTAMP, true),
('CC', '1089012345', 'Diana', 'Gonzalez', '3178889900', 'diana.gonzalez@email.com', 'Cll 26 #10-50, Bogotá', CURRENT_TIMESTAMP, true),
('CC', '1090123456', 'Luis', 'Perez', '3189990011', 'luis.perez@email.com', 'Cra 30 #25-60, Bogotá', CURRENT_TIMESTAMP, true),
('CC', '1101234567', 'Sofia', 'Diaz', '3190001122', 'sofia.diaz@email.com', 'Av Americas #42-80, Bogotá', CURRENT_TIMESTAMP, true),
('NIT', '900500200-1', 'Transportes Rápidos SAS', 'Oficina Principal', '6012345678', 'admin@transportesrapidos.com', 'Cra 45 #12-89, Bogotá', CURRENT_TIMESTAMP, true),
('NIT', '900500300-2', 'Mensajería Express LTDA', 'Oficina Principal', '6013456789', 'contacto@mensajeriaexpress.com', 'Av Caracas #36-55, Bogotá', CURRENT_TIMESTAMP, true),
('CC', '1112345678', 'Jorge', 'Herrera', '3201112233', 'jorge.herrera@email.com', 'Cll 80 #18-40, Bogotá', CURRENT_TIMESTAMP, true),
('CC', '1123456789', 'Claudia', 'Jimenez', '3212223344', 'claudia.jimenez@email.com', 'Cra 19 #90-20, Bogotá', CURRENT_TIMESTAMP, true),
('CC', '1134567890', 'Diego', 'Alvarez', '3223334455', 'diego.alvarez@email.com', 'Av Cali #30-70, Bogotá', CURRENT_TIMESTAMP, true),
('CC', '1145678901', 'Valentina', 'Moreno', '3234445566', 'valentina.moreno@email.com', 'Cll 134 #25-60, Bogotá', CURRENT_TIMESTAMP, true),
('CC', '1156789012', 'Fernando', 'Castro', '3245556677', 'fernando.castro@email.com', 'Cra 9 #55-30, Bogotá', CURRENT_TIMESTAMP, true),
('CC', '1167890123', 'Paola', 'Ortiz', '3256667788', 'paola.ortiz@email.com', 'Av 1 de Mayo #10-90, Bogotá', CURRENT_TIMESTAMP, true),
('NIT', '900500400-3', 'Distribuciones ABC SAS', 'Gerencia', '6023456789', 'gerencia@distabc.com', 'Cra 68 #15-45, Bogotá', CURRENT_TIMESTAMP, true),
('CC', '1178901234', 'Oscar', 'Vargas', '3267778899', 'oscar.vargas@email.com', 'Cll 63 #20-10, Bogotá', CURRENT_TIMESTAMP, true);

-- ============================================================
-- 7. VEHICULOS (30)
-- ============================================================
INSERT INTO vehiculos (placa, marca, linea, modelo, color, cilindraje, tipo_vehiculo, cliente_id, fecha_creacion, activo) VALUES
('ABC123', 'Toyota', 'Corolla', '2020', 'Blanco', '1800', 'Sedán', 1, CURRENT_TIMESTAMP, true),
('DEF456', 'Mazda', '3', '2021', 'Rojo', '2000', 'Sedán', 2, CURRENT_TIMESTAMP, true),
('GHI789', 'Chevrolet', 'Spark GT', '2019', 'Azul', '1200', 'Hatchback', 3, CURRENT_TIMESTAMP, true),
('JKL012', 'Renault', 'Duster', '2022', 'Gris', '1600', 'SUV', 4, CURRENT_TIMESTAMP, true),
('MNO345', 'Nissan', 'Versa', '2020', 'Negro', '1600', 'Sedán', 5, CURRENT_TIMESTAMP, true),
('PQR678', 'Hyundai', 'Tucson', '2021', 'Plateado', '2000', 'SUV', 6, CURRENT_TIMESTAMP, true),
('STU901', 'Kia', 'Picanto', '2023', 'Blanco', '1000', 'Hatchback', 7, CURRENT_TIMESTAMP, true),
('VWX234', 'Volkswagen', 'Gol', '2018', 'Gris', '1400', 'Hatchback', 8, CURRENT_TIMESTAMP, true),
('YZA567', 'Ford', 'Escape', '2020', 'Azul', '2500', 'SUV', 9, CURRENT_TIMESTAMP, true),
('BCD890', 'Chevrolet', 'Tracker', '2022', 'Rojo', '1400', 'SUV', 10, CURRENT_TIMESTAMP, true),
('EFG123', 'Toyota', 'Hilux', '2021', 'Blanco', '2800', 'Camioneta', 11, CURRENT_TIMESTAMP, true),
('HIJ456', 'Mazda', 'CX-5', '2023', 'Negro', '2000', 'SUV', 12, CURRENT_TIMESTAMP, true),
('KLM789', 'Renault', 'Logan', '2020', 'Plata', '1200', 'Sedán', 13, CURRENT_TIMESTAMP, true),
('NOP012', 'Suzuki', 'Swift', '2022', 'Rojo', '1200', 'Hatchback', 14, CURRENT_TIMESTAMP, true),
('QRS345', 'Chevrolet', 'Onix', '2021', 'Azul', '1400', 'Sedán', 15, CURRENT_TIMESTAMP, true),
('TUV678', 'Hyundai', 'Grand i10', '2023', 'Blanco', '1200', 'Hatchback', 1, CURRENT_TIMESTAMP, true),
('WXY901', 'Nissan', 'NP300', '2020', 'Gris', '2500', 'Camioneta', 11, CURRENT_TIMESTAMP, true),
('ZAB234', 'Volkswagen', 'Jetta', '2022', 'Negro', '2000', 'Sedán', 2, CURRENT_TIMESTAMP, true),
('CDE567', 'Kia', 'Sportage', '2021', 'Plateado', '2000', 'SUV', 3, CURRENT_TIMESTAMP, true),
('FGH890', 'Mitsubishi', 'Montero', '2019', 'Verde', '3000', 'SUV', 5, CURRENT_TIMESTAMP, true),
('IJK123', 'Toyota', 'Yaris', '2023', 'Blanco', '1500', 'Sedán', 6, CURRENT_TIMESTAMP, true),
('LMN456', 'Chevrolet', 'Colorado', '2020', 'Negro', '2800', 'Camioneta', 11, CURRENT_TIMESTAMP, true),
('OPQ789', 'Ford', 'Ranger', '2021', 'Plata', '2500', 'Camioneta', 12, CURRENT_TIMESTAMP, true),
('RST012', 'Renault', 'Kwid', '2023', 'Naranja', '1000', 'Hatchback', 7, CURRENT_TIMESTAMP, true),
('UVW345', 'Mazda', '2', '2020', 'Gris', '1500', 'Hatchback', 16, CURRENT_TIMESTAMP, true),
('XYZ678', 'Hyundai', 'Santa Fe', '2022', 'Azul', '2400', 'SUV', 17, CURRENT_TIMESTAMP, true),
('ABC901', 'Suzuki', 'Vitara', '2021', 'Blanco', '1600', 'SUV', 18, CURRENT_TIMESTAMP, true),
('DEF234', 'Chevrolet', 'Blazer', '2018', 'Rojo', '3200', 'SUV', 19, CURRENT_TIMESTAMP, true),
('GHI567', 'Nissan', 'Sentra', '2023', 'Gris', '1800', 'Sedán', 20, CURRENT_TIMESTAMP, true),
('JKL890', 'Volkswagen', 'Amarok', '2021', 'Negro', '3000', 'Camioneta', 4, CURRENT_TIMESTAMP, true);

-- ============================================================
-- 8. ORDENES DE TRABAJO (15)
-- ============================================================
INSERT INTO ordenes_trabajo (numero_orden, cliente_id, vehiculo_id, kilometraje, estado, fecha_ingreso, tecnico_id, observaciones, total_servicios, total_productos, total_general, fecha_creacion, activo)
SELECT 'ORD-A1B2C3D4', 1, 1, 45000, 'PENDIENTE', CURRENT_TIMESTAMP, u.id,
       'Cambio de aceite programado', 85000, 90000, 175000, CURRENT_TIMESTAMP, true
FROM usuarios u WHERE u.username = 'admin';

INSERT INTO ordenes_trabajo (numero_orden, cliente_id, vehiculo_id, kilometraje, estado, fecha_ingreso, observaciones, total_servicios, total_productos, total_general, fecha_creacion, activo)
VALUES ('ORD-E5F6G7H8', 2, 2, 32000, 'PENDIENTE', CURRENT_TIMESTAMP,
        'Revisión de rutina', 95000, 0, 95000, CURRENT_TIMESTAMP, true);

INSERT INTO ordenes_trabajo (numero_orden, cliente_id, vehiculo_id, kilometraje, estado, fecha_ingreso, observaciones, total_servicios, total_productos, total_general, fecha_creacion, activo)
VALUES ('ORD-I9J0K1L2', 3, 3, 58000, 'EN_PROCESO', CURRENT_TIMESTAMP,
        'Pastillas delanteras gastadas', 180000, 103000, 283000, CURRENT_TIMESTAMP, true);

INSERT INTO ordenes_trabajo (numero_orden, cliente_id, vehiculo_id, kilometraje, estado, fecha_ingreso, observaciones, total_servicios, total_productos, total_general, fecha_creacion, activo)
VALUES ('ORD-M3N4O5P6', 4, 4, 22000, 'PENDIENTE', CURRENT_TIMESTAMP,
        'Vibración en volante', 120000, 0, 120000, CURRENT_TIMESTAMP, true);

INSERT INTO ordenes_trabajo (numero_orden, cliente_id, vehiculo_id, kilometraje, estado, fecha_ingreso, observaciones, total_servicios, total_productos, total_general, fecha_creacion, activo)
VALUES ('ORD-Q7R8S9T0', 5, 5, 65000, 'PENDIENTE', CURRENT_TIMESTAMP,
        'Revisar check engine, cambiar bujías', 200000, 72000, 272000, CURRENT_TIMESTAMP, true);

INSERT INTO ordenes_trabajo (numero_orden, cliente_id, vehiculo_id, kilometraje, estado, fecha_ingreso, observaciones, total_servicios, total_productos, total_general, fecha_creacion, activo)
VALUES ('ORD-U1V2W3X4', 6, 6, 48000, 'EN_PROCESO', CURRENT_TIMESTAMP,
        'Sonido en parte delantera - cambiar amortiguadores', 250000, 240000, 490000, CURRENT_TIMESTAMP, true);

INSERT INTO ordenes_trabajo (numero_orden, cliente_id, vehiculo_id, kilometraje, estado, fecha_ingreso, observaciones, total_servicios, total_productos, total_general, fecha_creacion, activo)
SELECT 'ORD-Y5Z6A7B8', 7, 7, 15000, 'PENDIENTE', CURRENT_TIMESTAMP,
       'Cambio de aceite - vehículo nuevo', 85000, 96000, 181000, CURRENT_TIMESTAMP, true;

INSERT INTO ordenes_trabajo (numero_orden, cliente_id, vehiculo_id, kilometraje, estado, fecha_ingreso, observaciones, total_servicios, total_productos, total_general, fecha_creacion, activo)
VALUES ('ORD-C9D0E1F2', 8, 8, 42000, 'PENDIENTE', CURRENT_TIMESTAMP,
        'Aire acondicionado no enfría', 140000, 0, 140000, CURRENT_TIMESTAMP, true);

INSERT INTO ordenes_trabajo (numero_orden, cliente_id, vehiculo_id, kilometraje, estado, fecha_ingreso, observaciones, total_servicios, total_productos, total_general, fecha_creacion, activo)
SELECT 'ORD-G3H4I5J6', 9, 9, 55000, 'PENDIENTE', CURRENT_TIMESTAMP,
       'Mantenimiento preventivo', 85000, 115000, 200000, CURRENT_TIMESTAMP, true;

INSERT INTO ordenes_trabajo (numero_orden, cliente_id, vehiculo_id, kilometraje, estado, fecha_ingreso, observaciones, total_servicios, total_productos, total_general, fecha_creacion, activo)
VALUES ('ORD-K7L8M9N0', 10, 10, 60000, 'PENDIENTE', CURRENT_TIMESTAMP,
        'Ruido en suspensión delantera', 250000, 0, 250000, CURRENT_TIMESTAMP, true);

INSERT INTO ordenes_trabajo (numero_orden, cliente_id, vehiculo_id, kilometraje, estado, fecha_ingreso, observaciones, total_servicios, total_productos, total_general, fecha_creacion, activo)
VALUES ('ORD-O1P2Q3R4', 11, 11, 82000, 'EN_PROCESO', CURRENT_TIMESTAMP,
        'No enciende bien en las mañanas', 95000, 220000, 315000, CURRENT_TIMESTAMP, true);

INSERT INTO ordenes_trabajo (numero_orden, cliente_id, vehiculo_id, kilometraje, estado, fecha_ingreso, observaciones, total_servicios, total_productos, total_general, fecha_creacion, activo)
SELECT 'ORD-S5T6U7V8', 12, 12, 18000, 'PENDIENTE', CURRENT_TIMESTAMP,
       'Mantenimiento preventivo flota', 95000, 90000, 185000, CURRENT_TIMESTAMP, true;

INSERT INTO ordenes_trabajo (numero_orden, cliente_id, vehiculo_id, kilometraje, estado, fecha_ingreso, observaciones, total_servicios, total_productos, total_general, fecha_creacion, activo)
VALUES ('ORD-W9X0Y1Z2', 19, 28, 95000, 'EN_PROCESO', CURRENT_TIMESTAMP,
        'Tirones al acelerar - revisar inyectores y bujías', 290000, 72000, 362000, CURRENT_TIMESTAMP, true);

INSERT INTO ordenes_trabajo (numero_orden, cliente_id, vehiculo_id, kilometraje, estado, fecha_ingreso, observaciones, total_servicios, total_productos, total_general, fecha_creacion, activo)
VALUES ('ORD-A3B4C5D6', 15, 15, 35000, 'PENDIENTE', CURRENT_TIMESTAMP,
        'Rotación de neumáticos', 35000, 0, 35000, CURRENT_TIMESTAMP, true);

INSERT INTO ordenes_trabajo (numero_orden, cliente_id, vehiculo_id, kilometraje, estado, fecha_ingreso, observaciones, total_servicios, total_productos, total_general, fecha_creacion, activo)
VALUES ('ORD-E7F8G9H0', 4, 30, 41000, 'EN_PROCESO', CURRENT_TIMESTAMP,
        'Cambio de refrigerante + revisión general', 95000, 45000, 140000, CURRENT_TIMESTAMP, true);

-- ============================================================
-- 9. DETALLE DE ORDENES - SERVICIOS
-- ============================================================
INSERT INTO ordenes_servicios (orden_id, servicio_id, cantidad, precio_unitario, subtotal, observaciones, fecha_creacion)
SELECT o.id, s.id, 1, s.precio, s.precio, 'Aceite 20W50', CURRENT_TIMESTAMP
FROM ordenes_trabajo o, servicios s
WHERE o.numero_orden = 'ORD-A1B2C3D4' AND s.nombre = 'Cambio de Aceite y Filtro';

INSERT INTO ordenes_servicios (orden_id, servicio_id, cantidad, precio_unitario, subtotal, fecha_creacion)
SELECT o.id, s.id, 1, s.precio, s.precio, CURRENT_TIMESTAMP
FROM ordenes_trabajo o, servicios s
WHERE o.numero_orden = 'ORD-E5F6G7H8' AND s.nombre = 'Revisión General Preventiva';

INSERT INTO ordenes_servicios (orden_id, servicio_id, cantidad, precio_unitario, subtotal, fecha_creacion)
SELECT o.id, s.id, 1, s.precio, s.precio, CURRENT_TIMESTAMP
FROM ordenes_trabajo o, servicios s
WHERE o.numero_orden = 'ORD-I9J0K1L2' AND s.nombre = 'Cambio de Pastillas de Freno';

INSERT INTO ordenes_servicios (orden_id, servicio_id, cantidad, precio_unitario, subtotal, fecha_creacion)
SELECT o.id, s.id, 1, s.precio, s.precio, CURRENT_TIMESTAMP
FROM ordenes_trabajo o, servicios s
WHERE o.numero_orden = 'ORD-M3N4O5P6' AND s.nombre = 'Alineación y Balanceo';

INSERT INTO ordenes_servicios (orden_id, servicio_id, cantidad, precio_unitario, subtotal, fecha_creacion)
SELECT o.id, s.id, 1, s.precio, s.precio, CURRENT_TIMESTAMP
FROM ordenes_trabajo o, servicios s
WHERE o.numero_orden = 'ORD-Q7R8S9T0' AND s.nombre = 'Escaneo Computarizado';

INSERT INTO ordenes_servicios (orden_id, servicio_id, cantidad, precio_unitario, subtotal, fecha_creacion)
SELECT o.id, s.id, 1, s.precio, s.precio, CURRENT_TIMESTAMP
FROM ordenes_trabajo o, servicios s
WHERE o.numero_orden = 'ORD-Q7R8S9T0' AND s.nombre = 'Cambio de Bujías';

INSERT INTO ordenes_servicios (orden_id, servicio_id, cantidad, precio_unitario, subtotal, fecha_creacion)
SELECT o.id, s.id, 1, s.precio, s.precio, CURRENT_TIMESTAMP
FROM ordenes_trabajo o, servicios s
WHERE o.numero_orden = 'ORD-U1V2W3X4' AND s.nombre = 'Cambio de Amortiguadores';

INSERT INTO ordenes_servicios (orden_id, servicio_id, cantidad, precio_unitario, subtotal, fecha_creacion)
SELECT o.id, s.id, 1, s.precio, s.precio, CURRENT_TIMESTAMP
FROM ordenes_trabajo o, servicios s
WHERE o.numero_orden = 'ORD-Y5Z6A7B8' AND s.nombre = 'Cambio de Aceite y Filtro';

INSERT INTO ordenes_servicios (orden_id, servicio_id, cantidad, precio_unitario, subtotal, fecha_creacion)
SELECT o.id, s.id, 1, s.precio, s.precio, CURRENT_TIMESTAMP
FROM ordenes_trabajo o, servicios s
WHERE o.numero_orden = 'ORD-C9D0E1F2' AND s.nombre = 'Revisión de Aire Acondicionado';

INSERT INTO ordenes_servicios (orden_id, servicio_id, cantidad, precio_unitario, subtotal, fecha_creacion)
SELECT o.id, s.id, 1, s.precio, s.precio, CURRENT_TIMESTAMP
FROM ordenes_trabajo o, servicios s
WHERE o.numero_orden = 'ORD-G3H4I5J6' AND s.nombre = 'Cambio de Aceite y Filtro';

INSERT INTO ordenes_servicios (orden_id, servicio_id, cantidad, precio_unitario, subtotal, fecha_creacion)
SELECT o.id, s.id, 1, s.precio, s.precio, CURRENT_TIMESTAMP
FROM ordenes_trabajo o, servicios s
WHERE o.numero_orden = 'ORD-K7L8M9N0' AND s.nombre = 'Cambio de Amortiguadores';

INSERT INTO ordenes_servicios (orden_id, servicio_id, cantidad, precio_unitario, subtotal, fecha_creacion)
SELECT o.id, s.id, 1, s.precio, s.precio, CURRENT_TIMESTAMP
FROM ordenes_trabajo o, servicios s
WHERE o.numero_orden = 'ORD-O1P2Q3R4' AND s.nombre = 'Revisión General Preventiva';

INSERT INTO ordenes_servicios (orden_id, servicio_id, cantidad, precio_unitario, subtotal, fecha_creacion)
SELECT o.id, s.id, 1, s.precio, s.precio, CURRENT_TIMESTAMP
FROM ordenes_trabajo o, servicios s
WHERE o.numero_orden = 'ORD-S5T6U7V8' AND s.nombre = 'Revisión General Preventiva';

INSERT INTO ordenes_servicios (orden_id, servicio_id, cantidad, precio_unitario, subtotal, fecha_creacion)
SELECT o.id, s.id, 1, s.precio, s.precio, CURRENT_TIMESTAMP
FROM ordenes_trabajo o, servicios s
WHERE o.numero_orden = 'ORD-W9X0Y1Z2' AND s.nombre = 'Limpieza de Inyectores';

INSERT INTO ordenes_servicios (orden_id, servicio_id, cantidad, precio_unitario, subtotal, fecha_creacion)
SELECT o.id, s.id, 1, s.precio, s.precio, CURRENT_TIMESTAMP
FROM ordenes_trabajo o, servicios s
WHERE o.numero_orden = 'ORD-W9X0Y1Z2' AND s.nombre = 'Cambio de Bujías';

INSERT INTO ordenes_servicios (orden_id, servicio_id, cantidad, precio_unitario, subtotal, fecha_creacion)
SELECT o.id, s.id, 1, s.precio, s.precio, CURRENT_TIMESTAMP
FROM ordenes_trabajo o, servicios s
WHERE o.numero_orden = 'ORD-A3B4C5D6' AND s.nombre = 'Rotación de Neumáticos';

INSERT INTO ordenes_servicios (orden_id, servicio_id, cantidad, precio_unitario, subtotal, fecha_creacion)
SELECT o.id, s.id, 1, s.precio, s.precio, CURRENT_TIMESTAMP
FROM ordenes_trabajo o, servicios s
WHERE o.numero_orden = 'ORD-E7F8G9H0' AND s.nombre = 'Revisión General Preventiva';

-- ============================================================
-- 10. DETALLE DE ORDENES - PRODUCTOS
-- ============================================================
INSERT INTO ordenes_productos (orden_id, producto_id, cantidad, precio_unitario, subtotal, fecha_creacion)
SELECT o.id, p.id, 1, p.precio_venta, p.precio_venta, CURRENT_TIMESTAMP
FROM ordenes_trabajo o, productos p
WHERE o.numero_orden = 'ORD-A1B2C3D4' AND p.codigo = 'ACE-001';

INSERT INTO ordenes_productos (orden_id, producto_id, cantidad, precio_unitario, subtotal, fecha_creacion)
SELECT o.id, p.id, 1, p.precio_venta, p.precio_venta, CURRENT_TIMESTAMP
FROM ordenes_trabajo o, productos p
WHERE o.numero_orden = 'ORD-A1B2C3D4' AND p.codigo = 'FIL-001';

INSERT INTO ordenes_productos (orden_id, producto_id, cantidad, precio_unitario, subtotal, fecha_creacion)
SELECT o.id, p.id, 1, p.precio_venta, p.precio_venta, CURRENT_TIMESTAMP
FROM ordenes_trabajo o, productos p
WHERE o.numero_orden = 'ORD-I9J0K1L2' AND p.codigo = 'PAS-001';

INSERT INTO ordenes_productos (orden_id, producto_id, cantidad, precio_unitario, subtotal, fecha_creacion)
SELECT o.id, p.id, 1, p.precio_venta, p.precio_venta, CURRENT_TIMESTAMP
FROM ordenes_trabajo o, productos p
WHERE o.numero_orden = 'ORD-I9J0K1L2' AND p.codigo = 'LUB-001';

INSERT INTO ordenes_productos (orden_id, producto_id, cantidad, precio_unitario, subtotal, fecha_creacion)
SELECT o.id, p.id, 4, p.precio_venta, p.precio_venta * 4, CURRENT_TIMESTAMP
FROM ordenes_trabajo o, productos p
WHERE o.numero_orden = 'ORD-Q7R8S9T0' AND p.codigo = 'ELE-002';

INSERT INTO ordenes_productos (orden_id, producto_id, cantidad, precio_unitario, subtotal, fecha_creacion)
SELECT o.id, p.id, 2, p.precio_venta, p.precio_venta * 2, CURRENT_TIMESTAMP
FROM ordenes_trabajo o, productos p
WHERE o.numero_orden = 'ORD-U1V2W3X4' AND p.codigo = 'SUS-001';

INSERT INTO ordenes_productos (orden_id, producto_id, cantidad, precio_unitario, subtotal, fecha_creacion)
SELECT o.id, p.id, 1, p.precio_venta, p.precio_venta, CURRENT_TIMESTAMP
FROM ordenes_trabajo o, productos p
WHERE o.numero_orden = 'ORD-Y5Z6A7B8' AND p.codigo = 'ACE-002';

INSERT INTO ordenes_productos (orden_id, producto_id, cantidad, precio_unitario, subtotal, fecha_creacion)
SELECT o.id, p.id, 1, p.precio_venta, p.precio_venta, CURRENT_TIMESTAMP
FROM ordenes_trabajo o, productos p
WHERE o.numero_orden = 'ORD-Y5Z6A7B8' AND p.codigo = 'FIL-001';

INSERT INTO ordenes_productos (orden_id, producto_id, cantidad, precio_unitario, subtotal, fecha_creacion)
SELECT o.id, p.id, 1, p.precio_venta, p.precio_venta, CURRENT_TIMESTAMP
FROM ordenes_trabajo o, productos p
WHERE o.numero_orden = 'ORD-G3H4I5J6' AND p.codigo = 'ACE-001';

INSERT INTO ordenes_productos (orden_id, producto_id, cantidad, precio_unitario, subtotal, fecha_creacion)
SELECT o.id, p.id, 1, p.precio_venta, p.precio_venta, CURRENT_TIMESTAMP
FROM ordenes_trabajo o, productos p
WHERE o.numero_orden = 'ORD-G3H4I5J6' AND p.codigo = 'FIL-001';

INSERT INTO ordenes_productos (orden_id, producto_id, cantidad, precio_unitario, subtotal, fecha_creacion)
SELECT o.id, p.id, 1, p.precio_venta, p.precio_venta, CURRENT_TIMESTAMP
FROM ordenes_trabajo o, productos p
WHERE o.numero_orden = 'ORD-G3H4I5J6' AND p.codigo = 'FIL-002';

INSERT INTO ordenes_productos (orden_id, producto_id, cantidad, precio_unitario, subtotal, fecha_creacion)
SELECT o.id, p.id, 1, p.precio_venta, p.precio_venta, CURRENT_TIMESTAMP
FROM ordenes_trabajo o, productos p
WHERE o.numero_orden = 'ORD-O1P2Q3R4' AND p.codigo = 'ELE-001';

INSERT INTO ordenes_productos (orden_id, producto_id, cantidad, precio_unitario, subtotal, fecha_creacion)
SELECT o.id, p.id, 1, p.precio_venta, p.precio_venta, CURRENT_TIMESTAMP
FROM ordenes_trabajo o, productos p
WHERE o.numero_orden = 'ORD-S5T6U7V8' AND p.codigo = 'ACE-001';

INSERT INTO ordenes_productos (orden_id, producto_id, cantidad, precio_unitario, subtotal, fecha_creacion)
SELECT o.id, p.id, 1, p.precio_venta, p.precio_venta, CURRENT_TIMESTAMP
FROM ordenes_trabajo o, productos p
WHERE o.numero_orden = 'ORD-S5T6U7V8' AND p.codigo = 'FIL-001';

INSERT INTO ordenes_productos (orden_id, producto_id, cantidad, precio_unitario, subtotal, fecha_creacion)
SELECT o.id, p.id, 4, p.precio_venta, p.precio_venta * 4, CURRENT_TIMESTAMP
FROM ordenes_trabajo o, productos p
WHERE o.numero_orden = 'ORD-W9X0Y1Z2' AND p.codigo = 'ELE-002';

INSERT INTO ordenes_productos (orden_id, producto_id, cantidad, precio_unitario, subtotal, fecha_creacion)
SELECT o.id, p.id, 1, p.precio_venta, p.precio_venta, CURRENT_TIMESTAMP
FROM ordenes_trabajo o, productos p
WHERE o.numero_orden = 'ORD-E7F8G9H0' AND p.codigo = 'REF-001';
