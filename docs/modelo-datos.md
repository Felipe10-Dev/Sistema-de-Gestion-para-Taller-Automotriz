# Modelo de Datos

## Diagrama Entidad-Relación

```
┌─────────────┐     ┌──────────────┐
│   roles     │     │  usuarios    │
├─────────────┤     ├──────────────┤
│ id (PK)     │◄────│ rol_id (FK)  │
│ nombre      │     │ id (PK)      │
│ descripcion │     │ username     │
└─────────────┘     │ password     │
                    │ nombre       │
┌─────────────┐     │ apellido     │
│  clientes   │     │ email        │
├─────────────┤     │ activo       │
│ id (PK)     │     │ (hereda de   │
│ tipo_doc    │     │  BaseEntity) │
│ num_doc(*)  │     └──────────────┘
│ nombre      │
│ apellido    │     ┌──────────────┐
│ telefono    │     │  vehiculos   │
│ email       │     ├──────────────┤
│ direccion   │◄────│ cliente_id   │
│ activo      │     │ id (PK)      │
└──────┬──────┘     │ placa(*)     │
       │            │ marca        │
       │            │ linea        │
       │            │ modelo       │
       │            │ color        │
       │            │ cilindraje   │
       │            │ tipo_vehiculo│
       │            │ activo       │
       │            └──────┬───────┘
       │                   │
       │    ┌──────────────┴──────────┐
       │    │   ordenes_trabajo       │
       │    ├─────────────────────────┤
       └────┤ cliente_id (FK)         │
            │ vehiculo_id (FK)        │
            │ id (PK)                 │
            │ numero_orden(*)         │
            │ kilometraje             │
            │ estado                  │
            │ fecha_ingreso           │
            │ fecha_salida            │
            │ tecnico_id (FK) ────────┼───► usuarios
            │ observaciones           │
            │ total_servicios         │
            │ total_productos         │
            │ total_general           │
            │ activo                  │
            └──────┬──────────────────┘
                   │
         ┌─────────┴─────────┐
         ▼                   ▼
┌─────────────────┐  ┌─────────────────┐
│ ordenes_servicios│  │ ordenes_productos│
├─────────────────┤  ├─────────────────┤
│ id (PK)         │  │ id (PK)         │
│ orden_id (FK)   │  │ orden_id (FK)   │
│ servicio_id (FK)│  │ producto_id (FK)│
│ cantidad        │  │ cantidad        │
│ precio_unitario │  │ precio_unitario │
│ subtotal        │  │ subtotal        │
│ observaciones   │  └────────┬────────┘
└────────┬────────┘           │
         │                    │
         ▼                    ▼
┌─────────────────┐  ┌─────────────────┐
│   servicios     │  │   productos     │
├─────────────────┤  ├─────────────────┤
│ id (PK)         │  │ id (PK)         │
│ nombre          │  │ codigo(*)       │
│ descripcion     │  │ nombre          │
│ precio          │  │ descripcion     │
│ duracion_est.   │  │ precio_compra   │
│ categoria_id(FK)│  │ precio_venta    │
│ activo          │  │ stock_minimo    │
└────────┬────────┘  │ categoria_id(FK)│
         │           │ proveedor_id(FK)│
         ▼           │ activo          │
┌─────────────────┐  └────────┬────────┘
│   categorias    │           │
├─────────────────┤           ▼
│ id (PK)         │  ┌─────────────────┐
│ nombre(*)       │  │   proveedores   │
│ descripcion     │  ├─────────────────┤
│ activo          │  │ id (PK)         │
└─────────────────┘  │ nit(*)           │
                     │ nombre          │
                     │ contacto        │
                     │ telefono        │
      ┌─────────────────────────┐       │ email           │
      │     inventario          │       │ direccion       │
      ├─────────────────────────┤       │ activo          │
      │ id (PK)                 │       └─────────────────┘
      │ producto_id (FK, UQ) ───┼───► productos
      │ cantidad_actual         │
      │ cantidad_minima         │
      │ ubicacion               │
      └─────────────────────────┘

      ┌─────────────────────────┐
      │ movimientos_inventario  │
      ├─────────────────────────┤
      │ id (PK)                 │
      │ producto_id (FK) ───────┼───► productos
      │ tipo_movimiento         │
      │ cantidad                │
      │ motivo                  │
      │ fecha_movimiento        │
      │ usuario                 │
      └─────────────────────────┘
```

