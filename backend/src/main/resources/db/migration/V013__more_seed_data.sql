-- V013: Más datos de prueba para paginación visible
-- Nota: Incluye empresa_id y sede_id por la migración V011 multiempresa

-- ============================================================
-- 1. PROVEEDORES (+10, total: 15)
-- ============================================================
INSERT INTO proveedores (nit, nombre, contacto, telefono, email, direccion, fecha_creacion, activo, empresa_id) VALUES
('900123461-6', 'Neumáticos del Norte SAS', 'Luis Perez', '3016789012', 'luis@neumaticosnorte.com', 'Av 68 #120-30, Bogotá', CURRENT_TIMESTAMP, true, 1),
('900123462-7', 'Baterías Ultra Power SAS', 'Sofia Medina', '3027890123', 'sofia@bateriasultra.com', 'Cra 50 #85-10, Medellín', CURRENT_TIMESTAMP, true, 1),
('900123463-8', 'Suspensión Total SAS', 'Diego Rojas', '3038901234', 'diego@suspensiontotal.com', 'Cll 30 #18-45, Cali', CURRENT_TIMESTAMP, true, 1),
('900123464-9', 'Diagnóstico Automotriz SAS', 'Camila Ruiz', '3049012345', 'camila@diagnosticoauto.com', 'Av 80 #15-60, Barranquilla', CURRENT_TIMESTAMP, true, 1),
('900123465-0', 'Llantas y Rines SAS', 'Andres Castro', '3050123456', 'andres@llantasrines.com', 'Cra 45 #70-25, Bucaramanga', CURRENT_TIMESTAMP, true, 1),
('900123466-1', 'Aire Acondicionado Auto SAS', 'Valeria Mendez', '3061234567', 'valeria@aireauto.com', 'Cll 100 #20-50, Bogotá', CURRENT_TIMESTAMP, true, 1),
('900123467-2', 'Frenos Seguros SAS', 'Felipe Navarro', '3072345678', 'felipe@frenosseguros.com', 'Av 26 #40-15, Pereira', CURRENT_TIMESTAMP, true, 1),
('900123468-3', 'Motor Parts Center SAS', 'Gabriela Rios', '3083456789', 'gabriela@motorparts.com', 'Cra 15 #32-80, Ibagué', CURRENT_TIMESTAMP, true, 1),
('900123469-4', 'Electricidad Automotriz SAS', 'Sebastian Peña', '3094567890', 'sebastian@elecauto.com', 'Cll 60 #10-90, Manizales', CURRENT_TIMESTAMP, true, 1),
('900123470-5', 'Carrocería y Pintura SAS', 'Daniela Vega', '3105678901', 'daniela@carroceriapintura.com', 'Av 34 #55-12, Pasto', CURRENT_TIMESTAMP, true, 1);

-- ============================================================
-- 2. CATEGORIAS (+7, total: 12)
-- ============================================================
INSERT INTO categorias (nombre, descripcion, fecha_creacion, activo, empresa_id) VALUES
('Sistema de Refrigeración', 'Radiadores, termostatos, bombas de agua y refrigerantes', CURRENT_TIMESTAMP, true, 1),
('Motor y Componentes', 'Partes internas del motor, válvulas, pistones y juntas', CURRENT_TIMESTAMP, true, 1),
('Transmisión', 'Cajas de cambios, embragues y diferenciales', CURRENT_TIMESTAMP, true, 1),
('Sistema de Escape', 'Tubos de escape, silenciadores y catalizadores', CURRENT_TIMESTAMP, true, 1),
('Sistema de Combustible', 'Bombas, inyectores, tanques y líneas de combustible', CURRENT_TIMESTAMP, true, 1),
('Dirección Hidráulica', 'Bombas, cremalleras, mangueras y líquido hidráulico', CURRENT_TIMESTAMP, true, 1),
('Sistema Eléctrico Avanzado', 'Alternadores, motores de arranque, sensores y módulos', CURRENT_TIMESTAMP, true, 1);

-- ============================================================
-- 3. USUARIOS (+10, total: 11)
-- ============================================================
INSERT INTO usuarios (username, password, nombre, apellido, email, rol_id, activo, empresa_id, sede_id)
SELECT 'operador1', '$2a$10$TfpaK.gw9ba9D19S/cDI3O.FOMt5PlweDwGvDD5Eo/Xn0FRgpYyH.', 'Carlos', 'Mendoza', 'carlos.mendoza@serviteca.com', id, TRUE, 1, 1
FROM roles WHERE nombre = 'OPERADOR';

INSERT INTO usuarios (username, password, nombre, apellido, email, rol_id, activo, empresa_id, sede_id)
SELECT 'operador2', '$2a$10$TfpaK.gw9ba9D19S/cDI3O.FOMt5PlweDwGvDD5Eo/Xn0FRgpYyH.', 'Ana', 'Gutierrez', 'ana.gutierrez@serviteca.com', id, TRUE, 1, 1
FROM roles WHERE nombre = 'OPERADOR';

