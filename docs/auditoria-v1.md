# Auditoría Técnica v1 — SERVITECA-SAS

> **Fecha:** 2026-06-09
> **Alcance:** Backend Spring Boot, modelo de datos, seguridad, API REST
> **Versión auditada:** 1.0.0-SNAPSHOT

---

## Resumen Ejecutivo

| Dimensión | Hallazgos críticos | Hallazgos mayores | Hallazgos menores |
|---|---|---|---|
| Arquitectura | 0 | 1 | 3 |
| Modelo de datos | 0 | 4 | 5 |
| Orden de Trabajo | 0 | 2 | 3 |
| Auditoría / Soft Delete | 0 | 2 | 1 |
| Seguridad | 1 | 2 | 2 |
| API / REST | 0 | 2 | 4 |
| Rendimiento | 1 | 2 | 1 |
| Escalabilidad | 0 | 3 | 2 |
| Convenciones | 0 | 1 | 4 |
| **Total** | **2** | **19** | **25** |

---

## 1. Arquitectura y Paquetes

### 1.1 Estructura general — ACEPTABLE

El monolito modular está bien organizado:
- `shared/` para capa transversal (ApiResponse, BaseEntity, handlers)
- Módulos por dominio (cliente, vehiculo, usuario, orden, etc.)
- Inyección por constructor (correcto)
- Separación controller → service → repository → entity (correcta)

### 🔴 Hallazgo MA-01 (Mayor): OrdenTrabajoService tiene alta cohesión forzada

**Archivo:** `ordenes-trabajo/service/OrdenTrabajoService.java`

El servicio de ordenes importa **8 repositorios** directamente:

```
ClienteRepository, VehiculoRepository, UsuarioRepository,
ServicioRepository, ProductoRepository, OrdenTrabajoRepository,
OrdenServicioRepository, OrdenProductoRepository
```

Esto indica que la orden de trabajo es el agregado raíz del sistema, lo cual es correcto a nivel DDD. Sin embargo, el acoplamiento es alto y rompe el principio de **segregación de interfaces**. Si algún repositorio cambia su firma, este servicio se ve afectado.

**Sugerencia:** Crear servicios/fachadas intermedias (`ClienteService`, `VehiculoService`) e inyectarlas en lugar de los repositorios directamente. Esto ya se hace parcialmente (se inyecta `UsuarioService`), pero el resto son repositorios.

### 🟡 Hallazgo MI-01 (Menor): UsuarioService no está siendo usado en autenticación

**Archivo:** `seguridad/jwt/JwtAuthenticationFilter.java`

El filtro JWT usa `UserDetailsService` genérico de Spring Security, no el `UsuarioService` del módulo. Si se añadiera lógica personalizada (bloqueo de cuenta, último acceso), no se reflejaría.

### 🟡 Hallazgo MI-02 (Menor): AuthService referencia `"OPERADOR"` como string hardcodeado

**Archivo:** `seguridad/auth/AuthService.java`

La línea `rolRepository.findByNombre("OPERADOR")` usa un string literal. Si se renombra el rol en BD, se rompe la funcionalidad.

---

## 2. Modelo de Datos

### 2.1 Inconsistencia de herencia de BaseEntity

**Archivos involucrados:**
- Extienden BaseEntity: `Cliente`, `Vehiculo`, `Proveedor`, `Usuario`, `OrdenTrabajo`
- NO extienden BaseEntity: `Categoria`, `Servicio`, `Producto`, `Rol`, `Inventario`, `MovimientoInventario`, `OrdenServicio`, `OrdenProducto`

**Consecuencias:**
- Servicio, Producto, Categoria, Rol **no tienen** `fecha_creacion`, `fecha_modificacion`, `creado_por`, `modificado_por`
- No se puede auditar quién creó o modificó un servicio, producto o rol
- No hay soft delete unificado: Categoria/Producto tienen su propio `activo` pero sin auditoría

### 🔴 Hallazgo CR-01 (Crítico): Estrategia de eliminación inconsistente

| Entidad | Estrategia DELETE | Evidencia |
|---|---|---|
| Cliente | Soft delete (`activo=false`) | `ClienteService.delete()` |
| Usuario | Soft delete | `UsuarioService.delete()` |
| Rol | **Hard delete** | `RolService.eliminar()` → `rolRepository.deleteById()` |
| Categoria | Soft delete | `CategoriaService.eliminar()` |
| Servicio | Soft delete | `ServicioService.eliminar()` |
| Producto | Soft delete | `ProductoService.eliminar()` |
| Proveedor | Soft delete | `ProveedorService.eliminar()` |