## Descripción de tablas

### roles
Almacena los roles del sistema. Por defecto se crean ADMIN, OPERADOR y TECNICO.

| Columna | Tipo | Restricción |
|---------|------|-------------|
| id | BIGSERIAL | PK |
| nombre | VARCHAR(50) | NOT NULL, UNIQUE |
| descripcion | VARCHAR(200) | |
| fecha_creacion | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP |
| fecha_modificacion | TIMESTAMP | |
| creado_por | VARCHAR(50) | |
| modificado_por | VARCHAR(50) | |
| activo | BOOLEAN | DEFAULT TRUE |
| fecha_eliminacion | TIMESTAMP | |
| eliminado_por | VARCHAR(50) | |

### usuarios
Usuarios del sistema con credenciales y rol asociado.

| Columna | Tipo | Restricción |
|---------|------|-------------|
| id | BIGSERIAL | PK |
| username | VARCHAR(50) | NOT NULL, UNIQUE |
| password | VARCHAR(255) | NOT NULL (BCrypt) |
| nombre | VARCHAR(100) | NOT NULL |
| apellido | VARCHAR(100) | NOT NULL |
| email | VARCHAR(150) | |
| rol_id | BIGINT | FK → roles.id, NOT NULL |
| activo | BOOLEAN | DEFAULT TRUE |

**Índices**: username, rol_id

### clientes
Personas naturales o jurídicas que llevan sus vehículos al taller.

| Columna | Tipo | Restricción |
|---------|------|-------------|
| id | BIGSERIAL | PK |
| tipo_documento | VARCHAR(20) | NOT NULL (CC, NIT, CE) |
| numero_documento | VARCHAR(20) | NOT NULL, UNIQUE |
| nombre | VARCHAR(100) | NOT NULL |
| apellido | VARCHAR(100) | NOT NULL |
| telefono | VARCHAR(20) | |
| email | VARCHAR(150) | |
| direccion | VARCHAR(200) | |
| activo | BOOLEAN | DEFAULT TRUE |

**Índices**: numero_documento

### vehiculos
Vehículos asociados a un cliente.

| Columna | Tipo | Restricción |
|---------|------|-------------|
| id | BIGSERIAL | PK |
| placa | VARCHAR(10) | NOT NULL, UNIQUE |
| marca | VARCHAR(50) | NOT NULL |
| linea | VARCHAR(50) | NOT NULL |
| modelo | VARCHAR(10) | NOT NULL |
| color | VARCHAR(30) | |
| cilindraje | VARCHAR(10) | |
| tipo_vehiculo | VARCHAR(30) | |
| cliente_id | BIGINT | FK → clientes.id, NOT NULL |
| activo | BOOLEAN | DEFAULT TRUE |

**Índices**: placa, cliente_id

### categorias
Clasificación de servicios y productos.

| Columna | Tipo | Restricción |
|---------|------|-------------|
| id | BIGSERIAL | PK |
| nombre | VARCHAR(100) | NOT NULL, UNIQUE |
| descripcion | VARCHAR(200) | |
| fecha_creacion | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP |
| fecha_modificacion | TIMESTAMP | |
| creado_por | VARCHAR(50) | |
| modificado_por | VARCHAR(50) | |
| activo | BOOLEAN | DEFAULT TRUE |
| fecha_eliminacion | TIMESTAMP | |
| eliminado_por | VARCHAR(50) | |

### servicios
Servicios ofrecidos por el taller (mano de obra).

