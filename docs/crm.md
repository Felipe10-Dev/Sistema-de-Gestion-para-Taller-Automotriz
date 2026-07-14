# Módulo CRM - Customer Relationship Management

## Visión General

El módulo CRM convierte cada cliente en un perfil inteligente con métricas calculadas automáticamente, clasificación dinámica y herramientas para entender su comportamiento y valor para la serviteca.

El sistema **no envía** notificaciones, correos ni mensajes. Solo calcula, almacena y consulta datos, dejando todo preparado para futuras automatizaciones.

---

## 1. Perfil Inteligente del Cliente

### Endpoint

```
GET /api/crm/clientes/{id}/perfil
```

**Response 200**:
```json
{
  "success": true,
  "data": {
    "id": 1,
    "tipoDocumento": "CC",
    "numeroDocumento": "1012345678",
    "nombre": "Juan",
    "apellido": "Rodriguez",
    "telefono": "3101112233",
    "email": "juan.rodriguez@email.com",
    "direccion": "Cra 15 #45-67, Bogotá",
    "activo": true,
    "clasificacion": "FRECUENTE",
    "fechaRegistro": "2026-06-09T12:00:00",
    "primeraVisita": "2026-06-10T07:00:00",
    "ultimaVisita": "2026-06-10T07:00:00",
    "totalOrdenes": 6,
    "totalVehiculos": 2,
    "totalGastado": 175000.00,
    "promedioPorOrden": 29166.67,
    "notas": [
      {
        "id": 1,
        "usuario": "admin",
        "fecha": "2026-06-10T14:00:00",
        "comentario": "Cliente solicitó cotización de frenos"
      }
    ]
  }
}
```

### Métricas calculadas automáticamente

| Campo | Fuente |
|-------|--------|
| `fechaRegistro` | `fecha_creacion` del cliente |
| `primeraVisita` | `MIN(fechaIngreso)` de órdenes |
| `ultimaVisita` | `MAX(fechaIngreso)` de órdenes |
| `totalOrdenes` | COUNT de órdenes del cliente |
| `totalVehiculos` | COUNT de vehículos del cliente |
| `totalGastado` | SUM de `totalGeneral` de todas las órdenes |
| `promedioPorOrden` | `totalGastado / totalOrdenes` |
| `clasificacion` | Calculada según reglas de negocio |

---

## 2. Clasificación Automática de Clientes

### Reglas de Clasificación

| Clasificación | Condición |
|---------------|-----------|
| `INACTIVO` | Sin visitas en los últimos **12 meses** (o sin órdenes registradas) |
| `VIP` | Total acumulado **≥ $5.000.000** en órdenes |
| `FRECUENTE` | **≥ 5 órdenes** de trabajo registradas |
| `NUEVO` | Por defecto (no cumple ninguna condición anterior) |

### Prioridad de evaluación

1. Primero se verifica si el cliente es **INACTIVO** (sin visitas > 12 meses, incluso si tuvo muchas órdenes).
2. Luego si es **VIP** (alto valor acumulado).
3. Luego si es **FRECUENTE** (muchas visitas).
4. Si no cumple ninguna, es **NUEVO**.

### Configuración

Los umbrales están definidos como constantes en `CrmService`:

```java
public static final int UMBRAL_ORDENES_FRECUENTE = 5;
public static final BigDecimal UMBRAL_TOTAL_VIP = new BigDecimal("5000000");
public static final int MESES_INACTIVO = 12;
```

Se pueden modificar sin cambiar la arquitectura.

---

## 3. Historial Económico

### Endpoint

```
GET /api/crm/clientes/{id}/economico
```

**Response 200**:
```json
{
  "success": true,
  "data": {
    "totalInvertido": 1250000.00,
    "cantidadOrdenes": 6,
    "promedioPorOrden": 208333.33,
    "mayorCompra": 490000.00,
    "ultimaCompra": "2026-06-10T07:00:00",
    "totalPendientePago": 320000.00,
    "totalCancelado": 930000.00
  }
}
```

