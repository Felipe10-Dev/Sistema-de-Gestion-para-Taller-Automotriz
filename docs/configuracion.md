# Módulo de Configuración General

## Descripción
Sprint 8 — Parametrización completa del sistema. Permite administrar empresa, parámetros globales, numeración automática, impuestos, horarios, festivos, auditoría global, backups y permisos por módulo.

## Endpoints

### Empresa (`/api/configuracion/empresa`)
- `GET` — Obtener configuración de la empresa activa
- `PUT` — Actualizar datos de la empresa

### Parámetros del Sistema (`/api/configuracion/parametros`)
- `GET` — Listar todos los parámetros
- `GET /{codigo}` — Obtener por código (ej: `IVA_DEFECTO`)
- `POST` — Crear nuevo parámetro
- `PUT /{id}` — Actualizar parámetro

### Numeración Automática (`/api/configuracion/numeracion`)
- `GET` — Listar todas las numeraciones
- `GET /{modulo}` — Obtener por módulo
- `PUT /{id}` — Actualizar configuración de numeración
- `POST /{modulo}/generar` — Generar siguiente número (incrementa contador)

### Impuestos (`/api/configuracion/impuestos`)
- `GET` — Listar impuestos
- `POST` — Crear impuesto
- `PUT /{id}` — Actualizar impuesto

### Horarios de Atención (`/api/configuracion/horarios`)
- `GET` — Listar horarios (7 días)
- `PUT /{id}` — Actualizar horario

### Días Festivos (`/api/configuracion/festivos`)
- `GET` — Listar festivos
- `POST` — Crear festivo
- `DELETE /{id}` — Eliminar festivo

### Backups (`/api/configuracion/backups`)
- `GET` — Listar registros de backups
- `POST` — Crear registro de backup

### Auditoría (`/api/configuracion/auditoria`)
- `GET` — Listar toda la auditoría
- `GET ?modulo=` — Filtrar por módulo

### Permisos por Módulo (`/api/configuracion/permisos`)
- `GET ?rolId=` — Listar permisos por rol
- `POST` — Crear permiso
- `DELETE /{id}` — Eliminar permiso

## Seed Data
- **Empresa**: "Mi Serviteca", NIT 900000000-0
- **13 parámetros**: IVA_DEFECTO (19), DESCUENTO_MAXIMO (30), DIAS_INACTIVO (365), MONTO_MINIMO_VIP (5000000), ORDENES_FRECUENTE (5), umbrales km/días, etc.
- **4 numeraciones**: ORDEN_TRABAJO (ORD-), ORDEN_COMPRA (OC-), PAGO (PAG-), CAJA (CAJ-)
- **3 impuestos**: IVA 19% (defecto), IVA 5%, Exento
- **7 horarios**: LUN–VIE 08:00-18:00, SÁB 08:00-13:00, DOM inactivo
- **Permisos ADMIN**: 14 módulos × 4 permisos = 56 registros

## Notas
- Auditoría global es de solo lectura desde la API; no tiene POST/PUT/DELETE
- Backups solo registran metadatos, no ejecutan el backup real
- Numeración se genera con formato `prefijo + número_zero-padded + sufijo`
- Festivos se insertan por fecha única; no se permiten duplicados
- Permisos tienen unique constraint `(rol_id, modulo, permiso)`
