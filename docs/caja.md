# Módulo de Caja y Pagos

## Visión General

El módulo de Caja y Pagos permite controlar el flujo de dinero de la serviteca. Está diseñado para ser simple de usar en el día a día pero lo suficientemente robusto para manejar escenarios reales.

### Conceptos clave

- **Caja**: Sesión de trabajo de un usuario (apertura → registro de pagos → cierre).
- **Pago**: Transacción económica aplicada a una Orden de Trabajo.
- **Estado Financiero**: Estado independiente del estado operativo de la orden (SIN_PAGAR, PARCIAL, PAGADA).
- **Método de Pago**: Configuración dinámica de formas de pago disponibles.

---

## 1. Apertura de Caja

Cada jornada laboral debe comenzar con una apertura de caja.

### Reglas

- Solo puede existir **una caja abierta por usuario**.
- Si el usuario ya tiene una caja abierta, no se permite abrir otra.
- Se registra: usuario, fecha/hora, monto inicial, observación.

### Endpoint

```
POST /api/caja/apertura
```

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
    "totalEsperado": 0,
    "observaciones": "Apertura diaria"
  }
}
```

---

## 2. Cierre de Caja

Al finalizar la jornada (o cuando sea necesario) se cierra la caja.

### Cálculos automáticos

| Campo | Cálculo |
|-------|---------|
| Total ingresos | Suma de todos los pagos registrados entre apertura y cierre |
| Total esperado | Monto inicial + Total ingresos |
| Diferencia | Monto contado - Total esperado |

### Endpoint

```
POST /api/caja/{id}/cierre
```

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
    "montoInicial": 50000.00,
    "totalIngresos": 500000.00,
    "totalEsperado": 550000.00,
    "montoContado": 550000.00,
    "diferencia": 0.00,
    "fechaCierre": "2026-06-10T18:00:00"
  }
}
```

### Reglas

- No se puede cerrar una caja que no esté abierta.
- No se puede cerrar dos veces la misma caja.
- Los registros de cierre nunca se eliminan.

---

## 3. Registro de Pagos

Cada Orden de Trabajo puede recibir uno o varios pagos.

### Endpoint

```
POST /api/pagos
```

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

### Validaciones

- El valor debe ser mayor a cero.
- El valor no puede exceder el saldo pendiente de la orden.
- El método de pago debe existir y estar activo.

---

## 4. Pagos Parciales

El sistema soporta múltiples pagos por orden.

**Ejemplo:**
- Orden = $800,000
- Pago 1 = $300,000 → Estado financiero: PARCIAL
- Pago 2 = $500,000 → Estado financiero: PAGADA

### Consultar saldo pendiente

```
GET /api/pagos/orden/{ordenId}/saldo
```

**Response**: `300000.00` (saldo pendiente)

---

## 5. Estado Financiero

Cada Orden de Trabajo tiene un estado financiero independiente del estado operativo.

| Estado | Significado |
|--------|-------------|
| `SIN_PAGAR` | No se ha registrado ningún pago |
| `PARCIAL` | Se ha pagado parte del total |
| `PAGADA` | El total pagado cubre el total general |

**Ejemplo de estados válidos:**
- Orden = ENTREGADO, Estado financiero = PARCIAL ✅
- Orden = PENDIENTE, Estado financiero = PAGADA ✅

### Cálculo automático

```java
if totalPagado == 0 → SIN_PAGAR
if totalPagado >= totalGeneral → PAGADA
else → PARCIAL
```

El estado financiero se actualiza automáticamente al:
- Registrar un pago
- Anular un pago

---

## 6. Anulación de Pagos

Si un pago fue registrado incorrectamente, se debe anular (nunca eliminar).

### Endpoint

```
POST /api/pagos/{id}/anular
```

**Response 200**: El pago queda marcado como `pagoAnulado = true`. El estado financiero de la orden se recalcula automáticamente.

### Reglas

- Un pago anulado no se elimina (solo cambia la bandera).
- No se puede anular un pago ya anulado.
- Al anular, el saldo pendiente de la orden aumenta.