| Columna | Tipo | Restricción |
|---------|------|-------------|
| id | BIGSERIAL | PK |
| nombre | VARCHAR(100) | NOT NULL |
| descripcion | VARCHAR(500) | |
| precio | DECIMAL(10,2) | NOT NULL |
| duracion_estimada_minutos | INTEGER | |
| categoria_id | BIGINT | FK → categorias.id |
| fecha_creacion | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP |
| fecha_modificacion | TIMESTAMP | |
| creado_por | VARCHAR(50) | |
| modificado_por | VARCHAR(50) | |
| activo | BOOLEAN | DEFAULT TRUE |
| fecha_eliminacion | TIMESTAMP | |
| eliminado_por | VARCHAR(50) | |

### proveedores
Empresas que suministran productos al taller.

| Columna | Tipo | Restricción |
|---------|------|-------------|
| id | BIGSERIAL | PK |
| nit | VARCHAR(20) | NOT NULL, UNIQUE |
| nombre | VARCHAR(150) | NOT NULL |
| contacto | VARCHAR(100) | |
| telefono | VARCHAR(20) | |
| email | VARCHAR(150) | |
| direccion | VARCHAR(200) | |
| activo | BOOLEAN | DEFAULT TRUE |

### productos
Productos e insumos utilizados en el taller, con relación a categoría y proveedor.

| Columna | Tipo | Restricción |
|---------|------|-------------|
| id | BIGSERIAL | PK |
| codigo | VARCHAR(50) | NOT NULL, UNIQUE |
| nombre | VARCHAR(150) | NOT NULL |
| descripcion | VARCHAR(500) | |
| precio_compra | DECIMAL(10,2) | |
| precio_venta | DECIMAL(10,2) | NOT NULL |
| stock_minimo | INTEGER | |
| categoria_id | BIGINT | FK → categorias.id |
| proveedor_id | BIGINT | FK → proveedores.id |
| activo | BOOLEAN | DEFAULT TRUE |

**Índices**: codigo, categoria_id

### inventario
Stock actual de cada producto (relación 1:1 con productos).

| Columna | Tipo | Restricción |
|---------|------|-------------|
| id | BIGSERIAL | PK |
| producto_id | BIGINT | FK → productos.id, UNIQUE, NOT NULL |
| cantidad_actual | INTEGER | NOT NULL, DEFAULT 0 |
| cantidad_minima | INTEGER | DEFAULT 0 |
| ubicacion | VARCHAR(100) | |
| fecha_creacion | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP |
| fecha_modificacion | TIMESTAMP | |
| creado_por | VARCHAR(50) | |
| modificado_por | VARCHAR(50) | |
| activo | BOOLEAN | DEFAULT TRUE |

**Índices**: producto_id

### movimientos_inventario
Trazabilidad de cambios en el inventario (entradas y salidas).

| Columna | Tipo | Restricción |
|---------|------|-------------|
| id | BIGSERIAL | PK |
| producto_id | BIGINT | FK → productos.id |
| tipo_movimiento | VARCHAR(20) | NOT NULL (ENTRADA/SALIDA) |
| cantidad | INTEGER | NOT NULL |
| motivo | VARCHAR(200) | |
| fecha_movimiento | TIMESTAMP | NOT NULL, DEFAULT NOW() |
| usuario | VARCHAR(50) | |
| fecha_creacion | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP |
| fecha_modificacion | TIMESTAMP | |
| creado_por | VARCHAR(50) | |
| modificado_por | VARCHAR(50) | |
| activo | BOOLEAN | DEFAULT TRUE |

**Índices**: producto_id, fecha_movimiento

### ordenes_trabajo
Núcleo del sistema. Representa una orden de servicio para un vehículo de un cliente.

