# API REST

## Formato de respuestas

Todas las respuestas siguen el formato `ApiResponse<T>`:

```json
{
  "success": true,
  "message": "Operación exitosa",
  "data": { ... },
  "timestamp": "2026-06-09T12:00:00"
}
```

En caso de error:

```json
{
  "success": false,
  "message": "Mensaje de error",
  "status": 400,
  "error": "Bad Request",
  "path": "/api/clientes",
  "timestamp": "2026-06-09T12:00:00"
}
```

Para listas paginadas:

```json
{
  "success": true,
  "message": "Operación exitosa",
  "data": {
    "content": [ ... ],
    "page": 0,
    "size": 10,
    "totalElements": 100,
    "totalPages": 10,
    "last": false
  },
  "timestamp": "2026-06-09T12:00:00"
}
```

---

## Autenticación

### `POST /api/auth/login`

Inicia sesión y obtiene tokens JWT. **Público**.

**Request**:
```json
{
  "username": "admin",
  "password": "admin123"
}
```

**Response 200**:
```json
{
  "success": true,
  "message": "Inicio de sesión exitoso",
  "data": {
    "accessToken": "eyJhbG...",
    "refreshToken": "eyJhbG...",
    "tokenType": "Bearer",
    "username": "admin",
    "rol": "ADMIN",
    "nombre": "Admin Sistema"
  }
}
```

**Response 401**: Credenciales inválidas.

---

### `POST /api/auth/register`

Registra un nuevo usuario (rol OPERADOR por defecto). **Público**.

**Request**:
```json
{
  "username": "operador1",
  "password": "123456",
  "nombre": "Carlos",
  "apellido": "Méndez",
  "email": "carlos@serviteca.com"
}
```

**Response 201**: Misma estructura que login.

---

### `POST /api/auth/refresh`

Renueva el access token usando el refresh token. **Público**.

**Request**:
```json
{
  "refreshToken": "eyJhbG..."
}
```

**Response 200**: Nuevos accessToken y refreshToken.

---

## Health Check

### `GET /actuator/health`

**Público**. Retorna el estado del backend y sus componentes.

**Response 200**:
```json
{
  "status": "UP",
  "components": {
    "db": { "status": "UP" },
    "diskSpace": { "status": "UP" },
    "ping": { "status": "UP" }
  }
}
```

En producción (`application-prod.yml`) retorna solo `{"status":"UP"}` sin detalles.

---

## Usuarios (solo ADMIN)

### `GET /api/usuarios`

Lista todos los usuarios.

**Auth**: Requiere rol ADMIN.

**Response 200**:
```json
{
  "success": true,
  "data": [
    {
      "id": 1,
      "username": "admin",
      "nombre": "Admin",
      "apellido": "Sistema",
      "email": "admin@serviteca.com",
      "rol": "ADMIN",
      "activo": true
    }
  ]
}
```

### `GET /api/usuarios/{id}`

Obtiene un usuario por ID.

### `GET /api/usuarios/me`

Obtiene el usuario autenticado.

**Auth**: Requiere autenticación.

### `POST /api/usuarios`

Crea un nuevo usuario.

**Request**:
```json
{
  "username": "nuevo",
  "password": "123456",
  "nombre": "Juan",
  "apellido": "Pérez",
  "email": "juan@serviteca.com",
  "rolId": 1,
  "activo": true
}
```

### `PUT /api/usuarios/{id}`

Actualiza un usuario (sin password).

### `DELETE /api/usuarios/{id}`

Elimina un usuario.

---

## Roles

### `GET /api/roles`
### `GET /api/roles/{id}`
### `POST /api/roles`
### `PUT /api/roles/{id}`
### `DELETE /api/roles/{id}`

**Auth**: Requiere rol ADMIN.

---

## Clientes

### `GET /api/clientes`

Lista clientes activos con paginación y búsqueda.

**Query params**: `page` (0), `size` (10), `sort` (nombre), `search` (opcional).

### `GET /api/clientes/{id}`

### `POST /api/clientes`

**Request**:
```json
{
  "tipoDocumento": "CC",
  "numeroDocumento": "1234567890",
  "nombre": "Pedro",
  "apellido": "Gómez",
  "telefono": "3001234567",
  "email": "pedro@email.com",
  "direccion": "Cra 1 #2-3"
}
```

