# Módulo de Compras y Abastecimiento Inteligente

## Visión General

Este módulo gestiona las órdenes de compra a proveedores, la recepción de mercancía con actualización automática de inventario, y genera recomendaciones de compra basadas en niveles de stock y puntos de reposición.

---

## 1. Órdenes de Compra

### Estados

| Estado | Descripción |
|--------|-------------|
| `BORRADOR` | Orden en edición. Se puede modificar o eliminar |
| `ENVIADA` | Orden enviada al proveedor. No se puede modificar |
| `RECIBIDA` | Mercancía recibida. Actualiza inventario automáticamente |
| `CANCELADA` | Orden cancelada. Estado terminal |

### Transiciones Válidas

```
BORRADOR  →  ENVIADA, CANCELADA
ENVIADA   →  RECIBIDA, CANCELADA
RECIBIDA  →  (ninguna, terminal)
CANCELADA →  (ninguna, terminal)
```

### Endpoints

```
GET    /api/compras                    - Listar todas las órdenes
GET    /api/compras/{id}               - Obtener orden por ID
POST   /api/compras                    - Crear orden en estado BORRADOR
PATCH  /api/compras/{id}/estado        - Cambiar estado
DELETE /api/compras/{id}               - Eliminar (solo BORRADOR, soft delete)
```

### Crear Orden de Compra

**POST** `/api/compras`

```json
{
  "proveedorId": 1,
  "observaciones": "Compra mensual lubricantes",
  "productos": [
    { "productoId": 1, "cantidad": 5, "precioUnitario": 45000 },
    { "productoId": 3, "cantidad": 10, "precioUnitario": 8500 }
  ]
}
```

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
    "total": 310000.00,
    "observaciones": "Compra mensual lubricantes",
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

El `proveedorId` es opcional. Si no se envía, la orden se crea sin proveedor asignado.

### Cambiar Estado

**PATCH** `/api/compras/{id}/estado`

```json
{
  "estado": "ENVIADA"
}
```

Validación de transiciones: si el cambio no es válido según la máquina de estados, retorna **400 Bad Request**.

### Eliminar Orden

**DELETE** `/api/compras/{id}`

Solo se pueden eliminar órdenes en estado **BORRADOR**. La eliminación es lógica (soft delete). Para cualquier otro estado, retorna **400 Bad Request**.

---

## 2. Recepción de Mercancía

Al cambiar una orden a estado **RECIBIDA**:

1. Se busca el registro de inventario para cada producto.
2. Se incrementa `cantidadActual` con la cantidad recibida.
3. Se crea un `MovimientoInventario` tipo `ENTRADA` con motivo "Orden de Compra #...".
4. El inventario se actualiza automáticamente sin intervención manual.

---

## 3. Stock Máximo y Punto de Reposición

Se agregaron dos campos a la entidad `Producto`:

| Campo | Tipo | Descripción |
|-------|------|-------------|
| `stockMaximo` | Integer | Cantidad máxima deseada en inventario |
| `puntoReposicion` | Integer | Nivel de stock que dispara recomendación de compra |

Si `puntoReposicion` no está definido, el sistema usa `stockMinimo` como valor por defecto para las alertas de reposición.

---

## 4. Recomendaciones de Compra

Endpoint que analiza el inventario actual y sugiere qué productos comprar.

### Endpoint

```
GET /api/compras/recomendaciones
```

**Response 200**:
```json
{
  "success": true,
  "data": [
    {
      "productoId": 8,
      "productoCodigo": "SUS-002",
      "productoNombre": "Amortiguador Trasero",
      "stockActual": 4,
      "stockMinimo": 5,
      "stockMaximo": null,
      "puntoReposicion": 5,
      "cantidadSugerida": 6,
      "ultimoProveedor": "Repuestos El Motor SAS",
      "ultimoPrecioCompra": 52000.00,
      "alerta": "REPONER"
    }
  ]
}
```