| Columna | Tipo | Restricción |
|---------|------|-------------|
| id | BIGSERIAL | PK |
| numero_orden | VARCHAR(20) | NOT NULL, UNIQUE |
| cliente_id | BIGINT | FK → clientes.id, NOT NULL |
| vehiculo_id | BIGINT | FK → vehiculos.id, NOT NULL |
| kilometraje | INTEGER | |
| estado | VARCHAR(30) | NOT NULL, DEFAULT 'PENDIENTE' |
| fecha_ingreso | TIMESTAMP | NOT NULL, DEFAULT NOW() |
| fecha_salida | TIMESTAMP | |
| tecnico_id | BIGINT | FK → usuarios.id |
| observaciones | VARCHAR(1000) | |
| total_servicios | DECIMAL(10,2) | DEFAULT 0 |
| total_productos | DECIMAL(10,2) | DEFAULT 0 |
| total_general | DECIMAL(10,2) | DEFAULT 0 |
| activo | BOOLEAN | DEFAULT TRUE |

**Índices**: cliente_id, vehiculo_id, estado, fecha_ingreso

### ordenes_servicios
Detalle de servicios realizados en una orden.

| Columna | Tipo | Restricción |
|---------|------|-------------|
| id | BIGSERIAL | PK |
| orden_id | BIGINT | FK → ordenes_trabajo.id, NOT NULL |
| servicio_id | BIGINT | FK → servicios.id, NOT NULL |
| cantidad | INTEGER | NOT NULL, DEFAULT 1 |
| precio_unitario | DECIMAL(10,2) | NOT NULL |
| subtotal | DECIMAL(10,2) | NOT NULL |
| observaciones | VARCHAR(500) | |
| fecha_creacion | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP |
| fecha_modificacion | TIMESTAMP | |
| creado_por | VARCHAR(50) | |
| modificado_por | VARCHAR(50) | |
| activo | BOOLEAN | DEFAULT TRUE |

**Índices**: orden_id

### ordenes_productos
Detalle de productos utilizados en una orden.

| Columna | Tipo | Restricción |
|---------|------|-------------|
| id | BIGSERIAL | PK |
| orden_id | BIGINT | FK → ordenes_trabajo.id, NOT NULL |
| producto_id | BIGINT | FK → productos.id, NOT NULL |
| cantidad | INTEGER | NOT NULL, DEFAULT 1 |
| precio_unitario | DECIMAL(10,2) | NOT NULL |
| subtotal | DECIMAL(10,2) | NOT NULL |
| fecha_creacion | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP |
| fecha_modificacion | TIMESTAMP | |
| creado_por | VARCHAR(50) | |
| modificado_por | VARCHAR(50) | |
| activo | BOOLEAN | DEFAULT TRUE |

**Índices**: orden_id

## Cardinalidades

| Relación | Tipo |
|----------|------|
| roles → usuarios | 1 → N |
| clientes → vehiculos | 1 → N |
| clientes → ordenes_trabajo | 1 → N |
| vehiculos → ordenes_trabajo | 1 → N |
| usuarios → ordenes_trabajo (técnico) | 1 → N |
| categorias → servicios | 1 → N |
| categorias → productos | 1 → N |
| proveedores → productos | 1 → N |
| productos → inventario | 1 → 1 |
| productos → movimientos_inventario | 1 → N |
| ordenes_trabajo → ordenes_servicios | 1 → N |
| ordenes_trabajo → ordenes_productos | 1 → N |
| servicios → ordenes_servicios | 1 → N |
| productos → ordenes_productos | 1 → N |

## Restricciones generales

- Todas las tablas usan borrado lógico mediante `activo` (BOOLEAN DEFAULT TRUE).
- Los campos `fecha_creacion`, `fecha_modificacion`, `creado_por`, `modificado_por` se gestionan automáticamente por Spring Data Auditing (todas las entidades extienden `BaseEntity`).
- Los campos `fecha_eliminacion` y `eliminado_por` se usan para trazabilidad de eliminaciones.
- Los valores monetarios usan `DECIMAL(10,2)`.
- Los estados de orden están controlados: PENDIENTE, EN_PROCESO, COMPLETADA, CANCELADA.
- Los movimientos de inventario solo permiten ENTRADA o SALIDA.
- `MovimientoInventario` conserva `fecha_movimiento` y `usuario` como campos de negocio (independientes de los campos de auditoría heredados de `BaseEntity`).
- `OrdenServicio` y `OrdenProducto` copian el precio al momento de creación de la orden (preservación de históricos).