### `PUT /api/clientes/{id}`
### `DELETE /api/clientes/{id}`

---

## Vehículos

### `GET /api/vehiculos`

**Query params**: `page`, `size`, `sort`, `search`.

### `GET /api/vehiculos/{id}`
### `GET /api/vehiculos/cliente/{clienteId}`

Lista vehículos de un cliente específico.

### `POST /api/vehiculos`

**Request**:
```json
{
  "placa": "ABC123",
  "marca": "Toyota",
  "linea": "Corolla",
  "modelo": "2020",
  "color": "Rojo",
  "cilindraje": "1800",
  "tipoVehiculo": "Sedán",
  "clienteId": 1
}
```

### `PUT /api/vehiculos/{id}`
### `DELETE /api/vehiculos/{id}`

### `GET /api/vehiculos/{id}/historial`

Historial completo de órdenes del vehículo, ordenado descendente.

**Response**: Lista de items con fecha, número de orden, servicios, productos, total, técnico, observaciones.

### `GET /api/vehiculos/{id}/linea-tiempo`

Línea de tiempo del vehículo con eventos ordenados cronológicamente.

### `GET /api/vehiculos/{id}/estadisticas`

Estadísticas del vehículo: visitas, total invertido, última visita, promedio días, servicio y producto más frecuentes.

### `GET /api/vehiculos/{id}/mantenimientos`

Lista recomendaciones de mantenimiento preventivo del vehículo.

### `POST /api/vehiculos/{id}/mantenimientos`

Crea una recomendación de mantenimiento.

**Request**:
```json
{
  "tipo": "ACEITE",
  "descripcion": "Cambio de aceite cada 5000 km o 6 meses",
  "tipoProgramacion": "AMBOS",
  "intervaloKilometraje": 5000,
  "intervaloDias": 180
}
```

### `PUT /api/vehiculos/mantenimientos/{recomendacionId}`

Actualiza una recomendación.

### `DELETE /api/vehiculos/mantenimientos/{recomendacionId}`

Desactiva una recomendación (soft delete).

### `GET /api/vehiculos/{id}/proximos-mantenimientos`

Calcula próximos mantenimientos basados en la última orden y las recomendaciones activas. Incluye alertas (NORMAL/PRONTO/PROXIMO).

---

## Servicios

### `GET /api/servicios`

**Query params**: `search` (opcional).

**Response 200**:
```json
{
  "success": true,
  "data": [
    {
      "id": 1,
      "nombre": "Cambio de aceite",
      "descripcion": "Cambio de aceite y filtro",
      "precio": 120000.00,
      "duracionEstimadaMinutos": 30,
      "categoriaId": 1,
      "categoriaNombre": "Mantenimiento",
      "activo": true
    }
  ]
}
```

### `GET /api/servicios/{id}`
### `POST /api/servicios`
### `PUT /api/servicios/{id}`
### `DELETE /api/servicios/{id}`

---

## Categorías

### `GET /api/categorias`
### `GET /api/categorias/{id}`
### `POST /api/categorias`
### `PUT /api/categorias/{id}`
### `DELETE /api/categorias/{id}`

---

## Productos

### `GET /api/productos`

**Query params**: `search` (opcional).

### `GET /api/productos/{id}`
### `POST /api/productos`
### `PUT /api/productos/{id}`
### `DELETE /api/productos/{id}`

---

## Proveedores

### `GET /api/proveedores`

**Query params**: `search` (opcional).

### `GET /api/proveedores/{id}`
### `POST /api/proveedores`
### `PUT /api/proveedores/{id}`
### `DELETE /api/proveedores/{id}`

---

## Inventario

### `GET /api/inventario`
### `GET /api/inventario/{id}`
### `GET /api/inventario/producto/{productoId}`
### `GET /api/inventario/bajo-stock`

Lista productos con stock por debajo del mínimo.

### `POST /api/inventario`

**Request**:
```json
{
  "productoId": 1,
  "cantidadMinima": 5,
  "ubicacion": "Estante A3"
}
```

### `PUT /api/inventario/{id}`

### `POST /api/inventario/movimiento`

**Request**:
```json
{
  "productoId": 1,
  "tipoMovimiento": "ENTRADA",
  "cantidad": 10,
  "motivo": "Compra a proveedor"
}
```

