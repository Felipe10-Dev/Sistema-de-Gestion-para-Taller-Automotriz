-- Roles por defecto
INSERT INTO roles (nombre, descripcion) VALUES
('ADMIN', 'Administrador del sistema'),
('OPERADOR', 'Operador de serviteca'),
('TECNICO', 'Tecnico mecanico');

-- Usuario admin por defecto (password: admin123)
INSERT INTO usuarios (username, password, nombre, apellido, email, rol_id, activo)
SELECT 'admin', '$2a$10$TfpaK.gw9ba9D19S/cDI3O.FOMt5PlweDwGvDD5Eo/Xn0FRgpYyH.', 'Admin', 'Sistema', 'admin@serviteca.com', id, TRUE
FROM roles WHERE nombre = 'ADMIN';