---

## 7. Métodos de Pago Configurables

Los métodos de pago se administran mediante CRUD. No están quemados en el código.

### Métodos por defecto (seed)

1. Efectivo
2. Tarjeta Débito
3. Tarjeta Crédito
4. Transferencia
5. Nequi
6. Daviplata
7. Otro

### Endpoints

| Método | Ruta | Descripción |
|--------|------|-------------|
| `GET` | `/api/metodos-pago` | Listar métodos activos |
| `GET` | `/api/metodos-pago/todos` | Listar todos (activos e inactivos) |
| `POST` | `/api/metodos-pago` | Crear nuevo método |
| `PUT` | `/api/metodos-pago/{id}` | Actualizar nombre |
| `PATCH` | `/api/metodos-pago/{id}/toggle` | Activar/desactivar |

**Auth**: Solo ADMIN puede crear, actualizar o desactivar métodos de pago.

---

## 8. Dashboard Financiero

El endpoint `GET /api/dashboard` ahora incluye:

| Campo | Descripción |
|-------|-------------|
| `cajaAbierta` | ¿El usuario actual tiene caja abierta? |
| `cajaUsuario` | Usuario actual |
| `ordenesSinPagar` | Órdenes con estado financiero SIN_PAGAR |
| `ordenesParciales` | Órdenes con estado financiero PARCIAL |
| `ordenesPagadas` | Órdenes con estado financiero PAGADA |
| `saldoPendienteTotal` | Suma de saldos pendientes de todas las órdenes no pagadas |

---

## 9. API REST - Resumen de Endpoints

| Método | Ruta | Descripción |
|--------|------|-------------|
| `GET` | `/api/caja/actual` | Obtener caja abierta del usuario |
| `GET` | `/api/caja/historial` | Historial de cierres de caja |
| `POST` | `/api/caja/apertura` | Abrir caja |
| `POST` | `/api/caja/{id}/cierre` | Cerrar caja |
| `GET` | `/api/caja/movimientos` | Listar movimientos financieros |
| `GET` | `/api/pagos` | Listar todos los pagos |
| `GET` | `/api/pagos/orden/{id}` | Pagos de una orden |
| `GET` | `/api/pagos/orden/{id}/saldo` | Saldo pendiente de una orden |
| `POST` | `/api/pagos` | Registrar pago |
| `POST` | `/api/pagos/{id}/anular` | Anular pago |
| `GET` | `/api/metodos-pago` | Listar métodos de pago activos |
| `POST` | `/api/metodos-pago` | Crear método de pago (ADMIN) |
| `PUT` | `/api/metodos-pago/{id}` | Actualizar método (ADMIN) |
| `PATCH` | `/api/metodos-pago/{id}/toggle` | Activar/desactivar (ADMIN) |

---

## 10. Historial Financiero

Todos los movimientos se registran en la tabla `movimientos_caja`:

| Tipo | Descripción |
|------|-------------|
| `APERTURA` | Apertura de caja |
| `INGRESO_PAGO` | Pago registrado |
| `ANULACION` | Anulación de pago |
| `CIERRE` | Cierre de caja |

El historial nunca se elimina y es consultable mediante `GET /api/caja/movimientos`.

---

## 11. Reglas de Negocio

1. **Una caja abierta por usuario**: No se permite doble apertura.
2. **Pagos positivos**: El valor del pago debe ser mayor a cero.
3. **No exceder saldo**: No se permite pagar más del saldo pendiente.
4. **Estado financiero automático**: Se recalcula con cada pago o anulación.
5. **Anulación in-place**: Los pagos anulados no se eliminan, solo se marcan.
6. **Métodos configurables**: No están hardcodeados, se administran vía API.
7. **Cierre con cálculos**: Total esperado = monto inicial + ingresos del período.
8. **Auditoría completa**: Todos los movimientos financieros quedan registrados.
9. **Diferencia de caja**: El sistema calcula la diferencia entre lo esperado y lo contado.
