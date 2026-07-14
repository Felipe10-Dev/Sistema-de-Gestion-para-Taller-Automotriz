-- Roles
CREATE TABLE roles (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL UNIQUE,
    descripcion VARCHAR(200)
);

-- Usuarios
CREATE TABLE usuarios (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    apellido VARCHAR(100) NOT NULL,
    email VARCHAR(150),
    rol_id BIGINT NOT NULL REFERENCES roles(id),
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_modificacion TIMESTAMP,
    creado_por VARCHAR(50),
    modificado_por VARCHAR(50),
    activo BOOLEAN DEFAULT TRUE
);

-- Categorias
CREATE TABLE categorias (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL UNIQUE,
    descripcion VARCHAR(200),
    activo BOOLEAN DEFAULT TRUE
);

-- Servicios
CREATE TABLE servicios (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    descripcion VARCHAR(500),
    precio DECIMAL(10,2) NOT NULL,
    duracion_estimada_minutos INTEGER,
    categoria_id BIGINT REFERENCES categorias(id),
    activo BOOLEAN DEFAULT TRUE
);

-- Proveedores
CREATE TABLE proveedores (
    id BIGSERIAL PRIMARY KEY,
    nit VARCHAR(20) NOT NULL UNIQUE,
    nombre VARCHAR(150) NOT NULL,
    contacto VARCHAR(100),
    telefono VARCHAR(20),
    email VARCHAR(150),
    direccion VARCHAR(200),
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_modificacion TIMESTAMP,
    creado_por VARCHAR(50),
    modificado_por VARCHAR(50),
    activo BOOLEAN DEFAULT TRUE
);

-- Productos
CREATE TABLE productos (
    id BIGSERIAL PRIMARY KEY,
    codigo VARCHAR(50) NOT NULL UNIQUE,
    nombre VARCHAR(150) NOT NULL,
    descripcion VARCHAR(500),
    precio_compra DECIMAL(10,2),
    precio_venta DECIMAL(10,2) NOT NULL,
    stock_minimo INTEGER,
    categoria_id BIGINT REFERENCES categorias(id),
    proveedor_id BIGINT REFERENCES proveedores(id),
    activo BOOLEAN DEFAULT TRUE
);

-- Inventario
CREATE TABLE inventario (
    id BIGSERIAL PRIMARY KEY,
    producto_id BIGINT NOT NULL UNIQUE REFERENCES productos(id),
    cantidad_actual INTEGER NOT NULL DEFAULT 0,
    cantidad_minima INTEGER DEFAULT 0,
    ubicacion VARCHAR(100)
);

-- Movimientos de inventario
CREATE TABLE movimientos_inventario (
    id BIGSERIAL PRIMARY KEY,
    producto_id BIGINT NOT NULL REFERENCES productos(id),
    tipo_movimiento VARCHAR(20) NOT NULL,
    cantidad INTEGER NOT NULL,
    motivo VARCHAR(200),
    fecha_movimiento TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    usuario VARCHAR(50)
);

-- Clientes
CREATE TABLE clientes (
    id BIGSERIAL PRIMARY KEY,
    tipo_documento VARCHAR(20) NOT NULL,
    numero_documento VARCHAR(20) NOT NULL UNIQUE,
    nombre VARCHAR(100) NOT NULL,
    apellido VARCHAR(100) NOT NULL,
    telefono VARCHAR(20),
    email VARCHAR(150),
    direccion VARCHAR(200),
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_modificacion TIMESTAMP,
    creado_por VARCHAR(50),
    modificado_por VARCHAR(50),
    activo BOOLEAN DEFAULT TRUE
);

-- Vehiculos
CREATE TABLE vehiculos (
    id BIGSERIAL PRIMARY KEY,
    placa VARCHAR(10) NOT NULL UNIQUE,
    marca VARCHAR(50) NOT NULL,
    linea VARCHAR(50) NOT NULL,
    modelo VARCHAR(10) NOT NULL,
    color VARCHAR(30),
    cilindraje VARCHAR(10),
    tipo_vehiculo VARCHAR(30),
    cliente_id BIGINT NOT NULL REFERENCES clientes(id),
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_modificacion TIMESTAMP,
    creado_por VARCHAR(50),
    modificado_por VARCHAR(50),
    activo BOOLEAN DEFAULT TRUE
);

-- Ordenes de trabajo
CREATE TABLE ordenes_trabajo (
    id BIGSERIAL PRIMARY KEY,
    numero_orden VARCHAR(20) NOT NULL UNIQUE,
    cliente_id BIGINT NOT NULL REFERENCES clientes(id),
    vehiculo_id BIGINT NOT NULL REFERENCES vehiculos(id),
    kilometraje INTEGER,
    estado VARCHAR(30) NOT NULL DEFAULT 'PENDIENTE',
    fecha_ingreso TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_salida TIMESTAMP,
    tecnico_id BIGINT REFERENCES usuarios(id),
    observaciones VARCHAR(1000),
    total_servicios DECIMAL(10,2) DEFAULT 0,
    total_productos DECIMAL(10,2) DEFAULT 0,
    total_general DECIMAL(10,2) DEFAULT 0,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_modificacion TIMESTAMP,
    creado_por VARCHAR(50),
    modificado_por VARCHAR(50),
    activo BOOLEAN DEFAULT TRUE
);

-- Ordenes - Servicios (detalle)
CREATE TABLE ordenes_servicios (
    id BIGSERIAL PRIMARY KEY,
    orden_id BIGINT NOT NULL REFERENCES ordenes_trabajo(id),
    servicio_id BIGINT NOT NULL REFERENCES servicios(id),
    cantidad INTEGER NOT NULL DEFAULT 1,
    precio_unitario DECIMAL(10,2) NOT NULL,
    subtotal DECIMAL(10,2) NOT NULL,
    observaciones VARCHAR(500)
);

-- Ordenes - Productos (detalle)
CREATE TABLE ordenes_productos (
    id BIGSERIAL PRIMARY KEY,
    orden_id BIGINT NOT NULL REFERENCES ordenes_trabajo(id),
    producto_id BIGINT NOT NULL REFERENCES productos(id),
    cantidad INTEGER NOT NULL DEFAULT 1,
    precio_unitario DECIMAL(10,2) NOT NULL,
    subtotal DECIMAL(10,2) NOT NULL
);

-- Indexes
CREATE INDEX idx_usuarios_username ON usuarios(username);
CREATE INDEX idx_usuarios_rol ON usuarios(rol_id);
CREATE INDEX idx_clientes_documento ON clientes(numero_documento);
CREATE INDEX idx_vehiculos_placa ON vehiculos(placa);
CREATE INDEX idx_vehiculos_cliente ON vehiculos(cliente_id);
CREATE INDEX idx_ordenes_cliente ON ordenes_trabajo(cliente_id);
CREATE INDEX idx_ordenes_vehiculo ON ordenes_trabajo(vehiculo_id);
CREATE INDEX idx_ordenes_estado ON ordenes_trabajo(estado);
CREATE INDEX idx_ordenes_fecha ON ordenes_trabajo(fecha_ingreso);
CREATE INDEX idx_productos_codigo ON productos(codigo);
CREATE INDEX idx_productos_categoria ON productos(categoria_id);
CREATE INDEX idx_inventario_producto ON inventario(producto_id);
CREATE INDEX idx_movimientos_producto ON movimientos_inventario(producto_id);