### `GET /api/inventario/movimientos/{productoId}`

Historial de movimientos de un producto.

---

## Órdenes de Trabajo

**Auth**: Requiere autenticación (excepto consulta para ENTREGADO).

**Estados disponibles**: `PENDIENTE`, `EN_DIAGNOSTICO`, `EN_PROCESO`, `ESPERANDO_REPUESTOS`, `LISTO_PARA_ENTREGA`, `ENTREGADO`, `CANCELADO`.

**Transiciones válidas**: PENDIENTE → EN_DIAGNOSTICO → EN_PROCESO → LISTO_PARA_ENTREGA → ENTREGADO. ESPERANDO_REPUESTOS puede ir a EN_PROCESO. Cualquier estado no terminal puede ir a CANCELADO.

### `GET /api/ordenes`

**Query params**: `page`, `size`, `sort` (fechaIngreso), `search`, `estado`.

**Response 200**:
```json
{
  "success": true,
  "data": {
    "content": [
      {
        "id": 1,
        "numeroOrden": "ORD-XXXXXXXX",
        "clienteId": 1,
        "clienteNombre": "Pedro Gómez",
        "clienteDocumento": "1234567890",
        "vehiculoId": 1,
        "vehiculoPlaca": "ABC123",
        "vehiculoMarca": "Toyota",
        "vehiculoLinea": "Corolla",
        "kilometraje": 50000,
        "estado": "PENDIENTE",
        "fechaIngreso": "2026-06-09T10:00:00",
        "fechaSalida": null,
        "tecnicoId": 2,
        "tecnicoNombre": "Carlos Méndez",
        "observaciones": "Cliente reporta ruido extraño",
        "totalServicios": 240000.00,
        "totalProductos": 80000.00,
        "totalGeneral": 320000.00,
        "servicios": [
          {
            "id": 1,
            "servicioId": 1,
            "servicioNombre": "Cambio de aceite",
            "cantidad": 1,
            "precioUnitario": 120000.00,
            "subtotal": 120000.00,
            "observaciones": null
          }
        ],
        "productos": [
          {
            "id": 1,
            "productoId": 1,
            "productoNombre": "Aceite 20W50",
            "productoCodigo": "ACE-001",
            "cantidad": 2,
            "precioUnitario": 40000.00,
            "subtotal": 80000.00
          }
        ]
      }
    ],
    "page": 0,
    "size": 10,
    "totalElements": 1,
    "totalPages": 1,
    "last": true
  }
}
```

### `GET /api/ordenes/{id}`

Detalle completo de una orden. Incluye:
- Datos de cliente, vehículo, técnico
- Servicios y productos
- **Historial de cambios de estado** (`historial`: estado_anterior, estado_nuevo, usuario, fecha)
- **Bitácora de observaciones** (`observacionesList`: usuario, fecha, comentario)

### `POST /api/ordenes`

Crea una orden en estado **PENDIENTE**.

**Request**:
```json
{
  "clienteId": 1,
  "vehiculoId": 1,
  "kilometraje": 50000,
  "tecnicoId": 2,
  "observaciones": "Cliente reporta ruido",
  "servicios": [
    { "servicioId": 1, "cantidad": 1, "observaciones": "" },
    { "servicioId": 2, "cantidad": 1, "observaciones": "Revisar frenos traseros" }
  ],
  "productos": [
    { "productoId": 1, "cantidad": 2 },
    { "productoId": 3, "cantidad": 1 }
  ]
}
```

### `PUT /api/ordenes/{id}`

Actualiza datos de la orden. Reemplaza servicios y productos si se envían. Recalcula totales.

**Bloqueado si estado ENTREGADO o CANCELADO**.

**Request**: Misma estructura que POST.

### `PATCH /api/ordenes/{id}/estado`

Cambia estado validando la máquina de estados. Si el nuevo estado es ENTREGADO, registra fecha de salida y descuenta inventario. Si es LISTO_PARA_ENTREGA, valida que tenga cliente, vehículo y al menos un servicio o producto.

**Request**:
```json
{
  "estado": "EN_PROCESO",
  "observaciones": "Iniciando revisión"
}
```

### `POST /api/ordenes/{id}/observaciones`

