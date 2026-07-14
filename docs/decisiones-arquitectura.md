# Decisiones de Arquitectura

Todas las decisiones importantes del proyecto, con su contexto, alternativa considerada y justificación.

---

## Java 21 (LTS)

**Decisión**: Usar Java 21 LTS como lenguaje de programación del backend.

**Alternativas consideradas**: Java 17, Java 11, Kotlin.

**Justificación**:
- Java 21 es la versión LTS más reciente con soporte extendido.
- Nuevas features: pattern matching, records, sealed classes, virtual threads (Project Loom).
- Mejor rendimiento y seguridad que versiones anteriores.
- Ecosistema maduro y estable.
- Fácil encontrar desarrolladores con experiencia.

---

## Spring Boot 3

**Decisión**: Usar Spring Boot 3.x como framework de aplicaciones.

**Alternativas consideradas**: Quarkus, Micronaut, Jakarta EE.

**Justificación**:
- Ecosistema más maduro y ampliamente adoptado.
- Integración nativa con Spring Security, Spring Data JPA.
- Gran cantidad de recursos, documentación y comunidad.
- Compatibilidad con Java 21 y Jakarta EE.
- Fácil integración con Flyway, MapStruct, JWT.

---

## PostgreSQL

**Decisión**: Usar PostgreSQL como motor de base de datos.

**Alternativas consideradas**: MySQL, MariaDB, H2.

**Justificación**:
- Motor relacional robusto y maduro con características empresariales.
- Soporte nativo de JSON para consultas flexibles.
- Mejor rendimiento en consultas complejas y concurrentes.
- Licencia open source sin restricciones.
- Amplia adopción en producción.

---

## React + TypeScript

**Decisión**: Usar React 18 con TypeScript para el frontend.

**Alternativas consideradas**: Vue.js, Angular, Svelte.

**Justificación**:
- Mayor ecosistema de componentes y librerías.
- TypeScript proporciona tipado estático, mejorando la mantenibilidad.
- Tailwind CSS permite desarrollo rápido de UI consistente.
- Vite como bundler extremadamente rápido.
- Curva de aprendizaje menor que Angular; más estructura que Vue.

---

## JWT + Refresh Tokens

**Decisión**: Autenticación stateless con JWT y refresh tokens.

**Alternativas consideradas**: Sesiones server-side, OAuth2 completo, API Keys.

**Justificación**:
- Stateless: no requiere almacenamiento en servidor, fácil de escalar.
- Refresh tokens permiten renovar sesiones sin reautenticar.
- JWT contiene información del usuario (username, rol) sin consultar BD.
- Implementación ligera con la librería jjwt.

---

## Monolito Modular (NO microservicios)

**Decisión**: Construir el sistema como un monolito modular.

**Alternativas consideradas**: Microservicios con Spring Cloud.

**Justificación**:
- El dominio del problema no requiere escalamiento independiente.
- Complejidad operativa mínima (un solo artefacto, un solo deploy).
- Consistencia transaccional sin necesidad de Sagas o Event Sourcing.
- Facilidad de desarrollo y debugging.
- La modularidad interna permite extraer módulos a microservicios en el futuro si es necesario.

---

## Flyway

**Decisión**: Usar Flyway para migraciones de base de datos.

**Alternativas consideradas**: Liquibase, Hibernate ddl-auto=update, esquemas manuales.

**Justificación**:
- Migraciones versionadas y reproducibles.
- Integración nativa con Spring Boot.
- Rollback y control de versiones de esquema.
- SQL puro sin XML ni configuraciones adicionales.
- Ampliamente adoptado y probado en producción.

---

## MapStruct (vs Mappers manuales)

**Decisión**: Usar mappers manuales en lugar de MapStruct.

**Justificación**:
- Para un MVP con DTOs simples, los mappers manuales son más directos.
- Se evita la generación de código y la configuración del procesador de anotaciones.
- Los mappers manuales son explícitos y fáciles de debuggear.
- Si el proyecto crece, se puede migrar a MapStruct sin cambiar la interfaz.

---

## DTOs separados para Request y Response

**Decisión**: Crear DTOs de entrada (Request) y salida (Response) separados.

**Justificación**:
- Request contiene solo campos necesarios para creación/actualización.
- Response puede contener datos adicionales (nombres de relaciones, fechas).
- Principio de segregación de interfaces.
- Cambiar uno no afecta al otro.