### Cálculos

| Campo | Fórmula |
|-------|---------|
| `totalInvertido` | SUM de `totalGeneral` de todas las órdenes |
| `cantidadOrdenes` | COUNT de órdenes del cliente |
| `promedioPorOrden` | `totalInvertido / cantidadOrdenes` |
| `mayorCompra` | MAX de `totalGeneral` por orden |
| `ultimaCompra` | MAX de `fechaIngreso` |
| `totalPendientePago` | SUM de `totalGeneral` donde `estadoFinanciero != 'PAGADA'` |
| `totalCancelado` | SUM de pagos no anulados del cliente |

---

## 4. Historial Completo de Órdenes

### Endpoint

```
GET /api/crm/clientes/{id}/ordenes
```

**Response 200**:
```json
{
  "success": true,
  "data": [
    {
      "fecha": "2026-06-10T07:00:00",
      "vehiculoPlaca": "ABC123",
      "vehiculoMarca": "Toyota",
      "vehiculoLinea": "Corolla",
      "numeroOrden": "ORD-A1B2C3D4",
      "estado": "PENDIENTE",
      "estadoFinanciero": "SIN_PAGAR",
      "total": 175000.00,
      "servicios": ["Cambio de Aceite y Filtro x1"],
      "productos": ["Aceite Motor 20W50 Galón x1", "Filtro de Aceite x1"]
    }
  ]
}
```

Ordenado de la más reciente a la más antigua.

---

## 5. Notas Internas

Las notas son registros inmutables que permiten llevar un histórico de observaciones sobre cada cliente.

### Agregar Nota

```
POST /api/crm/clientes/{id}/notas
```

**Request**:
```json
{
  "comentario": "Cliente solicitó cotización de frenos"
}
```

**Response 201**:
```json
{
  "success": true,
  "message": "Nota agregada exitosamente",
  "data": {
    "id": 1,
    "usuario": "admin",
    "fecha": "2026-06-10T14:00:00",
    "comentario": "Cliente solicitó cotización de frenos"
  }
}
```

### Listar Notas

```
GET /api/crm/clientes/{id}/notas
```

**Reglas**:
- Las notas **nunca se eliminan**.
- Solo se pueden agregar nuevas.
- Cada nota registra automáticamente: usuario, fecha y hora.

---

## 6. Clientes Inactivos

### Endpoint

```
GET /api/crm/clientes/inactivos?meses=12
```

Parámetro `meses`: 6 o 12 (default 12).

**Response 200**:
```json
{
  "success": true,
  "data": [
    {
      "id": 15,
      "nombre": "Diego",
      "apellido": "Alvarez",
      "telefono": "3223334455",
      "email": "diego.alvarez@email.com",
      "ultimaVisita": "2025-05-15T10:00:00",
      "mesesSinVisita": 13,
      "totalOrdenes": 1
    }
  ]
}
```

### Clientes VIP

```
GET /api/crm/clientes/vip
```

Lista clientes con total gastado ≥ $5.000.000.

---

## 7. Ranking de Clientes

### Endpoint

```
GET /api/crm/ranking?tipo=invertido
```

| Parámetro `tipo` | Ordenado por |
|------------------|--------------|
| `invertido` | Total gastado (default) |
| `visitas` / `ordenes` | Número de órdenes |
| `servicios` | Cantidad de servicios realizados |
| `vehiculos` | Vehículos registrados |

**Response 200**:
```json
{
  "success": true,
  "data": [
    {
      "clienteId": 1,
      "nombreCompleto": "Juan Rodriguez",
      "tipoDocumento": "CC",
      "numeroDocumento": "1012345678",
      "valor": 1250000,
      "tipo": "Total invertido"
    }
  ]
}
```

Top 10 en cada categoría.

---

## 8. Próximos Mantenimientos del Cliente

### Endpoint

```
GET /api/crm/clientes/{id}/proximos-mantenimientos
```