Agrega una observación interna (bitácora). Inmutable. No requiere estado específico.

**Request**:
```json
{
  "comentario": "Cliente llamó preguntando estado"
}
```

### `DELETE /api/ordenes/{id}`

Eliminación lógica (soft delete). **Bloqueado si estado ENTREGADO o CANCELADO**.

---

## Caja

### `GET /api/caja/actual`

Obtiene la caja abierta del usuario autenticado.

**Auth**: Requiere autenticación.

**Response 200** (caja abierta):
```json
{
  "success": true,
  "data": {
    "id": 1,
    "usuario": "admin",
    "estado": "ABIERTA",
    "fechaApertura": "2026-06-10T07:00:00",
    "fechaCierre": null,
    "montoInicial": 50000.00,
    "totalIngresos": 0,
    "totalEsperado": 50000.00,
    "montoContado": null,
    "diferencia": null,
    "observaciones": "Apertura diaria"
  }
}
```

**Response 404**: No hay caja abierta.

### `GET /api/caja/historial`

Historial de cierres del usuario autenticado.

### `POST /api/caja/apertura`

Abre una nueva caja. No permite doble apertura.

**Request**:
```json
{
  "montoInicial": 50000,
  "observacion": "Apertura diaria"
}
```

**Response 201**:
```json
{
  "success": true,
  "message": "Caja abierta exitosamente",
  "data": {
    "id": 1,
    "usuario": "admin",
    "estado": "ABIERTA",
    "fechaApertura": "2026-06-10T07:00:00",
    "montoInicial": 50000.00,
    "totalIngresos": 0,
    "totalEsperado": 50000.00,
    "observaciones": "Apertura diaria"
  }
}
```

**Validation**: Si el usuario ya tiene caja abierta → `400: "El usuario ya tiene una caja abierta"`.

### `POST /api/caja/{id}/cierre`

Cierra la caja y calcula automáticamente total ingresos, total esperado y diferencia.

**Request**:
```json
{
  "montoContado": 550000,
  "observaciones": "Cierre diario"
}
```

**Response 200**:
```json
{
  "success": true,
  "data": {
    "id": 1,
    "estado": "CERRADA",
    "fechaApertura": "2026-06-10T07:00:00",
    "fechaCierre": "2026-06-10T18:00:00",
    "montoInicial": 50000.00,
    "totalIngresos": 500000.00,
    "totalEsperado": 550000.00,
    "montoContado": 550000.00,
    "diferencia": 0.00
  }
}
```

### `GET /api/caja/movimientos`

Lista movimientos financieros del usuario autenticado.

---

## Pagos

### `GET /api/pagos`

Lista todos los pagos registrados.

### `GET /api/pagos/orden/{ordenId}`

Lista los pagos de una orden específica.

### `GET /api/pagos/orden/{ordenId}/saldo`

Obtiene el saldo pendiente de una orden.

**Response 200**: `250000.00`

### `POST /api/pagos`

Registra un pago en una orden de trabajo. Actualiza el estado financiero automáticamente.

**Request**:
```json
{
  "ordenId": 1,
  "metodoPagoId": 1,
  "valor": 250000.00,
  "observacion": "Pago completo"
}
```

**Response 201**:
```json
{
  "success": true,
  "message": "Pago registrado exitosamente",
  "data": {
    "id": 1,
    "ordenId": 1,
    "ordenNumero": "ORD-ABCD1234",
    "usuario": "admin",
    "fecha": "2026-06-10T12:00:00",
    "metodoPagoId": 1,
    "metodoPagoNombre": "Efectivo",
    "valor": 250000.00
  }
}
```

**Validations**: `valor > 0`, no excede saldo pendiente, método de pago activo.

### `POST /api/pagos/{id}/anular`

Anula un pago. Lo marca como anulado (no lo elimina). Recalcula estado financiero.

---

## Métodos de Pago

### `GET /api/metodos-pago`

Lista métodos de pago activos.

**Response 200**:
```json
{
  "success": true,
  "data": [
    { "id": 1, "nombre": "Efectivo", "activo": true },
    { "id": 2, "nombre": "Tarjeta Débito", "activo": true },
    { "id": 3, "nombre": "Tarjeta Crédito", "activo": true },
    { "id": 4, "nombre": "Transferencia", "activo": true },
    { "id": 5, "nombre": "Nequi", "activo": true },
    { "id": 6, "nombre": "Daviplata", "activo": true },
    { "id": 7, "nombre": "Otro", "activo": true }
  ]
}
```

