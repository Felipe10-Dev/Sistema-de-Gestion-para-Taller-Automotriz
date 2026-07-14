# Flujo de Orden de Trabajo

La **Orden de Trabajo** es el núcleo del sistema. Todos los demás módulos existen para soportar su creación, ejecución y cierre.

## Máquina de Estados

```
                    ┌──────────────┐
                    │  PENDIENTE  │ ◄── Creada, esperando diagnóstico
                    └──────┬───────┘
                           │
                           ▼
                    ┌──────────────────┐
                    │ EN_DIAGNOSTICO  │ ◄── Técnico revisando
                    └──┬───────┬───────┘
                       │       │
              ┌────────┘       └────────┐
              ▼                          ▼
     ┌──────────────────┐     ┌──────────────────┐
     │   EN_PROCESO    │     │ESPERANDO_REPUESTOS│
     │ (Reparando)     │◄────│ (Esperando stock) │
     └──┬───────┬───────┘     └──────────────────┘
        │       │
        │       └────────┐
        ▼                 ▼
     ┌──────────────────────────┐
     │   LISTO_PARA_ENTREGA   │ ◄── Trabajo terminado
     └──────────┬───────────────┘
                │
                ▼
     ┌──────────────────────────┐
     │        ENTREGADO        │ ◄── Entregado al cliente (terminal)
     └──────────────────────────┘
```

Cualquier estado no terminal puede ir a **CANCELADO**.

### Transiciones permitidas

| Desde | Hacia |
|-------|-------|
| PENDIENTE | EN_DIAGNOSTICO, CANCELADO |
| EN_DIAGNOSTICO | EN_PROCESO, ESPERANDO_REPUESTOS, CANCELADO |
| EN_PROCESO | ESPERANDO_REPUESTOS, LISTO_PARA_ENTREGA, CANCELADO |
| ESPERANDO_REPUESTOS | EN_PROCESO, CANCELADO |
| LISTO_PARA_ENTREGA | ENTREGADO, CANCELADO |
| ENTREGADO | — (terminal) |
| CANCELADO | — (terminal) |

### Estados terminales

- **ENTREGADO**: No se puede modificar ni eliminar. Solo consulta.
- **CANCELADO**: No se puede modificar ni eliminar. Solo consulta.

---

## Flujo detallado

### 1. Recepción del cliente

1. El cliente llega al taller con su vehículo.
2. El operador busca al cliente en el sistema (por documento o nombre).
3. Si no existe, lo registra como nuevo cliente.
4. El operador selecciona o registra el vehículo del cliente.

### 2. Creación de la orden

1. El operador crea una nueva orden de trabajo.
2. Selecciona el cliente y el vehículo (obligatorios).
3. Opcionalmente asigna un técnico responsable.
4. Registra el kilometraje actual y observaciones.
5. Agrega los servicios solicitados desde el catálogo.
6. Agrega los productos/repuestos necesarios.
7. La orden se crea en estado **PENDIENTE**.
8. Los totales se calculan automáticamente.

### 3. Diagnóstico

1. El técnico recibe la orden y realiza la revisión inicial.
2. Cambia el estado a **EN_DIAGNOSTICO**.
3. Si necesita repuestos, puede ir a **ESPERANDO_REPUESTOS**.
4. Si determina el alcance del trabajo, pasa a **EN_PROCESO**.

### 4. Ejecución

1. El técnico realiza los servicios programados.
2. Si durante el trabajo descubre que faltan repuestos, cambia a **ESPERANDO_REPUESTOS**.
3. Cuando los repuestos llegan, regresa a **EN_PROCESO**.
4. Se pueden agregar o quitar servicios y productos mientras no esté en estado terminal.
5. Los totales se recalculan automáticamente al modificar servicios o productos.

### 5. Finalización

1. Todos los servicios han sido realizados.
2. El operador verifica que todo esté correcto.
3. Cambia el estado a **LISTO_PARA_ENTREGA**.
4. El sistema valida que la orden tenga al menos un servicio o producto.

### 6. Entrega

1. El cliente recibe el vehículo.
2. El operador cambia el estado a **ENTREGADO**.
3. El sistema registra la fecha y hora de salida.
4. Los productos utilizados se descuentan del inventario automáticamente.
5. La orden queda en estado terminal (solo consulta).

---

## Historial de Estados

Cada cambio de estado se registra automáticamente en la tabla `orden_historial_estados`:

| Campo | Descripción |
|-------|-------------|
| `orden_id` | Orden asociada |
| `estado_anterior` | Estado antes del cambio |
| `estado_nuevo` | Estado después del cambio |
| `usuario` | Usuario que realizó el cambio |
| `fecha` | Fecha y hora del cambio |
| `observacion` | Observación opcional |

El historial nunca se elimina y se devuelve en la respuesta de la API.

---

## Observaciones (Bitácora)

Las observaciones internas funcionan como una bitácora:

- Se agregan mediante `POST /api/ordenes/{id}/observaciones`.
- Cada observación registra: usuario, fecha, comentario.
- No se pueden editar ni eliminar (inmutables).
- Se devuelven en la respuesta de la API ordenadas por fecha.

---

## Reglas del flujo

- Una orden no puede saltarse estados intermedios (ej. PENDIENTE → ENTREGADO).
- Una orden en estado terminal (ENTREGADO o CANCELADO) no se puede modificar ni eliminar.
- Una orden **CANCELADA** no puede reactivarse.
- No se puede entregar una orden sin cliente, sin vehículo, o sin al menos un servicio o producto.
- Los precios se congelan al momento de agregar el ítem a la orden.
- Los totales se recalculan automáticamente al modificar servicios o productos.
- El número de orden es único y se genera automáticamente con formato `ORD-XXXXXXXX`.
- Al entregar la orden, los productos se descuentan del inventario.

---

## Endpoints de la API

| Método | Ruta | Descripción |
|--------|------|-------------|
| `GET` | `/api/ordenes` | Listar órdenes (paginado, filtro por texto o estado) |
| `GET` | `/api/ordenes/{id}` | Detalle completo de una orden (incluye historial y observaciones) |
| `POST` | `/api/ordenes` | Crear orden (estado inicial: PENDIENTE) |
| `PUT` | `/api/ordenes/{id}` | Actualizar orden (bloqueado si ENTREGADO o CANCELADO) |
| `PATCH` | `/api/ordenes/{id}/estado` | Cambiar estado (valida transición) |
| `POST` | `/api/ordenes/{id}/observaciones` | Agregar observación interna |
| `DELETE` | `/api/ordenes/{id}` | Eliminar orden (soft delete, bloqueado si ENTREGADO o CANCELADO) |

---

## Entidades involucradas

| Entidad | Rol en el flujo |
|---------|----------------|
| `Cliente` | Dueño del vehículo, responsable del pago |
| `Vehiculo` | Objeto del servicio |
| `OrdenTrabajo` | Contenedor de la orden con estado y totales |
| `OrdenServicio` | Servicio específico realizado |
| `OrdenProducto` | Producto/repuesto utilizado |
| `HistorialEstado` | Registro de cambios de estado |
| `OrdenObservacion` | Bitácora de observaciones internas |
| `Servicio` | Catálogo de servicios disponibles |
| `Producto` | Catálogo de productos disponibles |
| `Inventario` | Control de stock |
| `Usuario` (técnico) | Persona que ejecuta el servicio |