**RolService.eliminar() hace hard delete** (`rolRepository.deleteById(id)`), mientras que el resto del sistema usa soft delete. Esto es un error: si se elimina un rol con usuarios asociados, se pierde la integridad referencial.

**Riesgo:** Alto. Una mala eliminación puede dejar huérfanos o causar `ForeignKeyConstraintViolationException` si hay usuarios asignados a ese rol.

### 🟡 Hallazgo MA-02 (Mayor): Vehículo-Cliente: la relación no soporta historial

**Archivo:** `vehiculo/entity/Vehiculo.java`

```
@ManyToOne @JoinColumn(name = "cliente_id")
private Cliente cliente;
```

Un vehículo apunta a un solo cliente. Si cambia de propietario, se pierde el historial. Las órdenes anteriores mostrarán el dueño actual, no el que tenía al momento de la orden. La buena noticia es que `OrdenTrabajo` tiene su propia referencia a `Cliente`, por lo que la orden guarda ese snapshot — pero la relación `Vehiculo → Cliente` se pierde.

**Sugerencia:** Agregar tabla `vehiculo_historial_propietario` (vehiculo_id, cliente_id, fecha_desde, fecha_hasta) para mantener trazabilidad.

### 🟡 Hallazgo MA-03 (Mayor): `numero_orden` puede exceder VARCHAR(20)

**Archivo:** `orden/entity/OrdenTrabajo.java`

```
@Column(name = "numero_orden", unique = true, length = 20)
private String numeroOrden;
```

Formato actual: `"ORD-" + System.currentTimeMillis()` que genera: `ORD-1749494400000` → 18 caracteres. A partir de 2286 los timestamps de 13 dígitos darían 21 caracteres. Riesgo bajo hoy, pero es una bomba de tiempo.

Además, **`System.currentTimeMillis()` en alta concurrencia** puede generar duplicados en la misma fracción de milisegundo si dos hilos lo obtienen simultáneamente.

### 🟡 Hallazgo MI-03 (Menor): `stock_minimo` duplicado

- `Producto.stockMinimo` existe en la entidad Producto
- `Inventario.cantidadMinima` cumple la misma función para el inventario

Hay dos fuentes de verdad para el stock mínimo. ¿Cuál prevalece? Si se actualiza `Producto.stockMinimo`, el inventario no se entera.

### 🟡 Hallazgo MI-04 (Menor): `MovimientoInventario` no registra precio

No hay campo `precio_unitario` o `costo_total` en `MovimientoInventario`. No se puede calcular el costo de las entradas/salidas de inventario. Para contabilidad/facturación futura, esto será necesario.

### 🟡 Hallazgo MI-05 (Menor): Categoria no tiene código único

`Categoria` solo tiene `nombre` (sin unique). No hay manera de identificar una categoría por código.

---

## 3. Módulo Orden de Trabajo (Núcleo)

### 3.1 Flujo de estados — ACEPTABLE

La máquina de estados en `EstadoOrdenTrabajo` es correcta:
```
EN_ESPERA → EN_PROCESO → COMPLETADA
EN_PROCESO → CANCELADA
```

### 🔴 Hallazgo CR-02 (Crítico): N+1 en `toPagedResponse()`

**Archivo:** `orden/service/OrdenTrabajoService.java` (~línea 118)

```java
public PagedResponse<OrdenTrabajoResponse> toPagedResponse(Page<OrdenTrabajo> ordenes) {
    return new PagedResponse<>(
        ordenes.getContent().stream().map(this::toResponse).collect(Collectors.toList()), ...
    );
}
```

`toResponse()` hace por cada orden:
1. `ordenServicioRepository.findByOrdenTrabajoId(orden.getId())` → 1 query
2. `ordenProductoRepository.findByOrdenTrabajoId(orden.getId())` → 1 query

Para una página de 20 órdenes: **1 (page) + 20 + 20 = 41 queries**. Esto es un problema N+1 severo.

**Solución inmediata:** Usar `JOIN FETCH` en el query de paginación o usar `@EntityGraph`.

### 🟡 Hallazgo MA-04 (Mayor): No se descuenta inventario al crear/completar orden

Al crear una orden con productos, el stock en `inventario` no se descuenta. Tampoco se crea un `MovimientoInventario` de tipo salida.

Esto significa que:
- El inventario es puramente informativo
- Se puede vender un producto sin stock
- No hay trazabilidad de salidas de inventario