### `POST /api/metodos-pago`

Crea un nuevo método de pago. **Requiere rol ADMIN**.

### `PUT /api/metodos-pago/{id}`

Actualiza el nombre de un método. **Requiere rol ADMIN**.

### `PATCH /api/metodos-pago/{id}/toggle`

Activa/desactiva un método. **Requiere rol ADMIN**.

---

## CRM - Customer Relationship Management

### `GET /api/crm/clientes/{id}/perfil`

Perfil inteligente del cliente con métricas calculadas automáticamente y clasificación (NUEVO/FRECUENTE/VIP/INACTIVO).

**Response 200**: Datos del cliente + `fechaRegistro`, `primeraVisita`, `ultimaVisita`, `totalOrdenes`, `totalVehiculos`, `totalGastado`, `promedioPorOrden`, `clasificacion`, `notas`.

### `GET /api/crm/clientes/{id}/economico`

Historial económico del cliente.

**Response 200**: `totalInvertido`, `cantidadOrdenes`, `promedioPorOrden`, `mayorCompra`, `ultimaCompra`, `totalPendientePago`, `totalCancelado`.

### `GET /api/crm/clientes/{id}/ordenes`

Historial completo de órdenes del cliente, ordenado descendente por fecha.

**Response 200**: Lista con fecha, vehículo, número de orden, estado, estado financiero, total, servicios y productos.

### `GET /api/crm/clientes/{id}/notas`

Lista las notas internas del cliente, ordenadas por fecha descendente.

### `POST /api/crm/clientes/{id}/notas`

Agrega una nota interna. Registra usuario y fecha automáticamente. Las notas son inmutables.

**Request**:
```json
{
  "comentario": "Cliente solicitó cotización"
}
```

### `GET /api/crm/clientes/{id}/proximos-mantenimientos`

Próximos mantenimientos de todos los vehículos del cliente, usando el módulo de mantenimiento preventivo.

### `GET /api/crm/clientes/inactivos?meses=12`

Lista clientes sin visitas desde hace N meses (default 12, acepta 6 o 12). Incluye última visita, meses sin visita y total de órdenes históricas.

### `GET /api/crm/clientes/vip`

Lista clientes con total gastado ≥ $5.000.000.

### `GET /api/crm/ranking?tipo=invertido`

Ranking top 10 de clientes. Tipos: `invertido` (default), `visitas`, `ordenes`, `servicios`, `vehiculos`.

### `GET /api/crm/dashboard`

Dashboard CRM con indicadores: `clientesNuevosEsteMes`, `clientesActivos`, `clientesFrecuentes`, `clientesVip`, `clientesInactivos12Meses`, `clientesConMantenimientoProximo`, `top10ClientesFacturacion`, `top10ClientesVisitas`.

---

## Compras

### `GET /api/compras`

Lista todas las órdenes de compra ordenadas por fecha descendente.

**Auth**: Requiere autenticación.

### `GET /api/compras/{id}`

Obtiene detalle de una orden de compra con sus productos.

### `POST /api/compras`

Crea una orden de compra en estado **BORRADOR**.

**Request**:
```json
{
  "proveedorId": 1,
  "observaciones": "Compra mensual",
  "productos": [
    { "productoId": 1, "cantidad": 5, "precioUnitario": 45000 }
  ]
}
```

`proveedorId` es opcional.

**Response 201**:
```json
{
  "success": true,
  "message": "Orden de compra creada exitosamente",
  "data": {
    "id": 1,
    "numeroOrden": "OC-A1B2C3D4",
    "proveedorId": 1,
    "proveedorNombre": "Distribuidora Autopartes SAS",
    "fecha": "2026-06-10T12:00:00",
    "estado": "BORRADOR",
    "total": 225000.00,
    "observaciones": "Compra mensual",
    "productos": [
      {
        "id": 1,
        "productoId": 1,
        "productoCodigo": "ACE-001",
        "productoNombre": "Aceite Motor 20W50 Galón",
        "cantidad": 5,
        "precioUnitario": 45000.00,
        "subtotal": 225000.00
      }
    ]
  }
}
```

