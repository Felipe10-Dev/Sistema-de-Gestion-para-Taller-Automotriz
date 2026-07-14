# Módulo Multiempresa y Multisede

## Visión General

El sistema ahora soporta arquitectura multiinquilino (SaaS-ready). Cada empresa está completamente aislada: un usuario autenticado solo ve y opera sobre los datos de su empresa y sede asignada.

## Arquitectura

### Aislamiento de Datos

- Cada tabla del sistema tiene `empresa_id` y opcionalmente `sede_id`.
- El `empresa_id` se asigna automáticamente al persistir cualquier entidad mediante `TenantUtil.setTenantFields()`.
- El `sede_id` permite operar a nivel de sucursal dentro de una misma empresa.

### Flujo de Tenant

1. **Login**: AuthService genera JWT con claims `empresaId` y `sedeId`.
2. **Request**: JwtSecurityContextRepository extrae los claims del JWT y popula `TenantContext` (ThreadLocal).
3. **Persistencia**: `@PrePersist` en cada entidad llama a `TenantUtil.setTenantFields()` que lee de `TenantContext` y asigna `empresaId`/`sedeId`.
4. **Limpieza**: `TenantFilter` (servlet filter `@Order(1)`) limpia `TenantContext` en el finally de cada request.

## Entidades

| Entidad | Tabla | Descripción |
|---|---|---|
| `Empresa` | `empresas` | Compañía/tenant raíz |
| `Sede` | `sedes` | Sucursal de una empresa |
| `TransferenciaInventario` | `transferencias_inventario` | Transferencia de stock entre sedes |
| `TransferenciaProducto` | `transferencia_productos` | Productos individuales en una transferencia |

## Endpoints API

### Empresas

| Método | Ruta | Descripción |
|---|---|---|
| GET | `/api/multiempresa/empresas` | Lista todas las empresas |
| GET | `/api/multiempresa/empresas/{id}` | Obtiene una empresa por ID |
| POST | `/api/multiempresa/empresas` | Crea una nueva empresa |
| PUT | `/api/multiempresa/empresas/{id}` | Actualiza una empresa |
| DELETE | `/api/multiempresa/empresas/{id}` | Eliminación lógica (soft delete) |

### Sedes

| Método | Ruta | Descripción |
|---|---|---|
| GET | `/api/multiempresa/empresas/{empresaId}/sedes` | Lista sedes de una empresa |
| GET | `/api/multiempresa/empresas/{empresaId}/sedes/activas` | Lista solo sedes activas |
| POST | `/api/multiempresa/empresas/{empresaId}/sedes` | Crea una nueva sede |
| PUT | `/api/multiempresa/sedes/{id}` | Actualiza una sede |
| DELETE | `/api/multiempresa/sedes/{id}` | Eliminación lógica |

### Transferencias

| Método | Ruta | Descripción |
|---|---|---|
| GET | `/api/multiempresa/transferencias` | Lista transferencias de la empresa activa |
| POST | `/api/multiempresa/transferencias` | Crea una nueva transferencia |

### Dashboard Consolidado

| Método | Ruta | Descripción |
|---|---|---|
| GET | `/api/multiempresa/dashboard/{empresaId}` | Dashboard consolidado de la empresa |

## Dashboard Consolidado - Indicadores

| Campo | Descripción |
|---|---|
| `totalClientes` | Clientes activos de la empresa |
| `totalVehiculos` | Vehículos activos de la empresa |
| `ordenesActivas` | Órdenes en estados operativos (PENDIENTE, EN_DIAGNOSTICO, EN_PROCESO, ESPERANDO_REPUESTOS, LISTO_PARA_ENTREGA) |
| `ordenesPendientesPago` | Órdenes SIN_PAGAR o PARCIAL |
| `totalIngresos` | Suma de totalGeneral de todas las órdenes de la empresa |
| `totalProductos` | Productos activos de la empresa |
| `totalProveedores` | Proveedores activos de la empresa |

## Seed Data

La migración V011:
1. Crea la empresa "Mi Serviteca" con datos desde `configuracion_empresa`.
2. Crea "Sede Principal" asociada.
3. Actualiza todos los registros existentes con `empresa_id = 1` y `sede_id = 1`.

## Consideraciones Técnicas

- **TenantUtil.findField()**: Usa reflexión con recorrido de jerarquía de clases (getSuperclass()) para encontrar campos `empresaId`/`sedeId` incluso en entidades que heredan de BaseEntity.
- **Fallback a 1L**: Si no hay contexto de tenant (e.g., en tests), se asigna empresa/sede 1 por defecto.
- **Entidades multiempresa**: `Empresa`, `Sede`, `TransferenciaProducto` NO tienen `empresaId`/`sedeId` porque no lo necesitan (Empresa es el tenant root, Sede referencia a Empresa via FK, TransferenciaProducto referencia a TransferenciaInventario).