Reutiliza el módulo de mantenimiento preventivo existente para mostrar los mantenimientos próximos de todos los vehículos del cliente.

**Response 200**:
```json
{
  "success": true,
  "data": [
    {
      "vehiculoId": 1,
      "vehiculoPlaca": "ABC123",
      "vehiculoMarca": "Toyota",
      "vehiculoLinea": "Corolla",
      "mantenimientos": [
        {
          "recomendacionId": 1,
          "tipo": "ACEITE",
          "descripcion": "Cambio de aceite cada 5000 km o 6 meses",
          "ultimoKilometraje": 45000,
          "proximoKilometraje": 50000,
          "ultimaFecha": "2026-06-10",
          "proximaFecha": "2026-12-07",
          "kilometrosRestantes": 5000,
          "diasRestantes": 180,
          "alerta": "NORMAL"
        }
      ]
    }
  ]
}
```

---

## 9. Dashboard CRM

### Endpoint

```
GET /api/crm/dashboard
```

**Response 200**:
```json
{
  "success": true,
  "data": {
    "clientesNuevosEsteMes": 5,
    "clientesActivos": 18,
    "clientesFrecuentes": 3,
    "clientesVip": 1,
    "clientesInactivos12Meses": 2,
    "clientesConMantenimientoProximo": 4,
    "top10ClientesFacturacion": [ ... ],
    "top10ClientesVisitas": [ ... ]
  }
}
```

### Indicadores

| Indicador | Descripción |
|-----------|-------------|
| `clientesNuevosEsteMes` | Clientes registrados en el mes actual |
| `clientesActivos` | Clientes con al menos una orden en los últimos 6 meses |
| `clientesFrecuentes` | Clientes con ≥ 5 órdenes (clasificación FRECUENTE) |
| `clientesVip` | Clientes con ≥ $5M gastados (clasificación VIP) |
| `clientesInactivos12Meses` | Clientes sin visitas en los últimos 12 meses |
| `clientesConMantenimientoProximo` | Clientes con vehículos que tienen recomendaciones activas |
| `top10ClientesFacturacion` | Top 10 clientes por total gastado |
| `top10ClientesVisitas` | Top 10 clientes por número de órdenes |

---

## 10. API - Resumen de Endpoints CRM

| Método | Ruta | Descripción |
|--------|------|-------------|
| `GET` | `/api/crm/clientes/{id}/perfil` | Perfil inteligente del cliente |
| `GET` | `/api/crm/clientes/{id}/economico` | Historial económico |
| `GET` | `/api/crm/clientes/{id}/ordenes` | Historial de órdenes |
| `GET` | `/api/crm/clientes/{id}/notas` | Listar notas internas |
| `POST` | `/api/crm/clientes/{id}/notas` | Agregar nota interna |
| `GET` | `/api/crm/clientes/{id}/proximos-mantenimientos` | Próximos mantenimientos del cliente |
| `GET` | `/api/crm/clientes/inactivos` | Clientes inactivos (`?meses=6` o `12`) |
| `GET` | `/api/crm/clientes/vip` | Clientes VIP |
| `GET` | `/api/crm/ranking` | Ranking de clientes (`?tipo=...`) |
| `GET` | `/api/crm/dashboard` | Dashboard CRM |

---

## 11. Reglas de Negocio

1. **Clasificación automática**: Se calcula en tiempo real cada vez que se consulta el perfil.
2. **Notas inmutables**: Las notas nunca se eliminan ni se modifican. Solo se agregan.
3. **Sin notificaciones**: El CRM no envía WhatsApp, correos ni SMS. Solo consulta datos.
4. **Sin IA**: No se implementa inteligencia artificial. Las reglas son explícitas y configurables.
5. **Sin descuentos automáticos**: No se aplican descuentos ni programas de puntos.
6. **Datos preparados**: Toda la información está lista para futuras automatizaciones.
7. **Umbrales configurables**: Las reglas de clasificación se pueden modificar cambiando constantes en `CrmService`.