### `PATCH /api/compras/{id}/estado`

Cambia el estado de una orden de compra. Valida transiciones: BORRADOR→ENVIADA/CANCELADA, ENVIADA→RECIBIDA/CANCELADA. Al pasar a RECIBIDA, actualiza inventario automáticamente.

**Request**:
```json
{
  "estado": "ENVIADA"
}
```

### `DELETE /api/compras/{id}`

Elimina una orden (solo en estado BORRADOR). Soft delete.

### `GET /api/compras/recomendaciones`

Recomendaciones de compra basadas en stock actual vs punto de reposición. Incluye alertas AGOTADO/REPONER.

### `GET /api/compras/historial-producto/{productoId}`

Historial de compras de un producto. Ordenado descendente por fecha.

**Response 200**:
```json
{
  "success": true,
  "data": [
    {
      "ordenCompraId": 1,
      "numeroOrden": "OC-A1B2C3D4",
      "proveedorId": 1,
      "proveedorNombre": "Distribuidora Autopartes SAS",
      "fecha": "2026-06-10T12:00:00",
      "cantidad": 5,
      "precioUnitario": 45000.00,
      "subtotal": 225000.00,
      "estado": "RECIBIDA"
    }
  ]
}
```

### `GET /api/compras/estadisticas-proveedor/{proveedorId}`

Estadísticas de compras de un proveedor: total comprado, número de órdenes, promedio días entrega, última compra, productos suministrados.

**Response 200**:
```json
{
  "success": true,
  "data": {
    "proveedorId": 1,
    "proveedorNombre": "Distribuidora Autopartes SAS",
    "totalComprado": 1550000.00,
    "numeroOrdenes": 3,
    "promedioDiasEntrega": 5.5,
    "ultimaCompra": "2026-06-10T12:00:00",
    "productosSuministrados": 8
  }
}
```

---

## Configuración General

**Auth**: Todos los endpoints requieren autenticación.

### Empresa

#### `GET /api/configuracion/empresa`

Obtiene la configuración de la empresa activa.

**Response 200**:
```json
{
  "id": 1,
  "nombre": "Mi Serviteca",
  "razonSocial": "Mi Serviteca SAS",
  "nit": "900000000-0",
  "moneda": "COP",
  "zonaHoraria": "America/Bogota",
  "activo": true
}
```

#### `PUT /api/configuracion/empresa`

Actualiza los datos de la empresa. Solo envía campos a modificar.

**Request**:
```json
{
  "nombre": "Mi Serviteca",
  "telefono": "1234567",
  "email": "info@serviteca.com"
}
```

### Parámetros del Sistema

#### `GET /api/configuracion/parametros`

Lista todos los parámetros.

#### `GET /api/configuracion/parametros/{codigo}`

Obtiene un parámetro por código (ej. `IVA_DEFECTO`).

**Response 200**:
```json
{
  "id": 1,
  "codigo": "IVA_DEFECTO",
  "nombre": "IVA por defecto",
  "valor": "19",
  "tipo": "DECIMAL",
  "activo": true
}
```

#### `POST /api/configuracion/parametros`

Crea un nuevo parámetro.

**Request**:
```json
{
  "codigo": "NUEVO_PARAM",
  "nombre": "Nuevo Parámetro",
  "valor": "100",
  "tipo": "INTEGER"
}
```

#### `PUT /api/configuracion/parametros/{id}`

Actualiza un parámetro existente.

### Numeración Automática

#### `GET /api/configuracion/numeracion`

Lista todas las numeraciones configuradas.

#### `GET /api/configuracion/numeracion/{modulo}`

Obtiene numeración por módulo.

#### `PUT /api/configuracion/numeracion/{id}`

Actualiza configuración de numeración.

**Request**:
```json
{
  "prefijo": "ORD-",
  "sufijo": "",
  "longitud": 8,
  "reinicioAnual": false
}
```

#### `POST /api/configuracion/numeracion/{modulo}/generar`

Genera el siguiente número de la secuencia. Incrementa el contador.

**Response 200**: `"ORD-00000016"`

### Impuestos

#### `GET /api/configuracion/impuestos`

Lista los impuestos configurados.