---

## BaseEntity con auditoría

**Decisión**: Clase abstracta BaseEntity con campos de auditoría (fechaCreacion, fechaModificacion, creadoPor, modificadoPor, activo).

**Justificación**:
- Todos los módulos comparten campos de auditoría comunes.
- Spring Data JPA Auditing permite llenar automáticamente estos campos.
- `activo` permite borrado lógico en lugar de borrado físico.

### Refactorización v1.1 — BaseEntity unificado

**Decisión**: Todas las entidades del dominio ahora extienden BaseEntity.

**Entidades afectadas**: Categoria, Servicio, Producto, Rol, Inventario, MovimientoInventario, OrdenServicio, OrdenProducto.

**Cambios**:
- Se eliminaron campos `id` y `activo` duplicados de las entidades que los declaraban individualmente.
- Se agregaron `fecha_eliminacion` y `eliminado_por` a BaseEntity para trazabilidad completa de eliminaciones.
- Se agregó migración Flyway V003 con ALTER TABLE para las columnas faltantes.

**Beneficio**: Auditoría 100% consistente en todo el sistema. Sin excepciones.

---

## Paginación en listas grandes

**Decisión**: Usar `PagedResponse` con paginación Spring Data para listas de clientes, vehículos y órdenes.

**Justificación**:
- Evita cargar todos los registros en memoria.
- Mejora el rendimiento de la API.
- Consistente con el estándar REST.

---

## Respuesta unificada (ApiResponse)

**Decisión**: Todas las respuestas de la API envueltas en `ApiResponse<T>`.

**Justificación**:
- Formato consistente: `{ success, message, data, timestamp }`.
- El frontend puede manejar errores de forma genérica.
- Simplifica el manejo de estados en el cliente.

---

## Descuento de inventario al completar orden

**Decisión**: Al cambiar el estado de una orden a `COMPLETADA`, se descuenta automáticamente el stock del inventario.

**Justificación**:
- Garantiza que el inventario refleje el stock real disponible.
- Cada producto genera un `MovimientoInventario` de tipo `SALIDA` con referencia a la orden.
- Si no hay registro en inventario para un producto, simplemente se omite (no se bloquea la orden).

## Batch fetching para N+1

**Decisión**: En lugar de `JOIN FETCH` (que requiere relaciones `@OneToMany`), se usa `findByOrdenIdIn()` para cargar servicios y productos en lote.

**Justificación**:
- `OrdenTrabajo` no tiene relaciones `@OneToMany` a `OrdenServicio`/`OrdenProducto` (son tablas detalle con relación unidireccional ManyToOne).
- Una query `IN` con todos los IDs de la página reduce de N+1 queries a solo 2 adicionales.
- Las relaciones directas (cliente, vehículo, técnico) se cargan con `@EntityGraph` en la query de paginación.

## Estados de Orden de Trabajo

**Decisión**: Usar estados con máquina de estados simple (PENDIENTE → EN_PROCESO → COMPLETADA | CANCELADA).

**Justificación**:
- Flujo claro y predecible.
- Validación de transiciones en el backend.
- Fácil de entender para el usuario.
- Extensible en el futuro (agregar estados como REVISIÓN, ESPERANDO_PIEZAS, etc.).

---

## FetchType LAZY como default

**Decisión**: Todas las relaciones `@ManyToOne` y `@OneToOne` usan `FetchType.LAZY`.

**Justificación**:
- `FetchType.EAGER` puede causar queries innecesarias o problemas de rendimiento (N+1).
- LAZY forza al desarrollador a ser explícito sobre qué datos necesita cargar.
- `@EntityGraph` permite cargar relaciones específicas cuando se necesitan, sin perder la pereza por defecto.

**Excepción**: Ninguna. En v1.1 se cambió `Usuario.rol` de EAGER a LAZY.

## Inventario por productos

**Decisión**: Tabla `inventario` en relación OneToOne con `productos`, más tabla `movimientos_inventario` para trazabilidad.

**Justificación**:
- Cada producto tiene exactamente un registro de inventario.
- Los movimientos permiten auditoría completa de cambios de stock.
- Las salidas de inventario se registran al completar órdenes de trabajo.