**Solución:** Añadir lógica en `create()` para descontar stock y crear `MovimientoInventario` de tipo `SALIDA`.

### 🟡 Hallazgo MA-05 (Mayor): No hay endpoint para modificar ítems de una orden

No existe `PUT /api/ordenes/{id}/servicios` ni `PUT /api/ordenes/{id}/productos`. Una vez creada la orden con sus servicios y productos, no se pueden modificar. Solo se puede cambiar el estado vía PATCH.

### 🟡 Hallazgo MI-06 (Menor): OrdenTrabajoRequest no valida ítems vacíos

El `@Valid` en el controller no valida que `servicios` y `productos` no estén vacíos simultáneamente. Se podría crear una orden sin servicios ni productos.

### 🟡 Hallazgo MI-07 (Menor): `CambioEstadoRequest` solo valida `@NotBlank estado`

El campo `observaciones` es `@NotBlank` pero el DTO recibe `observaciones` nullable. Si alguien cambia estado sin observaciones, la validación lo permite pero la lógica espera observaciones.

### 🟡 Hallazgo MI-08 (Menor): No hay recalculo de totales

No hay endpoint para recalcular `totalServicios` y `totalProductos` de una orden. Estos son inmutables tras la creación.

---

## 4. Auditoría y Soft Delete

### 4.1 BaseEntity — ACEPTABLE

```
@Column(name = "fecha_creacion")  @CreatedDate
@Column(name = "fecha_modificacion") @LastModifiedDate
@Column(name = "creado_por") @CreatedBy
@Column(name = "modificado_por") @LastModifiedBy
@Column(name = "activo")  (default true)
```

JPA Auditing con `@EnableJpaAuditing` en la aplicación. Correcto.

### 🟡 Hallazgo MA-06 (Mayor): Soft delete sin `fecha_eliminacion` ni `eliminado_por`

Solo se usa un booleano `activo`. No se sabe:
- Cuándo se eliminó
- Quién eliminó
- No se puede restaurar fácilmente (no hay endpoint de restaurar)

**Sugerencia:** Agregar `fecha_eliminacion` y `eliminado_por` a BaseEntity.

### 🟡 Hallazgo MA-07 (Mayor): Las tablas detalle no tienen auditoría

`OrdenServicio` y `OrdenProducto` no extienden `BaseEntity`. No tienen `fecha_creacion`, `creado_por`, etc. Para un sistema de facturación, esto puede ser problemático.

### 🟡 Hallazgo MI-09 (Menor): `@CreatedBy` y `@LastModifiedBy` pueden ser null

**Archivo:** `shared/audit/AuditorAwareImpl.java`

```java
@Override
public Optional<String> getCurrentAuditor() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null || !authentication.isAuthenticated()) {
        return Optional.of("SYSTEM");
    }
    return Optional.ofNullable(authentication.getName());
}
```

Si `authentication.getName()` retorna null, se guarda null en BD.

---

## 5. Seguridad

### 🔴 Hallazgo CR-03 (Crítico): JWT Secret hardcodeado en application.yml

**Archivo:** `application.yml`

```yaml
jwt:
  secret: "586E3272357538782F413F4428472B4B6250655368566B597033733676397924"
  expiration: 86400000
```

Este es el secreto con el que se firman todos los JWTs. Está en texto plano en el repositorio. Cualquiera con acceso al código puede:
- Generar tokens válidos para cualquier usuario
- Acceder al sistema sin autenticación
- Suplantar identidades

**Solución:** Usar variable de entorno `JWT_SECRET` y cargarla en tiempo de ejecución.

### 🟡 Hallazgo CR-04 (Crítico): CSRF deshabilitado sin alternativa

```java
http.csrf(csrf -> csrf.disable())
```

Si bien es común en APIs REST con JWT, debe documentarse que se confía en el token JWT como mecanismo anti-CSRF. Si hay un XSS, el atacante podría hacer peticiones en nombre del usuario.

### 🟡 Hallazgo MA-08 (Mayor): No hay rate limiting en `/api/auth/login`

No hay protección contra ataques de fuerza bruta en el endpoint de login. Un atacante podría probar combinaciones de usuario/contraseña indefinidamente.

**Sugerencia:** Implementar Spring Boot + Resilience4j o un filtro personalizado de rate limiting.

### 🟡 Hallazgo MA-09 (Mayor): Refresh tokens no son revocables