**Response 200**:
```json
[
  { "id": 1, "nombre": "IVA 19%", "porcentaje": 19.00, "activo": true, "aplicacionPorDefecto": true },
  { "id": 2, "nombre": "IVA 5%", "porcentaje": 5.00, "activo": true, "aplicacionPorDefecto": false }
]
```

#### `POST /api/configuracion/impuestos`

#### `PUT /api/configuracion/impuestos/{id}`

### Horarios de Atención

#### `GET /api/configuracion/horarios`

Lista los 7 horarios de la semana.

**Response 200**:
```json
[
  { "id": 1, "diaSemana": "LUNES", "horaApertura": "08:00", "horaCierre": "18:00", "activo": true },
  { "id": 7, "diaSemana": "DOMINGO", "horaApertura": "08:00", "horaCierre": "13:00", "activo": false }
]
```

#### `PUT /api/configuracion/horarios/{id}`

**Request**:
```json
{
  "horaApertura": "09:00",
  "horaCierre": "17:00"
}
```

### Días Festivos

#### `GET /api/configuracion/festivos`

#### `POST /api/configuracion/festivos`

**Request**:
```json
{
  "fecha": "2026-12-25",
  "descripcion": "Navidad"
}
```

#### `DELETE /api/configuracion/festivos/{id}`

### Backups

#### `GET /api/configuracion/backups`

#### `POST /api/configuracion/backups`

**Request**:
```json
{
  "usuario": "admin",
  "tamanio": "1.5 MB",
  "estado": "EXITOSO"
}
```

### Auditoría

#### `GET /api/configuracion/auditoria`

Lista toda la auditoría. Opcionalmente filtrar por módulo.

**Query params**: `modulo` (opcional, ej. `ORDENES`)

**Response 200**:
```json
[
  {
    "id": 1,
    "usuario": "admin",
    "fecha": "2026-06-10T12:00:00",
    "accion": "ACTUALIZAR",
    "modulo": "CLIENTES",
    "entidad": "Cliente",
    "entidadId": 1,
    "valorAnterior": "...",
    "valorNuevo": "..."
  }
]
```

### Permisos por Módulo

#### `GET /api/configuracion/permisos?rolId=1`

Lista los permisos de un rol.

#### `POST /api/configuracion/permisos`

**Request**:
```json
{
  "rolId": 1,
  "modulo": "REPORTES",
  "permiso": "LEER"
}
```

#### `DELETE /api/configuracion/permisos/{id}`

---

## Dashboard

### `GET /api/dashboard`

**Auth**: Requiere autenticación.

**Indicadores operativos**:

- `totalClientes`, `totalVehiculos`, `ordenesPendientes`, `ordenesEnDiagnostico`, `ordenesEnProceso`, `ordenesEsperandoRepuestos`, `ordenesListas`, `ordenesEntregadasHoy`, `productosBajoStock`, `totalUsuarios`, `ingresosHoy`

**Indicadores financieros**:

- `cajaAbierta`, `cajaUsuario`, `ordenesSinPagar`, `ordenesParciales`, `ordenesPagadas`, `saldoPendienteTotal`

**Indicadores de vehículos**:

- `vehiculosProximosMantenimiento` — vehículos con recomendaciones activas
- `vehiculosSinVisitas12Meses` — vehículos sin órdenes en 12 meses
- `clientesFrecuentes` — clientes top 5 con más órdenes
- `vehiculosMayorInversion` — vehículos top 5 con mayor inversión histórica

**Indicadores de inventario y compras**:

- `productosBajoMinimo` — productos con stock por debajo del mínimo
- `productosAgotados` — productos con stock en cero
- `valorTotalInventario` — valor total del inventario (stock × precio venta)
- `comprasDelMes` — órdenes de compra RECIBIDA en el mes actual
- `proveedoresMasUtilizados` — top proveedores con más órdenes recibidas

