# Roadmap

## Fase 1 - MVP (Actual)

**Objetivo**: Sistema base funcional para operación diaria de una serviteca.

| Funcionalidad | Estado | Prioridad |
|--------------|--------|-----------|
| Autenticación JWT (login, refresh, register) | ✅ Completo | Alta |
| CRUD Usuarios | ✅ Completo | Alta |
| CRUD Roles | ✅ Completo | Alta |
| Dashboard básico | ✅ Completo | Alta |
| CRUD Clientes (con paginación y búsqueda) | ✅ Completo | Alta |
| CRUD Vehículos | ✅ Completo | Alta |
| CRUD Servicios | ✅ Completo | Alta |
| CRUD Categorías | ✅ Completo | Alta |
| CRUD Productos | ✅ Completo | Alta |
| CRUD Proveedores | ✅ Completo | Alta |
| Inventario + Movimientos | ✅ Completo | Alta |
| Órdenes de Trabajo (crear, cambiar estado, detalle) | ✅ Completo | Alta |
| Máquina de estados + Historial + Observaciones | ✅ Completo | Alta |
| Caja (apertura, cierre, movimientos) | ✅ Completo | Alta |
| Pagos (registrar, anular, estado financiero) | ✅ Completo | Alta |
| Métodos de pago configurables | ✅ Completo | Alta |
| Dashboard financiero | ✅ Completo | Alta |

---

## Fase 2 - CRM Inteligente y Fidelización

**Objetivo**: Conocer profundamente a cada cliente, su historial, comportamiento y valor.

| Funcionalidad | Estado |
|--------------|--------|
| Perfil inteligente del cliente (métricas automáticas) | ✅ Completo |
| Clasificación automática (NUEVO/FRECUENTE/VIP/INACTIVO) | ✅ Completo |
| Historial económico del cliente | ✅ Completo |
| Historial completo de órdenes por cliente | ✅ Completo |
| Notas internas inmutables | ✅ Completo |
| Clientes inactivos (6/12 meses) | ✅ Completo |
| Ranking de clientes (invertido, visitas, servicios, vehículos) | ✅ Completo |
| Próximos mantenimientos del cliente | ✅ Completo |
| Dashboard CRM | ✅ Completo |
| Migración Flyway V009 | ✅ Completo |
| Pruebas de integración CRM (10 tests) | ✅ Completo |

---

## Fase 3 - Reportes y Facturación

**Objetivo**: Mejorar la toma de decisiones y formalizar los procesos de cobro.

| Funcionalidad | Estado |
|--------------|--------|
| Reportes avanzados (Excel/PDF) | ⏳ Pendiente |
| Facturación electrónica básica | ⏳ Pendiente |
| Auditoría de cambios en entidades | ✅ Completo (v1.8.0) |
| Módulo de configuración del sistema | ✅ Completo (v1.8.0) |
| Exportación de datos | ⏳ Pendiente |
| Filtros avanzados en listados | ⏳ Pendiente |

---

## Fase 4 - Comunicación y Agenda

**Objetivo**: Mejorar la comunicación con el cliente y optimizar la agenda del taller.

| Funcionalidad | Estado |
|--------------|--------|
| Integración con WhatsApp (notificaciones) | ⏳ Pendiente |
| Recordatorios automáticos de servicio | ⏳ Pendiente |
| Agenda/Citas (calendarización) | ⏳ Pendiente |
| Portal del cliente (consultar estado de orden) | ⏳ Pendiente |
| Notificaciones push | ⏳ Pendiente |
| Historial de comunicación con cliente | ⏳ Pendiente |

---

## Fase 5 - Inteligencia y Automatización

**Objetivo**: Agregar capacidades predictivas y de automatización para mejorar la eficiencia.

| Funcionalidad | Estado |
|--------------|--------|
| Dashboard inteligente con gráficas | ⏳ Pendiente |
| Predicción de inventario (stock óptimo) | ⏳ Pendiente |
| Sistema de fidelización (puntos, descuentos) | ⏳ Pendiente |
| Predicción de servicios recurrentes | ⏳ Pendiente |
| Automatización de tareas repetitivas | ⏳ Pendiente |
| Detección de anomalías en órdenes | ⏳ Pendiente |

---

## Fase 6 - Multiempresa y SaaS

**Objetivo**: Convertir el sistema en una plataforma SaaS multiinquilino.

| Funcionalidad | Estado |
|--------------|--------|
| Arquitectura multiempresa | ✅ Completo (v1.9.0) |
| Arquitectura multisede | ✅ Completo (v1.9.0) |
| Transferencias entre sedes | ✅ Completo (v1.9.0) |
| Dashboard consolidado por empresa | ✅ Completo (v1.9.0) |
| Aislamiento de datos (empresa_id/sede_id) | ✅ Completo (v1.9.0) |
| Comparación de métricas entre sedes | ⏳ Pendiente |
| Roles y permisos granulares por empresa | ⏳ Pendiente |
| Facturación por uso (SaaS) | ⏳ Pendiente |
| Onboarding de nuevas empresas | ⏳ Pendiente |
| Marketplace de servicios | ⏳ Pendiente |

---

## Criterios de priorización

1. **Valor de negocio**: Lo que más impacto tiene en la operación diaria.
2. **Dependencias técnicas**: Lo que se necesita antes de poder construir otra cosa.
3. **Esfuerzo vs. impacto**: Funcionalidades de bajo esfuerzo y alto impacto primero.
4. **Feedback de usuarios**: Lo que los operadores del taller soliciten.