INSERT INTO usuarios (username, password, nombre, apellido, email, rol_id, activo, empresa_id, sede_id)
SELECT 'tecnico1', '$2a$10$TfpaK.gw9ba9D19S/cDI3O.FOMt5PlweDwGvDD5Eo/Xn0FRgpYyH.', 'Miguel', 'Torres', 'miguel.torres@serviteca.com', id, TRUE, 1, 1
FROM roles WHERE nombre = 'TECNICO';

INSERT INTO usuarios (username, password, nombre, apellido, email, rol_id, activo, empresa_id, sede_id)
SELECT 'tecnico2', '$2a$10$TfpaK.gw9ba9D19S/cDI3O.FOMt5PlweDwGvDD5Eo/Xn0FRgpYyH.', 'Laura', 'Herrera', 'laura.herrera@serviteca.com', id, TRUE, 1, 1
FROM roles WHERE nombre = 'TECNICO';

INSERT INTO usuarios (username, password, nombre, apellido, email, rol_id, activo, empresa_id, sede_id)
SELECT 'tecnico3', '$2a$10$TfpaK.gw9ba9D19S/cDI3O.FOMt5PlweDwGvDD5Eo/Xn0FRgpYyH.', 'Pedro', 'Ramos', 'pedro.ramos@serviteca.com', id, TRUE, 1, 1
FROM roles WHERE nombre = 'TECNICO';

INSERT INTO usuarios (username, password, nombre, apellido, email, rol_id, activo, empresa_id, sede_id)
SELECT 'tecnico4', '$2a$10$TfpaK.gw9ba9D19S/cDI3O.FOMt5PlweDwGvDD5Eo/Xn0FRgpYyH.', 'Sofia', 'Lopez', 'sofia.lopez@serviteca.com', id, TRUE, 1, 1
FROM roles WHERE nombre = 'TECNICO';

INSERT INTO usuarios (username, password, nombre, apellido, email, rol_id, activo, empresa_id, sede_id)
SELECT 'tecnico5', '$2a$10$TfpaK.gw9ba9D19S/cDI3O.FOMt5PlweDwGvDD5Eo/Xn0FRgpYyH.', 'Diego', 'Martinez', 'diego.martinez@serviteca.com', id, TRUE, 1, 1
FROM roles WHERE nombre = 'TECNICO';

INSERT INTO usuarios (username, password, nombre, apellido, email, rol_id, activo, empresa_id, sede_id)
SELECT 'tecnico6', '$2a$10$TfpaK.gw9ba9D19S/cDI3O.FOMt5PlweDwGvDD5Eo/Xn0FRgpYyH.', 'Camila', 'Reyes', 'camila.reyes@serviteca.com', id, TRUE, 1, 1
FROM roles WHERE nombre = 'TECNICO';

INSERT INTO usuarios (username, password, nombre, apellido, email, rol_id, activo, empresa_id, sede_id)
SELECT 'tecnico7', '$2a$10$TfpaK.gw9ba9D19S/cDI3O.FOMt5PlweDwGvDD5Eo/Xn0FRgpYyH.', 'Andres', 'Silva', 'andres.silva@serviteca.com', id, TRUE, 1, 1
FROM roles WHERE nombre = 'TECNICO';

INSERT INTO usuarios (username, password, nombre, apellido, email, rol_id, activo, empresa_id, sede_id)
SELECT 'tecnico8', '$2a$10$TfpaK.gw9ba9D19S/cDI3O.FOMt5PlweDwGvDD5Eo/Xn0FRgpYyH.', 'Valentina', 'Cruz', 'valentina.cruz@serviteca.com', id, TRUE, 1, 1
FROM roles WHERE nombre = 'TECNICO';

-- ============================================================
-- 4. SERVICIOS (+5, total: 15)
-- ============================================================
INSERT INTO servicios (nombre, descripcion, precio, duracion_estimada_minutos, categoria_id, fecha_creacion, activo, empresa_id) VALUES
('Cambio de Refrigerante', 'Vaciado y llenado de refrigerante nuevo. Incluye purga del sistema.', 90000, 40, 6, CURRENT_TIMESTAMP, true, 1),
('Sincronización de Motor', 'Ajuste de válvulas, cambio de bujías, cables y revisión de distribución.', 220000, 120, 7, CURRENT_TIMESTAMP, true, 1),
('Cambio de Embrague', 'Cambio de disco, plato y release. Incluye rectificado de volante.', 450000, 240, 8, CURRENT_TIMESTAMP, true, 1),
('Rectificación de Discos de Freno', 'Rectificado de discos delanteros o traseros en máquina.', 80000, 45, 3, CURRENT_TIMESTAMP, true, 1),
('Limpieza del Sistema de Combustible', 'Limpieza completa de tanque, líneas e inyectores con aditivo.', 120000, 60, 10, CURRENT_TIMESTAMP, true, 1);