**Response 200**:
```json
{
  "success": true,
  "data": {
    "totalClientes": 150,
    "totalVehiculos": 200,
    "ordenesPendientes": 12,
    "ordenesEnDiagnostico": 3,
    "ordenesEnProceso": 5,
    "ordenesEsperandoRepuestos": 2,
    "ordenesListas": 4,
    "ordenesEntregadasHoy": 8,
    "productosBajoStock": 3,
    "totalUsuarios": 5,
    "ingresosHoy": 2400000.00,
    "cajaAbierta": true,
    "cajaUsuario": "admin",
    "ordenesSinPagar": 3,
    "ordenesParciales": 1,
    "ordenesPagadas": 4,
    "saldoPendienteTotal": 1200000.00,
    "vehiculosProximosMantenimiento": 5,
    "vehiculosSinVisitas12Meses": 3,
    "clientesFrecuentes": 5,
    "vehiculosMayorInversion": 5,
    "productosBajoMinimo": 2,
    "productosAgotados": 0,
    "valorTotalInventario": 6250000.00,
    "comprasDelMes": 1,
    "proveedoresMasUtilizados": 3
  }
}
```

---

## Multiempresa

Endpoints para gestión multiempresa, multisede y transferencias entre sedes.

### Empresas

**Listar todas las empresas**

```
GET /api/multiempresa/empresas
```

Response: `200 OK`
```json
[
  {
    "id": 1,
    "nombre": "Mi Serviteca",
    "razonSocial": "Mi Serviteca SAS",
    "nit": "900000000-0",
    "direccion": "Cra 1 #2-3",
    "ciudad": "Bogotá",
    "telefono": "6012345678",
    "email": "info@miserviteca.com",
    "activo": true,
    "fechaCreacion": "2026-06-10T00:00:00"
  }
]
```

**Obtener empresa por ID**

```
GET /api/multiempresa/empresas/{id}
```

**Crear empresa**

```
POST /api/multiempresa/empresas
Content-Type: application/json

{
  "nombre": "Nueva Empresa",
  "razonSocial": "Nueva SAS",
  "nit": "900123456-7",
  "direccion": "Cra 10 #20-30",
  "ciudad": "Medellín",
  "telefono": "6041234567",
  "email": "contacto@nueva.com"
}
```

Response: `201 Created`

**Actualizar empresa**

```
PUT /api/multiempresa/empresas/{id}
Content-Type: application/json

{
  "nombre": "Empresa Actualizada",
  "telefono": "6050000000"
}
```

**Eliminar empresa (soft delete)**

```
DELETE /api/multiempresa/empresas/{id}
```

Response: `204 No Content`

### Sedes

**Listar sedes de una empresa**

```
GET /api/multiempresa/empresas/{empresaId}/sedes
```

**Listar sedes activas**

```
GET /api/multiempresa/empresas/{empresaId}/sedes/activas
```

**Crear sede**

```
POST /api/multiempresa/empresas/{empresaId}/sedes
Content-Type: application/json

{
  "nombre": "Sede Norte",
  "direccion": "Calle 1 #2-3",
  "ciudad": "Bogotá",
  "telefono": "6011111111",
  "activo": true
}
```

Response: `201 Created`

**Actualizar sede**

```
PUT /api/multiempresa/sedes/{id}
```

**Eliminar sede (soft delete)**

```
DELETE /api/multiempresa/sedes/{id}
```

Response: `204 No Content`

### Transferencias entre Sedes

**Listar transferencias**

```
GET /api/multiempresa/transferencias
```

**Crear transferencia**

```
POST /api/multiempresa/transferencias
Content-Type: application/json

{
  "sedeOrigenId": 1,
  "sedeDestinoId": 2,
  "observaciones": "Traslado de inventario",
  "productos": [
    {
      "productoId": 1,
      "cantidad": 10
    }
  ]
}
```

Response: `201 Created`

### Dashboard Consolidado

```
GET /api/multiempresa/dashboard/{empresaId}
```

Response: `200 OK`
```json
{
  "totalClientes": 50,
  "totalVehiculos": 60,
  "ordenesActivas": 5,
  "ordenesPendientesPago": 3,
  "totalIngresos": 15000000.00,
  "totalProductos": 100,
  "totalProveedores": 8
}
```

---

## Códigos HTTP utilizados

| Código | Significado |
|--------|-------------|
| 200 | OK - Operación exitosa |
| 201 | Created - Recurso creado |
| 400 | Bad Request - Error de validación |
| 401 | Unauthorized - No autenticado |
| 403 | Forbidden - Sin permisos |
| 404 | Not Found - Recurso no encontrado |
| 409 | Conflict - Recurso duplicado |
| 500 | Internal Server Error |
