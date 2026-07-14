# Historial Inteligente del Vehículo y Mantenimiento Preventivo

## Visión General

Éste módulo convierte cada vehículo en una fuente completa de información histórica, permitiendo consultar órdenes anteriores, calcular estadísticas, y gestionar mantenimientos preventivos.

---

## 1. Ficha Técnica del Vehículo

Se agregaron campos adicionales a la entidad `Vehiculo` para completar la ficha técnica:

| Campo | Tipo | Descripción |
|-------|------|-------------|
| `motor` | String (50) | Código del motor (ej. 2ZR-FE) |
| `combustible` | String (30) | Tipo de combustible (Gasolina, Diesel, Híbrido, Eléctrico) |
| `vin` | String (20) | Número de chasis/VIN |
| `anio` | Integer | Año del vehículo |

Estos campos se agregaron a `VehiculoRequest` y `VehiculoResponse`, son **opcionales** en creación/actualización.

---

## 2. Historial Integral del Vehículo

Endpoint que consolida todas las órdenes de un vehículo en una lista ordenada.

### Endpoint

```
GET /api/vehiculos/{id}/historial
```

**Response 200**:
```json
{
  "success": true,
  "data": [
    {
      "ordenId": 1,
      "numeroOrden": "ORD-A1B2C3D4",
      "fecha": "2026-06-10T07:00:00",
      "estado": "PENDIENTE",
      "estadoFinanciero": "SIN_PAGAR",
      "serviciosRealizados": ["Cambio de Aceite y Filtro x1"],
      "productosUtilizados": ["Aceite Motor 20W50 Galón x1", "Filtro de Aceite x1"],
      "totalGeneral": 175000.00,
      "totalPagado": 0,
      "tecnicoNombre": "Admin Sistema",
      "observaciones": "Cambio de aceite programado",
      "kilometraje": 45000
    }
  ]
}
```

Cada item incluye:
- Fecha, número de orden, estado operativo y financiero
- Lista de servicios realizados con cantidades
- Lista de productos utilizados con cantidades
- Total general de la orden
- Mecánico responsable
- Observaciones

Ordenado de la más reciente a la más antigua.

---

## 3. Línea de Tiempo

Presenta el historial del vehículo como una cronología de eventos.

### Endpoint

```
GET /api/vehiculos/{id}/linea-tiempo
```

**Response 200**:
```json
{
  "success": true,
  "data": [
    {
      "fecha": "2026-06-10T07:00:00",
      "tipo": "PENDIENTE",
      "descripcion": "Orden ORD-A1B2C3D4",
      "kilometraje": 45000,
      "totalOrden": 175000.00,
      "observaciones": "Cambio de aceite programado"
    }
  ]
}
```

Cada evento incluye:
- Fecha del evento
- Tipo (estado de la orden)
- Descripción breve
- Kilometraje registrado
- Total de la orden
- Observaciones relevantes

Ordenado de la más reciente a la más antigua.

---

## 4. Estadísticas del Vehículo

Cálculos automáticos sobre el historial del vehículo.

### Endpoint

```
GET /api/vehiculos/{id}/estadisticas
```

**Response 200**:
```json
{
  "success": true,
  "data": {
    "totalVisitas": 5,
    "totalInvertido": 1250000.00,
    "ultimaVisita": "2026-06-10T07:00:00",
    "promedioDiasEntreVisitas": 45.5,
    "servicioMasFrecuente": "Cambio de Aceite y Filtro",
    "productoMasUtilizado": "Aceite Motor 20W50 Galón"
  }
}
```

### Cálculos

| Campo | Fórmula |
|-------|---------|
| `totalVisitas` | Conteo de órdenes del vehículo |
| `totalInvertido` | Suma de `totalGeneral` de todas las órdenes |
| `ultimaVisita` | `fechaIngreso` más reciente |
| `promedioDiasEntreVisitas` | Promedio de días entre cada visita (0 si < 2 visitas) |
| `servicioMasFrecuente` | Servicio que más se ha realizado |
| `productoMasUtilizado` | Producto que más se ha usado |

---

## 5. Mantenimiento Preventivo

Permite registrar recomendaciones de mantenimiento futuro para cada vehículo.

### Tipos de Mantenimiento

| Tipo | Descripción |
|------|-------------|
| `ACEITE` | Cambio de aceite |
| `FILTROS` | Cambio de filtros |
| `FRENOS` | Revisión de frenos |
| `ALINEACION` | Alineación y balanceo |
| `BALANCEO` | Balanceo |
| `OTRO` | Otros mantenimientos |

