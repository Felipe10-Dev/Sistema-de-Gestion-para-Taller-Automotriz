# Funcionalidades Futuras

Este documento registra todas las ideas y requerimientos identificados para versiones futuras del sistema. Sirve como backlog para no perder ninguna idea.

---

## Reportes y Análisis

- [ ] Reportes exportables (PDF, Excel) de órdenes completadas.
- [ ] Reporte de ingresos por período (día, semana, mes).
- [ ] Reporte de servicios más solicitados.
- [ ] Reporte de productos más utilizados.
- [ ] Reporte de técnicos (tiempo promedio por servicio, órdenes completadas).
- [ ] Reporte de clientes frecuentes.
- [ ] Dashboard avanzado con gráficas (Chart.js o Recharts).
- [ ] Indicadores de rendimiento del taller (KPI).

---

## Facturación

- [ ] Módulo de facturación electrónica (DIAN - Colombia).
- [ ] Numeración de facturas.
- [ ] Soporte para distintos tipos de documento (factura, nota crédito, nota débito).
- [ ] Integración con medios de pago (datafono, transferencias).
- [ ] Historial de pagos por orden.
- [ ] Estados de cuenta de clientes.

---

## Comunicación con el cliente

- [ ] Integración con WhatsApp Business API.
- [ ] Envío automático de notificaciones al cambiar estado de la orden.
- [ ] Recordatorios de servicio programado (cambio de aceite, revisión).
- [ ] Recordatorios de citas agendadas.
- [ ] Encuesta de satisfacción post-servicio.
- [ ] Seguimiento post-servicio (llamada o mensaje a los días).

---

## Portal del Cliente

- [ ] Portal web donde el cliente pueda consultar el estado de su orden.
- [ ] Historial de servicios realizados a su vehículo.
- [ ] Descarga de facturas.
- [ ] Agenda de citas en línea.
- [ ] Notificaciones push al celular.
- [ ] QR para acceso rápido a la orden/vehículo.

---

## Agenda y Citas

- [ ] Calendario de citas programadas.
- [ ] Asignación de horarios por técnico.
- [ ] Vista de agenda por día/semana/mes.
- [ ] Confirmación de cita automática.
- [ ] Gestión de capacidad del taller.

---

## Inteligencia Artificial

- [ ] Predicción de servicios próximos basada en historial del vehículo.
- [ ] Predicción de inventario óptimo (qué productos comprar y cuándo).
- [ ] Detección de anomalías en órdenes (precios inusuales, tiempos excesivos).
- [ ] Recomendación de servicios adicionales basada en kilometraje y modelo.
- [ ] Chatbot para consultas de clientes.
- [ ] Análisis de sentimiento en encuestas.

---

## Automatización

- [ ] Flujo automático de descuento de inventario al completar orden.
- [ ] Asignación automática de técnico por especialidad y disponibilidad.
- [ ] Generación automática de orden de compra cuando stock baja del mínimo.
- [ ] Email automático con resumen de servicios al completar orden.

---

## Fidelización

- [ ] Programa de puntos por servicios.
- [ ] Descuentos por cliente frecuente.
- [ ] Cupones de descuento automáticos.
- [ ] Tarjeta de fidelización digital.
- [ ] Promociones por cumpleaños del cliente o del vehículo.

---

## Operativos

- [ ] Fotos de recepción del vehículo (evidencia de estado).
- [ ] Firma digital del cliente al recibir el vehículo.
- [ ] Check-list de recepción (niveles, luces, carrocería).
- [ ] Órdenes de compra a proveedores.
- [ ] Múltiples métodos de pago por orden.
- [ ] Módulo de garantías (seguimiento a servicios garantizados).

---

## Técnicos

- [ ] App móvil para técnicos (ver órdenes asignadas, registrar avances).
- [ ] Tiempo real de trabajo por servicio.
- [ ] Notas internas del técnico visibles solo internamente.
- [ ] Solicitud de aprobación al cliente para servicios adicionales.

---

## Multiempresa y Multisede (Fase 5)

- [ ] Arquitectura multiinquilino (una instancia sirve a múltiples empresas).
- [ ] Aislamiento de datos por empresa.
- [ ] Múltiples sedes por empresa.
- [ ] Dashboard comparativo entre sedes.
- [ ] Roles y permisos granulares por empresa.
- [ ] Personalización de marca (logo, colores) por empresa.
- [ ] Migración de inventario entre sedes.
- [ ] Facturación separada por sede.

---

## Infraestructura

- [ ] Dockerización completa (backend + frontend + BD en contenedores).
- [ ] CI/CD con GitHub Actions.
- [ ] Despliegue en la nube (AWS, Azure, GCP).
- [ ] Monitoreo y alertas.
- [ ] Backup automático de base de datos.
- [ ] CDN para assets estáticos.

---

## Calidad y Testing

- [ ] Tests unitarios (JUnit 5 + Mockito).
- [ ] Tests de integración (Spring Boot Test).
- [ ] Tests end-to-end (Cypress).
- [ ] Cobertura de código mínima del 80%.
- [ ] Pruebas de carga y rendimiento.

---

## Notas

- Las funcionalidades marcadas con `[ ]` están pendientes de priorizar.
- Este documento se actualiza cada vez que se identifica una nueva necesidad.
- No hay fechas compromiso para estas funcionalidades hasta que se asignen a una fase del roadmap.
