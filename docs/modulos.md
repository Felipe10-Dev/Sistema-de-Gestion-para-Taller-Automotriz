# Módulos del Sistema

## shared (Capa transversal)

**Objetivo**: Proporcionar clases base, utilidades y configuración compartida entre todos los módulos.

**Responsabilidades**:
- Formato unificado de respuestas (`ApiResponse<T>`).
- Paginación genérica (`PagedResponse<T>`).
- Entidad base con auditoría (`BaseEntity`).
- Manejo global de excepciones (`GlobalExceptionHandler`).
- Configuración de auditoría automática.

**Dependencias**: Ninguna (es la base del sistema).

**Clases principales**:
- `ApiResponse<T>` - Envoltura para todas las respuestas API.
- `PagedResponse<T>` - Envoltura para respuestas paginadas.
- `BaseEntity` - Clase base con id, fechas, auditoría y activo.
- `GlobalExceptionHandler` - Manejador global de excepciones.
- `ResourceNotFoundException`, `BadRequestException`, `DuplicateResourceException`.

---

## security

**Objetivo**: Gestionar la autenticación y autorización del sistema.

**Responsabilidades**:
- Generación y validación de tokens JWT.
- Filtro de autenticación para peticiones HTTP.
- Configuración de seguridad (CORS, CSRF, rutas públicas/protegidas).
- Integración con Spring Security.

**Dependencias**: shared, usuario (para UserDetailsService).

**Clases principales**:
- `JwtTokenProvider` - Genera y valida access/refresh tokens.
- `JwtAuthenticationFilter` - Filtro que extrae y valida el JWT en cada request.
- `SecurityConfig` - Configuración de Spring Security, CORS, rutas.

---

## auth

**Objetivo**: Manejar la autenticación de usuarios.

**Responsabilidades**:
- Login con username y password.
- Registro de nuevos usuarios (rol OPERADOR por defecto).
- Renovación de tokens mediante refresh token.

**Dependencias**: security, usuario, rol.

**Endpoints**:
- `POST /api/auth/login`
- `POST /api/auth/register`
- `POST /api/auth/refresh`

**DTOs**:
- `LoginRequest` - username + password.
- `LoginResponse` - accessToken, refreshToken, username, rol, nombre.
- `RegisterRequest` - username, password, nombre, apellido, email.
- `RefreshTokenRequest` - refreshToken.

---

## usuario

**Objetivo**: Administrar los usuarios del sistema.

**Responsabilidades**:
- CRUD completo de usuarios.
- Carga de usuarios para autenticación (UserDetailsService).
- Consulta del usuario autenticado (`/me`).

**Dependencias**: shared, rol.

**Entidades**: `Usuario` (extends BaseEntity) - username, password, nombre, apellido, email, rol.

**Endpoints**:
- `GET /api/usuarios` - Listar todos (solo ADMIN).
- `GET /api/usuarios/{id}` - Buscar por ID.
- `GET /api/usuarios/me` - Usuario actual.
- `POST /api/usuarios` - Crear usuario.
- `PUT /api/usuarios/{id}` - Actualizar usuario.
- `DELETE /api/usuarios/{id}` - Eliminar usuario.

**DTOs**:
- `UsuarioRequest` - Creación con password.
- `UsuarioUpdateRequest` - Actualización sin password.
- `UsuarioResponse` - Datos públicos del usuario.

---

## rol

**Objetivo**: Administrar los roles del sistema.

**Responsabilidades**:
- CRUD completo de roles.
- Roles por defecto: ADMIN, OPERADOR, TECNICO.

**Dependencias**: shared.

**Entidades**: `Rol` - id, nombre (único), descripcion.

**Endpoints**:
- `GET /api/roles`
- `GET /api/roles/{id}`
- `POST /api/roles`
- `PUT /api/roles/{id}`
- `DELETE /api/roles/{id}`

---

## cliente

**Objetivo**: Gestionar los clientes del taller.

**Responsabilidades**:
- CRUD completo de clientes.
- Búsqueda por nombre, apellido o documento.
- Paginación de resultados.
- Borrado lógico (desactivación).

**Dependencias**: shared.

**Entidades**: `Cliente` (extends BaseEntity) - tipoDocumento, numeroDocumento, nombre, apellido, telefono, email, direccion.

**Endpoints**:
- `GET /api/clientes` - Listar con paginación y búsqueda.
- `GET /api/clientes/{id}`
- `POST /api/clientes`
- `PUT /api/clientes/{id}`
- `DELETE /api/clientes/{id}`

---

## vehiculo

**Objetivo**: Gestionar los vehículos asociados a clientes.

**Responsabilidades**:
- CRUD completo de vehículos.
- Búsqueda por cliente.
- Validación de placa única.

**Dependencias**: shared, cliente.

**Entidades**: `Vehiculo` (extends BaseEntity) - placa, marca, linea, modelo, color, cilindraje, tipoVehiculo, cliente.

**Endpoints**:
- `GET /api/vehiculos` - Listar con paginación.
- `GET /api/vehiculos/{id}`
- `GET /api/vehiculos/cliente/{clienteId}` - Vehículos de un cliente.
- `POST /api/vehiculos`
- `PUT /api/vehiculos/{id}`
- `DELETE /api/vehiculos/{id}`