No hay mecanismo para invalidar un refresh token robado. Si alguien obtiene un refresh token, puede generar nuevos access tokens hasta que expire.

### 🟡 Hallazgo MI-10 (Menor): `@PreAuthorize` no se usa

Los permisos se manejan exclusivamente a nivel de ruta en `SecurityConfig` con `requestMatchers`. No hay anotaciones `@PreAuthorize` a nivel de método. Esto hace que los permisos estén centralizados en una sola clase, lo cual es frágil.

### 🟡 Hallazgo MI-11 (Menor): Roles se cargan con FetchType.EAGER

**Archivo:** `usuario/entity/Usuario.java`

```java
@ManyToOne(fetch = FetchType.EAGER)
@JoinColumn(name = "rol_id")
private Rol rol;
```

Cada vez que se carga un usuario (incluso para login), se hace un JOIN/query adicional para cargar el rol. Si bien es solo uno, `EAGER` es considerado anti-patrón.

---

## 6. API REST

### 6.1 Endpoints — CONSOLIDADO

| Módulo | Endpoints | Estilo |
|---|---|---|
| Auth | POST /api/auth/login, POST /api/auth/register | REST |
| Clientes | CRUD /api/clientes | REST |
| Usuarios | CRUD /api/usuarios | REST |
| Roles | CRUD /api/roles | REST |
| Proveedores | CRUD /api/proveedores | REST |
| Categorías | CRUD /api/categorias | REST |
| Servicios | CRUD /api/servicios | REST |
| Productos | CRUD /api/productos | REST |
| Vehículos | CRUD /api/vehiculos + GET /api/vehiculos/cliente/{id} | REST |
| Ordenes Trabajo | GET/POST /api/ordenes + PATCH /api/ordenes/{id}/estado | REST |
| Inventario | GET/POST/PUT /api/inventario | REST |
| Dashboard | GET /api/dashboard/* | REST |

### 🟡 Hallazgo MA-10 (Mayor): DashboardService tiene método con valor hardcodeado

**Archivo:** `dashboard/service/DashboardService.java`

```java
public Long ingresosHoy() {
    return 0L; // TODO: implementar
}
```

El endpoint `/api/dashboard/ingresos-hoy` siempre retorna 0. No se implementó.

### 🟡 Hallazgo MA-11 (Mayor): `GET /api/vehiculos/cliente/{id}` no está paginado

**Archivo:** `vehiculo/controller/VehiculoController.java`

Un cliente puede tener muchos vehículos. Sin paginación, una consulta podría devolver cientos o miles de registros.

### 🟡 Hallazgo MI-12 (Menor): Códigos HTTP no siempre son precisos

- `POST /api/roles` → retorna `200 OK` en lugar de `201 Created` (revisar RolController)
- `PATCH /api/ordenes/{id}/estado` → retorna `200 OK` (correcto)

### 🟡 Hallazgo MI-13 (Menor): `ApiResponse` sin paginación en algunos endpoints

Los endpoints de listado de Servicios y Productos no usan `PagedResponse`, devuelven listas planas.

### 🟡 Hallazgo MI-14 (Menor): No hay `HEAD` ni `OPTIONS` implementados

No hay endpoints para consultar metadatos de recursos.

---

## 7. Rendimiento

### 🟡 Hallazgo MA-12 (Mayor): OrdenTrabajoRepository no usa `JOIN FETCH`

**Archivo:** `orden/repository/OrdenTrabajoRepository.java`

```java
Page<OrdenTrabajo> findByClienteId(Long clienteId, Pageable pageable);
```

No hay `JOIN FETCH` para `cliente`, `vehiculo`, `usuario`. Cada orden carga estas relaciones perezosamente, generando queries N+1 adicionales.

### 🟡 Hallazgo MA-13 (Mayor): DashboardService hace múltiples COUNT

```java
public Long totalClientes() { return clienteRepository.count(); }
public Long totalVehiculos() { return vehiculoRepository.count(); }
// ...
```

Cada método es un COUNT individual. Para un dashboard con 10 indicadores, se hacen 10 queries. Con muchos registros, esto puede volverse lento.

### 🟡 Hallazgo MI-15 (Menor): Faltan índices explícitos en columnas de búsqueda

| Columna | Búsqueda frecuente por... | ¿Índice? |
|---|---|---|
| `clientes.numero_documento` | Búsqueda exacta | No |
| `servicios.nombre` | LIKE `%texto%` | No |
| `productos.nombre` | LIKE `%texto%` | No |
| `ordenes_servicios.orden_id` | FK lookup | Sí (JPA lo genera) |
| `ordenes_productos.orden_id` | FK lookup | Sí (JPA lo genera) |
| `movimientos_inventario.producto_id` | FK lookup | Sí (JPA lo genera) |

Las búsquedas LIKE en servicios y productos no tienen índices. Para catálogos pequeños no es problema, pero con >10K registros el rendimiento se degrada.

### 🟡 Hallazgo MI-16 (Menor): UsuarioService.findAll() no está paginado

Retorna `List<Usuario>`, no `Page<Usuario>`.

---

## 8. Escalabilidad (Multiempresa)

### 🟡 Hallazgo MA-14 (Mayor): No hay campo `empresa_id` en ninguna entidad

Para convertir a SaaS multiempresa, todas las entidades requerirían un `empresa_id`. Las que heredan de `BaseEntity` lo heredarían automáticamente, pero:
- Servicio, Producto, Rol, Categoria, Inventario, MovimientoInventario NO heredan de BaseEntity → requerirían modificación manual
- Se necesitaría un filtro global (ej. Spring Filter o Hibernate multi-tenant)

### 🟡 Hallazgo MA-15 (Mayor): No hay campo `sede_id`

Similar a multiempresa. No hay soporte para múltiples sedes de una misma empresa.

### 🟡 Hallazgo MI-17 (Menor): `SharedEntity` como alternativa de BaseEntity

Si se crea `SharedEntity extends BaseEntity` con `empresaId`, solo las entidades que hereden de ella tendrían multiempresa. Las que heredan directo de BaseEntity hoy (Cliente, Vehiculo, etc.) ya tendrían el soporte.

### 🟡 Hallazgo MI-18 (Menor): JWT sin claim de empresa

Si se implementa multiempresa, el JWT debería incluir `empresaId` como claim para evitar consultas adicionales.

---

## 9. Convenciones y Estilo

### 🟡 Hallazgo MI-19 (Menor): Mezcla de español e inglés

- Entidades y tablas en español: `Cliente`, `Vehiculo`, `OrdenTrabajo`, `MovimientoInventario`
- Anotaciones y métodos en inglés: `findByClienteId()`, `deleteById()`
- DTOs en español/inglés: `ClienteRequest` vs `RegisterRequest`

No es un error, pero sería bueno establecer una convención.

### 🟡 Hallazgo MA-16 (Mayor): Validators no existen para todos los módulos

- Existen `@Valid` en ClienteRequest, ProveedorRequest, InventarioRequest, OrdenTrabajoRequest
- No existen validators específicos para ServicioRequest, ProductoRequest, VehiculoRequest (solo `@NotBlank`, `@NotNull` básicos)

### 🟡 Hallazgo MI-20 (Menor): Algunos controllers tienen logs, otros no

- `OrdenTrabajoController` tiene `log.info()` en cada método
- `ServicioController`, `ProductoController`, `VehiculoController` no tienen logs

### 🟡 Hallazgo MI-21 (Menor): Código duplicado en mapeos

```java
// En varios services:
.setActivo(true)  // o entidad.getActivo()
// Esto se repite en varios mappers
```

### 🟡 Hallazgo MI-22 (Menor): `@Transactional` no está en todos los servicios

- `OrdenTrabajoService.create()` usa `@Transactional` — correcto
- `AuthService.register()` usa `@Transactional` — correcto
- `MovimientoInventarioService.crear()` no usa `@Transactional` explícito — Riesgo bajo porque JPA repository.save() maneja una transacción, pero si hubiera múltiples saves, no habría rollback atómico.

---

## 10. Resumen de Prioridades

### Críticos (requieren acción inmediata)

| ID | Hallazgo | Archivo | Acción |
|---|---|---|---|
| CR-01 | Hard delete en RolService (inconsistencia) | `roles/RolService.java:42` | Cambiar a soft delete |
| CR-02 | N+1 en toPagedResponse de OrdenTrabajo | `orden/OrdenTrabajoService.java:118` | Usar JOIN FETCH o EntityGraph |
| CR-03 | JWT Secret hardcodeado | `application.yml` | Mover a variable de entorno |
| CR-04 | CSRF deshabilitado sin alternativa | `config/SecurityConfig.java` | Documentar dependencia en JWT |

### Mayores (requieren acción en esta iteración)

| ID | Hallazgo | Acción |
|---|---|---|
| MA-01 | Alta cohesión en OrdenTrabajoService | Inyectar servicios en lugar de repositorios |
| MA-02 | Vehículo-Cliente sin historial | Agregar tabla de historial de propietarios |
| MA-03 | numero_orden puede exceder límite | Cambiar a BIGINT autoincremental o UUID |
| MA-04 | No se descuenta inventario | Agregar descuento al crear/completar orden |
| MA-05 | No hay endpoint para modificar items | Agregar PUT para servicios/productos de una orden |
| MA-06 | Soft delete sin fecha/usuário | Agregar fecha_eliminacion, eliminado_por |
| MA-07 | Tablas detalle sin auditoría | Hacer que hereden BaseEntity |
| MA-08 | No hay rate limiting en login | Agregar Resilience4j o filtro |
| MA-09 | Refresh tokens no revocables | Agregar tabla de tokens activos |
| MA-10 | ingresosHoy() hardcodeado en 0 | Implementar query real |
| MA-11 | Vehículos por cliente sin paginación | Agregar Pageable |
| MA-12 | JOIN FETCH faltante en repositorios | Agregar a OrdenTrabajoRepository |
| MA-13 | Dashboard con múltiples COUNT | Optimizar con una sola query |
| MA-14 | Sin campo empresa_id | Planificar para futuro SaaS |
| MA-15 | Sin campo sede_id | Planificar para futuro |
| MA-16 | Validators faltantes | Agregar a Servicio, Producto, Vehiculo |

### Menores (mejora continua)

| ID | Hallazgo |
|---|---|
| MI-01 | UsuarioService no usado en auth filter |
| MI-02 | "OPERADOR" hardcodeado |
| MI-03 | stock_minimo duplicado |
| MI-04 | MovimientoInventario sin precio |
| MI-05 | Categoria sin código único |
| MI-06 | Orden sin ítems vacía |
| MI-07 | CambioEstadoRequest nullable |
| MI-08 | Sin recalculo de totales |
| MI-09 | AuditorAware puede retornar null |
| MI-10 | @PreAuthorize no usado |
| MI-11 | FetchType.EAGER en Usuario.rol |
| MI-12 | Código HTTP 200 vs 201 |
| MI-13 | ApiResponse sin paginación |
| MI-14 | HEAD/OPTIONS no implementados |
| MI-15 | Índices faltantes en LIKE |
| MI-16 | UsuarioService.findAll() sin paginación |
| MI-17 | SharedEntity para multiempresa |
| MI-18 | JWT sin claim empresa |
| MI-19 | Mezcla español/inglés |
| MI-20 | Logs inconsistentes |
| MI-21 | Código duplicado en mapeos |
| MI-22 | @Transactional faltante en algunos servicios |

---

## Checklist de Cumplimiento

| Aspecto | Estado |
|---|---|
| RESTful | ✅ Parcial (faltan HATEOAS, falta PUT/PATCH en algunos) |
| Validación de entrada | ✅ Parcial (faltan validators específicos) |
| Manejo de errores | ✅ Global con `GlobalExceptionHandler` |
| Paginación | ✅ Parcial (Vehículos, Usuarios sin paginar) |
| Seguridad JWT | ✅ Implementado (con secret hardcodeado) |
| Auditoría | ✅ Parcial (solo entidades que heredan BaseEntity) |
| Soft Delete | ✅ Parcial (inconsistente con RolService) |
| Transacciones | ✅ Parcial |
| Logging | ⚠️ Inconsistente |
| Documentación API | ❌ No hay Swagger/OpenAPI |
| Tests | ❌ No se encontraron en el código base |
| Docker | ❌ No se encontró Dockerfile |

---

## Notas Finales

1. **Calidad general:** Media. El sistema es funcional y cubre los casos de uso principales, pero tiene varias deudas técnicas que deben abordarse antes de producción.
2. **Riesgo principal:** La seguridad (JWT secret hardcodeado) y el rendimiento (N+1 en OrdenTrabajo) son los dos problemas más urgentes.
3. **Deuda técnica:** La inconsistencia de BaseEntity y el hard delete en RolService son issues fáciles de corregir que mejoran significativamente la calidad del código.
4. **Próximos pasos sugeridos:**
   - Semana 1: Corregir CR-01, CR-02, CR-03, CR-04
   - Semana 2: Corregir MA-04, MA-06, MA-07, MA-10, MA-11
   - Semana 3: Agregar tests unitarios y de integración
   - Semana 4: Agregar Swagger/OpenAPI y mejorar documentación
5. **Recomendación:** Migrar a variables de entorno para secrets antes de cualquier despliegue.