### Lógica de Filtrado

- Se evalúan todos los productos activos con registro en inventario.
- Se calcula el punto de reposición: `puntoReposicion` del producto, o `stockMinimo` como fallback.
- Si `stockActual > puntoReposicion` y `stockActual > 0`, el producto se omite.
- Los productos se ordenan por alerta (AGOTADO primero, REPONER después).

### Alertas

| Alerta | Significado |
|--------|-------------|
| `AGOTADO` | Stock actual = 0 |
| `REPONER` | Stock actual ≤ punto de reposición |
| `OK` | Stock normal (no debería aparecer en recomendaciones) |

### Cantidad Sugerida

Se calcula como: `stockMaximo - stockActual`, con un mínimo de 1.
Si no hay `stockMaximo`, se usa `stockMinimo * 2` como fallback.

---

## 5. Historial de Compras por Producto

### Endpoint

```
GET /api/compras/historial-producto/{productoId}
```

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

Ordenado de la compra más reciente a la más antigua.

---

## 6. Estadísticas de Proveedores

### Endpoint

```
GET /api/compras/estadisticas-proveedor/{proveedorId}
```

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

### Cálculos

| Campo | Fórmula |
|-------|---------|
| `totalComprado` | Suma de `total` de órdenes RECIBIDA del proveedor |
| `numeroOrdenes` | Conteo de órdenes RECIBIDA del proveedor |
| `promedioDiasEntrega` | Promedio de días entre `fecha` y `fechaModificacion` de órdenes RECIBIDA |
| `ultimaCompra` | `fecha` más reciente de órdenes RECIBIDA |
| `productosSuministrados` | Cantidad de productos distintos asociados al proveedor |

---

## 7. Dashboard - Indicadores de Inventario

Nuevos indicadores agregados al endpoint `GET /api/dashboard`:

| Campo | Descripción |
|-------|-------------|
| `productosBajoMinimo` | Cantidad de registros en inventario con `cantidadActual < cantidadMinima` |
| `productosAgotados` | Cantidad de registros en inventario con `cantidadActual = 0` |
| `valorTotalInventario` | Suma de `cantidadActual * precioVenta` de todos los productos |
| `comprasDelMes` | Cantidad de órdenes de compra RECIBIDA en el mes actual |
| `proveedoresMasUtilizados` | Top proveedores ordenados por cantidad de órdenes recibidas |

---

## 8. API - Resumen de Endpoints

| Método | Ruta | Descripción |
|--------|------|-------------|
| `GET` | `/api/compras` | Listar órdenes de compra |
| `GET` | `/api/compras/{id}` | Obtener orden por ID |
| `POST` | `/api/compras` | Crear orden de compra |
| `PATCH` | `/api/compras/{id}/estado` | Cambiar estado (BORRADOR/ENVIADA/RECIBIDA/CANCELADA) |
| `DELETE` | `/api/compras/{id}` | Eliminar orden (solo BORRADOR) |
| `GET` | `/api/compras/recomendaciones` | Recomendaciones de compra |
| `GET` | `/api/compras/historial-producto/{productoId}` | Historial de compras por producto |
| `GET` | `/api/compras/estadisticas-proveedor/{proveedorId}` | Estadísticas de un proveedor |

---

## 9. Reglas de Negocio

1. **Estados inmutables**: Una vez que una orden pasa a RECIBIDA o CANCELADA, no se puede cambiar su estado.
2. **Eliminación controlada**: Solo se pueden eliminar órdenes en BORRADOR (soft delete).
3. **Inventario automático**: Al recibir mercancía, el inventario se actualiza sin intervención manual.
4. **Recomendaciones sugeridas**: Las recomendaciones son solo informativas. No se generan compras automáticas.
5. **Auditoría**: Cada movimiento de inventario generado por una recepción queda registrado con usuario, fecha y motivo.
6. **Número de orden**: Formato `OC-{UUID 8 caracteres}`, generado automáticamente.