### Tipo de Programación

| Valor | Significado |
|-------|-------------|
| `KILOMETRAJE` | Basado solo en kilómetros |
| `FECHA` | Basado solo en fecha |
| `AMBOS` | Basado en ambos (el que se cumpla primero) |

### Endpoints CRUD

```
GET    /api/vehiculos/{id}/mantenimientos          - Listar recomendaciones
POST   /api/vehiculos/{id}/mantenimientos          - Crear recomendación
PUT    /api/vehiculos/mantenimientos/{id}          - Actualizar recomendación
DELETE /api/vehiculos/mantenimientos/{id}          - Eliminar (desactivar)
```

### Crear Recomendación

**POST** `/api/vehiculos/{id}/mantenimientos`

```json
{
  "tipo": "ACEITE",
  "descripcion": "Cambio de aceite cada 5000 km o 6 meses",
  "tipoProgramacion": "AMBOS",
  "intervaloKilometraje": 5000,
  "intervaloDias": 180
}
```

---

## 6. Próximos Mantenimientos

Calcula automáticamente las próximas fechas y kilometrajes basándose en el último registro del vehículo.

### Endpoint

```
GET /api/vehiculos/{id}/proximos-mantenimientos
```

**Response 200**:
```json
{
  "success": true,
  "data": [
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
```

### Alertas

| Alerta | Significado |
|--------|-------------|
| `NORMAL` | Dentro de los rangos esperados |
| `PRONTO` | Próximo a vencer (≤ 1000 km o ≤ 30 días) |
| `PROXIMO` | Muy próximo (≤ 500 km o ≤ 15 días) |

### Cálculo

- **Próximo kilometraje**: `ultimoKilometraje + intervaloKilometraje` (basado en la última orden)
- **Próxima fecha**: `ultimaFecha + intervaloDias` (basado en la fecha de la última orden)
- **Kilómetros restantes**: `proximoKilometraje - ultimoKilometraje` (o 0 si ya se pasó)
- **Días restantes**: `proximaFecha - hoy` (o 0 si ya se pasó)

---

## 7. Dashboard

Nuevos indicadores agregados al endpoint `GET /api/dashboard`:

| Campo | Descripción |
|-------|-------------|
| `vehiculosProximosMantenimiento` | Vehículos con al menos una recomendación activa de mantenimiento |
| `vehiculosSinVisitas12Meses` | Vehículos sin ninguna orden en los últimos 12 meses |
| `clientesFrecuentes` | Clientes con más órdenes (top 5) |
| `vehiculosMayorInversion` | Vehículos con mayor inversión histórica (top 5) |

---

## 8. API - Resumen de Endpoints

| Método | Ruta | Descripción |
|--------|------|-------------|
| `GET` | `/api/vehiculos/{id}/historial` | Historial completo de órdenes |
| `GET` | `/api/vehiculos/{id}/linea-tiempo` | Línea de tiempo del vehículo |
| `GET` | `/api/vehiculos/{id}/estadisticas` | Estadísticas del vehículo |
| `GET` | `/api/vehiculos/{id}/mantenimientos` | Listar recomendaciones |
| `POST` | `/api/vehiculos/{id}/mantenimientos` | Crear recomendación |
| `PUT` | `/api/vehiculos/mantenimientos/{id}` | Actualizar recomendación |
| `DELETE` | `/api/vehiculos/mantenimientos/{id}` | Eliminar recomendación |
| `GET` | `/api/vehiculos/{id}/proximos-mantenimientos` | Próximos mantenimientos calculados |

---

## 9. Reglas de Negocio

1. **Ficha técnica**: Los nuevos campos (motor, combustible, VIN, año) son opcionales y se pueden actualizar sin perder historial.
2. **Historial inmodificable**: El historial de órdenes es de solo lectura. No se puede modificar desde estos endpoints.
3. **Estadísticas automáticas**: Se calculan en tiempo real basándose en las órdenes existentes.
4. **Mantenimientos por vehículo**: Cada recomendación pertenece a un vehículo específico.
5. **Eliminación lógica**: Las recomendaciones se desactivan (soft delete), no se eliminan físicamente.
6. **Alertas visuales**: Las alertas (NORMAL / PRONTO / PROXIMO) se calculan en base a umbrales fijos.
7. **Sin notificaciones**: Este sprint no implementa envío de alertas por WhatsApp ni correo.
