# Changelog

Todas las modificaciones importantes del proyecto se registran aquí.

Formato basado en [Keep a Changelog](https://keepachangelog.com/).

---

## [1.9.0] - 2026-06-10

### Added

- **Arquitectura multiempresa y multisede**: Nuevo paquete `com.serviteca.multiempresa` con entidades `Empresa`, `Sede`, `TransferenciaInventario`, `TransferenciaProducto`, DTOs, repositorios, servicios y controlador.
- **Empresas CRUD**: Endpoints `GET/POST /api/multiempresa/empresas`, `GET/PUT/DELETE /api/multiempresa/empresas/{id}`. Soft delete.
- **Sedes CRUD**: Endpoints `GET/POST /api/multiempresa/empresas/{empresaId}/sedes`, `GET /api/multiempresa/empresas/{empresaId}/sedes/activas`, `PUT/DELETE /api/multiempresa/sedes/{id}`. Soft delete.
- **Transferencias entre sedes**: Endpoints `POST/GET /api/multiempresa/transferencias`. Historial completo con sede origen, destino y productos transferidos.
- **Dashboard Consolidado**: Endpoint `GET /api/multiempresa/dashboard/{empresaId}`. Indicadores: total clientes, vehículos, órdenes activas, órdenes pendientes de pago, ingresos totales, productos, proveedores.
- **Aislamiento multiempresa**: `BaseEntity` ahora incluye `empresaId` y `sedeId`. `TenantUtil.setTenantFields()` asigna automáticamente desde el contexto de seguridad o fallback a 1L. `JwtSecurityContextRepository` popula `TenantContext` desde el JWT.
- **TenantUtil**: Utility class con `setTenantFields(Object)` que usa reflexión con jerarquía de clases para asignar empresaId/sedeId en cualquier entidad.
- **Migración Flyway V011**: Tablas `empresas`, `sedes`, `transferencias_inventario`, `transferencia_productos`. Columnas `empresa_id` y `sede_id` agregadas a TODAS las tablas existentes con actualización de datos. Nuevos índices unique compuestos.
- **Pruebas de integración**: 6 tests en `MultiempresaIntegrationTest` que cubren CRUD empresa, CRUD sede, listado, soft delete, dashboard consolidado y restricción de autenticación.

### Changed

- **BaseEntity**: Agregados campos `empresaId` y `sedeId` con getters/setters. Reemplazado `@PrePersist` por método estático `initTenant(BaseEntity)`.
- **Usuario.java**: Campo `sedeId` removido (ahora en BaseEntity).
- **LoginResponse.java**: Campos `empresaId` y `sedeId` agregados.
- **JwtTokenProvider.java**: `generateAccessToken` ahora recibe y emite claims `empresaId`/`sedeId`. Método `getClaim(token, claimName)` agregado.
- **AuthService.java**: Login/register/refreshToken pasan empresaId/sedeId al token y LoginResponse. Register asigna empresaId=1L y sedeId=1L.
- **JwtSecurityContextRepository**: Ahora popula `TenantContext.setEmpresaId()` y `TenantContext.setSedeId()` al autenticar desde JWT.
- **SecurityConfig**: Limpiado — ya no registra `TenantFilter` en la cadena Spring Security.
- **TenantFilter**: Refactorizado a `@Component @Order(1)` — solo limpia `TenantContext` en finally.
- **~30 entidades modificadas**: Agregados campos `empresaId`/`sedeId` con `@Column` y `@PrePersist` que llama `TenantUtil.setTenantFields(this)`.
- **Repositorios**: Agregados métodos `countByEmpresaId` y variantes en `ClienteRepository`, `VehiculoRepository`, `OrdenTrabajoRepository`, `ProductoRepository`, `ProveedorRepository` para filtrado multiempresa.

### Fixed

- **TenantUtil**: `getDeclaredField()` reemplazado por `findField()` que recorre la jerarquía de clases para encontrar campos heredados de BaseEntity.
- **TenantUtil**: Agregado fallback a 1L cuando no hay contexto de tenant disponible.
- **V011 migration**: Agregado `sede_id` faltante en `ordenes_compra_productos` y `transferencias_inventario`. Corregido `AUTO_INCREMENT` → `BIGSERIAL` para compatibilidad H2.
- **Sede.java/Empresa.java/TransferenciaProducto.java**: Removidos campos redundantes `empresaId`/`sedeId` que causaban `MappingException` por columna duplicada.
- **9 entidades BaseEntity**: Agregado `@PrePersist` faltante (Categoria, Cliente, Inventario, Producto, Proveedor, Rol, Servicio, Usuario, Vehiculo).

---

## [1.8.0] - 2026-06-10

### Added

- **Módulo de Configuración General**: Nuevo paquete `com.serviteca.configuracion` con 9 entidades, 17 DTOs, 9 repositorios, servicio y controlador.
- **Empresa**: Endpoint `GET/PUT /api/configuracion/empresa`. Datos de la empresa activa (nombre, NIT, dirección, moneda, zona horaria, etc.).
- **Parámetros del Sistema**: Endpoint `GET /api/configuracion/parametros`, `GET /{codigo}`, `POST`, `PUT /{id}`. 13 parámetros seed como IVA_DEFECTO (19%), DESCUENTO_MAXIMO (30), umbrales CRM y alertas.
- **Numeración Automática**: Endpoint `GET /api/configuracion/numeracion`, `GET /{modulo}`, `PUT /{id}`, `POST /{modulo}/generar`. 4 módulos seed (ORDEN_TRABAJO, ORDEN_COMPRA, PAGO, CAJA) con prefijo, sufijo, longitud y reinicio anual configurables.
- **Impuestos**: Endpoint `GET /api/configuracion/impuestos`, `POST`, `PUT /{id}`. 3 impuestos seed (IVA 19%, IVA 5%, Exento) con selección de defecto.
- **Horarios de Atención**: Endpoint `GET /api/configuracion/horarios`, `PUT /{id}`. 7 horarios seed (LUN-VIE 08:00-18:00, SÁB 08:00-13:00, DOM inactivo).
- **Días Festivos**: Endpoint `GET /api/configuracion/festivos`, `POST`, `DELETE /{id}`. Festivos personalizados por fecha única.
- **Backups**: Endpoint `GET /api/configuracion/backups`, `POST`. Registro de metadatos de backups (no ejecuta backup real).
- **Auditoría Global**: Endpoint `GET /api/configuracion/auditoria?modulo=`. Tabla `auditoria_global` de solo lectura. Registra usuario, fecha, acción, módulo, valores anterior/nuevo.
- **Permisos por Módulo**: Endpoint `GET /api/configuracion/permisos?rolId=`, `POST`, `DELETE /{id}`. Permisos CRUD granulares por módulo y rol. Seed: 56 permisos para rol ADMIN (14 módulos x 4 acciones).
- **Migración Flyway V010**: 9 tablas nuevas con seed data completa.
- **Pruebas de integración**: 14 tests en `ConfiguracionIntegrationTest` que cubren empresa, parámetros, numeración, impuestos, horarios, festivos, backups, auditoría, permisos.

### Changed

- **Roadmap**: Fase 3 actualizada — Módulo de configuración marcado como completado.

---

## [1.7.0] - 2026-06-10

### Added

- **Módulo CRM**: Nuevo paquete `com.serviteca.crm` con entidades, DTOs, repositorios, servicios y controladores.
- **Perfil Inteligente del Cliente**: Endpoint `GET /api/crm/clientes/{id}/perfil`. Muestra fecha registro, primera/última visita, total órdenes, vehículos, total gastado, promedio por orden, notas y clasificación automática.
- **Clasificación Automática**: Clientes clasificados como NUEVO, FRECUENTE (≥5 órdenes), VIP (≥$5M acumulados) o INACTIVO (sin visitas >12 meses). Reglas configurables como constantes en `CrmService`.
- **Historial Económico**: Endpoint `GET /api/crm/clientes/{id}/economico`. Total invertido, cantidad órdenes, promedio, mayor compra, última compra, pendiente de pago y total cancelado.
- **Historial Completo de Órdenes**: Endpoint `GET /api/crm/clientes/{id}/ordenes`. Lista todas las órdenes del cliente con servicios y productos.
- **Notas Internas**: Entidad `ClienteNota` (tabla `notas_crm`). Endpoints `GET/POST /api/crm/clientes/{id}/notas`. Las notas son inmutables (nunca se eliminan ni modifican). Registran usuario, fecha y hora automáticamente.
- **Clientes Inactivos**: Endpoint `GET /api/crm/clientes/inactivos?meses=12`. Detecta clientes sin visitas en 6 o 12 meses. Incluye clientes sin ninguna orden registrada.
- **Clientes VIP**: Endpoint `GET /api/crm/clientes/vip`. Lista clientes con ≥$5M gastados.
- **Ranking de Clientes**: Endpoint `GET /api/crm/ranking?tipo=invertido`. Top 10 por: invertido, visitas, servicios realizados o vehículos registrados.
- **Próximos Mantenimientos del Cliente**: Endpoint `GET /api/crm/clientes/{id}/proximos-mantenimientos`. Reutiliza el módulo de mantenimiento preventivo para mostrar mantenimientos de todos los vehículos del cliente.
- **Dashboard CRM**: Endpoint `GET /api/crm/dashboard`. Indicadores: nuevos este mes, activos, frecuentes, VIP, inactivos 12M, con mantenimiento próximo, top 10 facturación y visitas.
- **Migración Flyway V009**: Columna `clasificacion` en `clientes`. Tabla `notas_crm` con índice.
- **Pruebas de integración**: 10 tests en `CrmIntegrationTest` que cubren perfil, económico, órdenes, notas (CRUD), ranking, inactivos, VIP, dashboard y mantenimientos próximos.

### Changed

- **Cliente entity**: Agregado campo `clasificacion` con getter/setter.
- **ClienteResponse**: Agregado campo `clasificacion`.
- **ClienteMapper**: Incluye `clasificacion` en `toResponse`.
- **ClienteRepository**: Agregadas queries `findClientesSinVisitasDesde` y `countNuevosEsteMes`.
- **OrdenTrabajoRepository**: Agregadas 9 queries para consultas por cliente (perfil, económico, ranking).
- **PagoRepository**: Agregada query `sumByClienteIdAndAnuladoFalse`.
- **OrdenServicioRepository**: Agregada query `countByClienteGrouped` para ranking por servicios.
- **ClienteNotaRepository**: Nuevo repositorio para notas CRM.

---

## [1.6.0] - 2026-06-10

### Added

- **Módulo de Compras**: Nuevo paquete `com.serviteca.compra` con entidades `OrdenCompra` y `OrdenCompraProducto`, controladores, servicios y repositorios.
- **Órdenes de Compra**: Endpoints CRUD con estados BORRADOR, ENVIADA, RECIBIDA, CANCELADA. Validación de transiciones vía máquina de estados. Número de orden automático (`OC-XXXXXXXX`).
- **Recepción de Mercancía**: Al pasar una orden a RECIBIDA, se actualiza automáticamente `cantidadActual` en inventario y se registra movimiento tipo ENTRADA con motivo "Orden de Compra #...".
- **Stock Máximo y Punto de Reposición**: Campos `stockMaximo` y `puntoReposicion` en entidad `Producto`. Fallback a `stockMinimo` si no hay `puntoReposicion`.
- **Recomendaciones de Compra**: Endpoint `GET /api/compras/recomendaciones`. Productos con stock ≤ punto de reposición. Alertas: AGOTADO, REPONER. Cantidad sugerida calculada como `stockMaximo - stockActual`.
- **Historial de Compras por Producto**: Endpoint `GET /api/compras/historial-producto/{productoId}`. Lista todas las compras de un producto ordenadas por fecha descendente.
- **Estadísticas de Proveedores**: Endpoint `GET /api/compras/estadisticas-proveedor/{proveedorId}`. Total comprado, número de órdenes, promedio días entrega, última compra, productos suministrados.
- **Dashboard de Inventario**: Nuevos indicadores: `productosBajoMinimo`, `productosAgotados`, `valorTotalInventario`, `comprasDelMes`, `proveedoresMasUtilizados`.
- **Migración Flyway V008**: Columnas `stock_maximo`, `punto_reposicion` en productos. Tablas `ordenes_compra` y `ordenes_compra_productos` con índices y columnas de auditoría.
- **Pruebas de integración**: 10 tests en `CompraIntegrationTest` que cubren creación con/sin proveedor, listar, cambio de estado, transición inválida, recepción con actualización de inventario, eliminación (BORRADOR y no BORRADOR), recomendaciones y dashboard.

### Changed

- **ProductoService**: Maneja nuevos campos `stockMaximo` y `puntoReposicion` en creación y actualización.
- **ProductoMapper**: Mapea los 2 nuevos campos.
- **DashboardService**: Inyecta `OrdenCompraRepository`. Calcula 5 nuevos indicadores de inventario y compras.
- **DashboardResponse**: Agregados campos `productosBajoMinimo`, `productosAgotados`, `valorTotalInventario`, `comprasDelMes`, `proveedoresMasUtilizados`.
- **InventarioRepository**: Agregado `countByCantidadActual` y `sumValorTotalInventario()`.
- **OrdenCompraRepository**: Agregados queries JPQL para estadísticas de proveedores (total, fecha máxima, promedio días, proveedores más utilizados).

### Fixed

- **Migración V008**: Agregadas columnas `fecha_eliminacion` y `eliminado_por` faltantes en tabla `ordenes_compra` (requeridas por `BaseEntity`).
- **OrdenCompraRepository**: Corregida query `findPromedioDiasEntregaByProveedorId` — cambiado `DATEDIFF('DAY', ...)` por `TIMESTAMPDIFF(DAY, ...)` para compatibilidad con Hibernate 6.

---

## [1.5.0] - 2026-06-10

### Added

- **Ficha Técnica del Vehículo**: Campos `motor`, `combustible`, `vin`, `anio` en entidad `Vehiculo`. Opcionales en creación/actualización. No afectan historial existente.
- **Historial Integral del Vehículo**: Endpoint `GET /api/vehiculos/{id}/historial`. Lista todas las órdenes del vehículo ordenadas por fecha descendente, incluyendo servicios, productos, total, técnico y observaciones.
- **Línea de Tiempo**: Endpoint `GET /api/vehiculos/{id}/linea-tiempo`. Cronología de eventos del vehículo con fecha, tipo, kilometraje y total.
- **Estadísticas del Vehículo**: Endpoint `GET /api/vehiculos/{id}/estadisticas`. Cálculos automáticos: total visitas, total invertido, última visita, promedio días entre visitas, servicio y producto más frecuentes.
- **Mantenimiento Preventivo**: Nueva entidad `MantenimientoRecomendacion` con tabla `mantenimiento_recomendaciones`. Tipos: ACEITE, FILTROS, FRENOS, ALINEACION, BALANCEO, OTRO. Programación por KILOMETRAJE, FECHA o AMBOS.
- **CRUD de Recomendaciones**: Endpoints para listar, crear, actualizar y eliminar (soft delete) recomendaciones de mantenimiento por vehículo.
- **Próximos Mantenimientos**: Endpoint `GET /api/vehiculos/{id}/proximos-mantenimientos`. Calcula automáticamente próximo kilometraje y fecha basados en la última orden y las recomendaciones activas. Alertas visuales: NORMAL, PRONTO, PROXIMO.
- **Migración Flyway V007**: Columnas `motor`, `combustible`, `vin`, `anio` en `vehiculos`. Tabla `mantenimiento_recomendaciones` con índices.
- **Dashboard de Vehículos**: Nuevos indicadores: `vehiculosProximosMantenimiento`, `vehiculosSinVisitas12Meses`, `clientesFrecuentes`, `vehiculosMayorInversion`.
- **Pruebas de integración**: 8 tests en `HistorialVehiculoIntegrationTest` que cubren historial, línea de tiempo, estadísticas, CRUD de recomendaciones, próximos mantenimientos, dashboard y ficha técnica.

### Changed

- **VehiculoService**: Maneja nuevos campos `motor`, `combustible`, `vin`, `anio` en creación y actualización.
- **VehiculoMapper**: Mapea los 4 nuevos campos de ficha técnica.
- **VehiculoController**: Nuevos endpoints para historial, línea de tiempo, estadísticas, mantenimientos y próximos mantenimientos.
- **DashboardService**: Inyecta `MantenimientoRecomendacionRepository`. Calcula 4 nuevos indicadores de vehículos.
- **OrdenTrabajoRepository**: Nuevas queries: `findByVehiculoIdOrderByFechaIngresoDesc`, `countByVehiculoId`, `sumTotalGeneralByVehiculoId`, `findMaxFechaIngresoByVehiculoId`, `findVehiculosSinVisitasDesde`, `findClientesFrecuentes`, `findVehiculosMayorInversion`.

---

## [1.4.0] - 2026-06-10

### Added

- **Módulo Caja y Pagos**: Nuevo paquete `com.serviteca.caja` con entidades, servicios, controladores y repositorios.
- **Apertura de Caja**: Endpoint `POST /api/caja/apertura`. Una caja abierta por usuario. Registra usuario, fecha, monto inicial y observación.
- **Cierre de Caja**: Endpoint `POST /api/caja/{id}/cierre`. Calcula automáticamente: total ingresos, total esperado, diferencia vs monto contado.
- **Historial de Caja**: Tabla `movimientos_caja` que registra cada acción financiera (APERTURA, INGRESO_PAGO, ANULACION, CIERRE). Nunca se elimina.
- **Registro de Pagos**: Endpoint `POST /api/pagos`. Pagos asociados a órdenes de trabajo. Soporta pagos parciales y múltiples.
- **Estado Financiero**: Nuevo campo `estado_financiero` en `ordenes_trabajo`. Estados: `SIN_PAGAR`, `PARCIAL`, `PAGADA`. Independiente del estado operativo.
- **Pagos Parciales**: Una orden puede tener múltiples pagos. El sistema recalcula el estado financiero automáticamente.
- **Anulación de Pagos**: Endpoint `POST /api/pagos/{id}/anular`. Marca el pago como anulado (no lo elimina). Recalcula el estado financiero.
- **Métodos de Pago Configurables**: Tabla `metodos_pago`. Seed con 7 métodos (Efectivo, Tarjeta Débito/Crédito, Transferencia, Nequi, Daviplata, Otro). CRUD completo. Solo ADMIN puede gestionar.
- **Dashboard Financiero**: Nuevos indicadores: `cajaAbierta`, `cajaUsuario`, `ordenesSinPagar`, `ordenesParciales`, `ordenesPagadas`, `saldoPendienteTotal`.
- **Migración Flyway V006**: Crea tablas `caja`, `pagos`, `metodos_pago`, `movimientos_caja` con índices y seed data. Agrega columna `estado_financiero` a `ordenes_trabajo`.
- **Pruebas de integración**: 7 tests en `CajaPagoIntegrationTest` que cubren apertura, doble apertura, cierre con cálculos, pagos, pagos parciales, estado financiero, saldo pendiente, y métodos de pago.

### Security

- **Rutas protegidas**: `/api/caja/**` y `/api/pagos/**` requieren autenticación. `/api/metodos-pago/**` requiere rol ADMIN para operaciones de escritura.

### Changed

- **OrdenTrabajoResponse**: Ahora incluye `estadoFinanciero`.
- **OrdenMapper**: Mapea `estadoFinanciero` de la entidad a la respuesta.
- **DashboardService**: Inyecta `CajaRepository`. Calcula indicadores financieros.
- **DashboardResponse**: Agregados campos `cajaAbierta`, `cajaUsuario`, `ordenesSinPagar`, `ordenesParciales`, `ordenesPagadas`, `saldoPendienteTotal`.
- **OrdenTrabajoRepository**: Agregados `countByEstadoFinanciero()` y `sumSaldoPendienteTotal()`.

---

## [1.3.0] - 2026-06-10

### Added

- **Máquina de estados con 7 estados**: PENDIENTE → EN_DIAGNOSTICO → EN_PROCESO → ESPERANDO_REPUESTOS → LISTO_PARA_ENTREGA → ENTREGADO, más CANCELADO. Mapa de transiciones en `OrdenEstado` enum.
- **Historial de estados**: Tabla `orden_historial_estados`, endpoint `GET /api/ordenes/{id}` incluye historial.
- **Observaciones (bitácora)**: Tabla `orden_observaciones`, endpoint `POST /api/ordenes/{id}/observaciones`.
- **Edición de orden**: `PUT /api/ordenes/{id}` con recálculo automático de totales.
- **Dashboard operativo**: Indicadores de diagnóstico, espera de repuestos, listos, entregados hoy, ingresos reales.
- **Migración Flyway V005**: Tablas `orden_historial_estados`, `orden_observaciones`.

### Security

- **Edición segura**: Órdenes ENTREGADO o CANCELADO no permiten modificación ni eliminación.
- **Validación de cierre**: No se puede entregar sin cliente, vehículo o al menos un servicio/producto.

---

## [1.2.0] - 2026-06-09

### Security (Corregido)

- **JWT Authentication**: Reemplazado `OncePerRequestFilter` por `SecurityContextRepository`. Eliminado workaround `.permitAll()`.
- **RestAuthenticationEntryPoint/RestAccessDeniedHandler**: Handlers 401/403 con JSON uniforme.

### Added

- **Health Check** (`/actuator/health`), **Logs con rotación**, **Variables de entorno**, **Docker Compose expandido** (4 servicios).
- **Pruebas de integración**: 11 tests en 4 clases.

---

## [1.1.0] - 2026-06-09

### Changed

- **BaseEntity unificada**, **Soft delete consistente**, **EAGER → LAZY**, **N+1 resuelto**.
- **Descuento de inventario** automático al completar orden.

---

## [1.0.0] - 2026-06-09

### Added

- **Proyecto base**: Backend Spring Boot (12 módulos), Frontend React + TypeScript, Infraestructura Docker.