---

## servicio

**Objetivo**: Administrar el catálogo de servicios ofrecidos.

**Responsabilidades**:
- CRUD completo de servicios.
- Asociación con categorías.
- Búsqueda por nombre o descripción.

**Dependencias**: shared, categoria.

**Entidades**: `Servicio` - nombre, descripcion, precio, duracionEstimadaMinutos, categoria, activo.

**Endpoints**:
- `GET /api/servicios` - Listar con búsqueda opcional.
- `GET /api/servicios/{id}`
- `POST /api/servicios`
- `PUT /api/servicios/{id}`
- `DELETE /api/servicios/{id}`

---

## categoria

**Objetivo**: Administrar las categorías para clasificar servicios y productos.

**Responsabilidades**:
- CRUD completo de categorías.
- Las categorías son usadas por servicios y productos.

**Dependencias**: shared.

**Entidades**: `Categoria` - id, nombre (único), descripcion, activo.

**Endpoints**:
- `GET /api/categorias`
- `GET /api/categorias/{id}`
- `POST /api/categorias`
- `PUT /api/categorias/{id}`
- `DELETE /api/categorias/{id}`

---

## producto

**Objetivo**: Administrar el catálogo de productos e insumos.

**Responsabilidades**:
- CRUD completo de productos.
- Asociación con categorías y proveedores.
- Control de stock mínimo.

**Dependencias**: shared, categoria, proveedor.

**Entidades**: `Producto` - codigo, nombre, descripcion, precioCompra, precioVenta, stockMinimo, categoria, proveedor, activo.

**Endpoints**:
- `GET /api/productos` - Listar con búsqueda.
- `GET /api/productos/{id}`
- `POST /api/productos`
- `PUT /api/productos/{id}`
- `DELETE /api/productos/{id}`

---

## proveedor

**Objetivo**: Administrar los proveedores de productos.

**Responsabilidades**:
- CRUD completo de proveedores.
- Búsqueda por NIT, nombre o contacto.

**Dependencias**: shared.

**Entidades**: `Proveedor` (extends BaseEntity) - nit, nombre, contacto, telefono, email, direccion.

**Endpoints**:
- `GET /api/proveedores` - Listar con búsqueda.
- `GET /api/proveedores/{id}`
- `POST /api/proveedores`
- `PUT /api/proveedores/{id}`
- `DELETE /api/proveedores/{id}`

---

## inventario

**Objetivo**: Controlar el stock de productos y registrar movimientos.

**Responsabilidades**:
- Registro de inventario por producto.
- Control de stock mínimo (alerta bajo stock).
- Movimientos de entrada y salida.
- Trazabilidad completa de cambios de stock.

**Dependencias**: shared, producto.

**Entidades**:
- `Inventario` - producto (1:1), cantidadActual, cantidadMinima, ubicacion.
- `MovimientoInventario` - producto, tipoMovimiento, cantidad, motivo, fecha, usuario.

**Endpoints**:
- `GET /api/inventario` - Listar todo.
- `GET /api/inventario/{id}`
- `GET /api/inventario/producto/{productoId}`
- `GET /api/inventario/bajo-stock` - Alertas.
- `POST /api/inventario` - Crear registro.
- `PUT /api/inventario/{id}` - Actualizar configuración.
- `POST /api/inventario/movimiento` - Registrar entrada/salida.
- `GET /api/inventario/movimientos/{productoId}` - Historial.

---

## orden

**Objetivo**: Gestionar las órdenes de trabajo (núcleo del sistema).

**Responsabilidades**:
- Creación de órdenes con cliente, vehículo, servicios y productos.
- Máquina de estados (PENDIENTE → EN_PROCESO → COMPLETADA | CANCELADA).
- Cálculo automático de totales.
- Asignación de técnico.

**Dependencias**: shared, cliente, vehiculo, servicio, producto, usuario.

**Entidades**:
- `OrdenTrabajo` (extends BaseEntity) - numeroOrden, cliente, vehiculo, kilometraje, estado, fechaIngreso, fechaSalida, tecnico, totales.
- `OrdenServicio` - orden, servicio, cantidad, precioUnitario, subtotal.
- `OrdenProducto` - orden, producto, cantidad, precioUnitario, subtotal.

**Endpoints**:
- `GET /api/ordenes` - Listar con paginación, búsqueda y filtro por estado.
- `GET /api/ordenes/{id}` - Detalle completo con servicios y productos.
- `POST /api/ordenes` - Crear orden con items.
- `PATCH /api/ordenes/{id}/estado` - Cambiar estado.
- `DELETE /api/ordenes/{id}` - Borrado lógico.

---

## dashboard

**Objetivo**: Proporcionar indicadores resumidos del estado del sistema.

**Responsabilidades**:
- Contar clientes, vehículos, órdenes por estado, productos bajo stock.
- Mostrar información relevante para la toma de decisiones.

**Dependencias**: cliente, vehiculo, orden, inventario, usuario.

**DTOs**:
- `DashboardResponse` - totalClientes, totalVehiculos, ordenesPendientes, ordenesEnProceso, ordenesCompletadasHoy, productosBajoStock, totalUsuarios, ingresosHoy.

**Endpoints**:
- `GET /api/dashboard`
