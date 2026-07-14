# Casos de Uso

## Registrar Cliente

**Actor**: Administrador, Operador

**Flujo principal**:
1. El usuario accede al módulo de Clientes.
2. Hace clic en "Nuevo Cliente".
3. Completa el formulario con:
   - Tipo de documento (CC, NIT, CE)
   - Número de documento
   - Nombre
   - Apellido
   - Teléfono (opcional)
   - Email (opcional)
   - Dirección (opcional)
4. Envía el formulario.
5. El sistema valida que el número de documento no exista.
6. El cliente se crea con estado activo.
7. Se muestra el mensaje "Cliente creado exitosamente".

**Validaciones**:
- Número de documento único.
- Campos obligatorios: tipo documento, número documento, nombre, apellido.

---

## Registrar Vehículo

**Actor**: Administrador, Operador

**Flujo principal**:
1. El usuario accede al módulo de Vehículos.
2. Hace clic en "Nuevo Vehículo".
3. Selecciona el cliente propietario.
4. Completa el formulario con:
   - Placa
   - Marca
   - Línea
   - Modelo
   - Color (opcional)
   - Cilindraje (opcional)
   - Tipo de vehículo (opcional)
5. Envía el formulario.
6. El sistema valida que la placa no exista.
7. El vehículo se crea con estado activo.

**Validaciones**:
- Placa única (se almacena en mayúsculas).
- Cliente debe existir.
- Campos obligatorios: placa, marca, línea, modelo, cliente.

---

## Crear Orden de Trabajo

**Actor**: Administrador, Operador

**Flujo principal**:
1. El usuario accede al módulo de Órdenes.
2. Hace clic en "Nueva Orden".
3. Selecciona el cliente y el vehículo (obligatorios).
4. Opcionalmente asigna un técnico.
5. Ingresa el kilometraje actual (opcional).
6. Agrega observaciones (opcional).
7. Agrega servicios desde el catálogo.
8. Agrega productos desde el inventario.
9. Envía la orden.
10. El sistema genera un número de orden único (`ORD-XXXXXXXX`).
11. La orden se crea con estado **PENDIENTE**.
12. Los totales se calculan automáticamente.

**Reglas de negocio**:
- Cliente y vehículo obligatorios.
- El número de orden se genera automáticamente con UUID.
- Los precios se copian del catálogo al momento de crear la orden.
- El total general = suma de subtotales de servicios + suma de subtotales de productos.

---

## Cambiar Estado de una Orden

**Actor**: Administrador, Operador, Técnico

**Flujo principal**:
1. El usuario accede al detalle de la orden.
2. Cambia el estado según las transiciones disponibles.
3. El sistema valida la transición.
4. Se registra el cambio en el historial con usuario, fecha y observación.
5. La orden se actualiza.

**Transiciones válidas**:

| Desde | Hacia |
|-------|-------|
| PENDIENTE | EN_DIAGNOSTICO, CANCELADO |
| EN_DIAGNOSTICO | EN_PROCESO, ESPERANDO_REPUESTOS, CANCELADO |
| EN_PROCESO | ESPERANDO_REPUESTOS, LISTO_PARA_ENTREGA, CANCELADO |
| ESPERANDO_REPUESTOS | EN_PROCESO, CANCELADO |
| LISTO_PARA_ENTREGA | ENTREGADO, CANCELADO |
| ENTREGADO | — (terminal) |
| CANCELADO | — (terminal) |

**Reglas**:
- No se permiten transiciones inválidas.
- Al pasar a **LISTO_PARA_ENTREGA** se valida que la orden tenga cliente, vehículo y al menos un servicio o producto.
- Al pasar a **ENTREGADO** se registra fecha de salida y se descuenta inventario.
- Las órdenes terminales (ENTREGADO, CANCELADO) no admiten más cambios.

---

## Agregar Observación Interna

**Actor**: Administrador, Operador, Técnico

**Flujo principal**:
1. El usuario accede al detalle de la orden.
2. Agrega un comentario en la bitácora.
3. El sistema registra: usuario, fecha y comentario.
4. La observación queda inmutable.

**Reglas**:
- Las observaciones no se pueden editar ni eliminar.
- Funcionan como bitácora de auditoría.
- Se pueden agregar incluso en estados terminales.

---

## Editar Orden (Servicios, Productos, Datos Generales)

**Actor**: Administrador, Operador

**Flujo principal**:
1. El usuario accede al detalle de la orden.
2. Modifica los servicios, productos, técnico, kilometraje, etc.
3. El sistema valida que la orden no esté en estado terminal.
4. Los totales se recalculan automáticamente.
5. La orden se guarda.

**Reglas**:
- No se puede editar una orden en estado **ENTREGADO** o **CANCELADO**.
- Al reemplazar servicios/productos, se eliminan los anteriores y se guardan los nuevos.
- Los totales siempre se mantienen consistentes.

---

## Cerrar Orden (Entrega)

**Actor**: Administrador, Operador

**Flujo principal**:
1. La orden debe estar en **LISTO_PARA_ENTREGA**.
2. El operador confirma que el cliente recibió el vehículo.
3. Cambia el estado a **ENTREGADO**.
4. El sistema registra fecha y hora de salida.
5. El sistema descuenta los productos del inventario automáticamente.
6. El sistema registra el movimiento de inventario tipo SALIDA.

**Validaciones**:
- La orden debe tener un cliente asignado.
- La orden debe tener un vehículo asociado.
- La orden debe tener al menos un servicio o un producto.

---

## Cancelar Orden

**Actor**: Administrador, Operador

**Flujo principal**:
1. El usuario accede al detalle de la orden.
2. Cambia el estado a **CANCELADO**.
3. La orden queda en estado terminal.
4. No se puede modificar ni eliminar después.

**Reglas**:
- No se puede cancelar una orden ya ENTREGADO.
- Las órdenes canceladas no se eliminan, solo cambian de estado.

---

## Consultar Historial

**Actor**: Administrador, Operador

**Flujo principal**:
1. El usuario accede al módulo de Órdenes.
2. Puede filtrar por:
   - Búsqueda por texto (número de orden, cliente, placa).
   - Estado (PENDIENTE, EN_DIAGNOSTICO, EN_PROCESO, ESPERANDO_REPUESTOS, LISTO_PARA_ENTREGA, ENTREGADO, CANCELADO).
3. El sistema muestra las órdenes paginadas, ordenadas por fecha de ingreso descendente.
4. El usuario puede hacer clic en una orden para ver su detalle completo.

**Datos visibles en el detalle**:
- Cliente (nombre, documento).
- Vehículo (placa, marca, línea).
- Técnico asignado.
- Lista de servicios con precios.
- Lista de productos con precios.
- Totales (servicios, productos, general).
- **Historial de cambios de estado** (ordenado por fecha).
- **Bitácora de observaciones internas**.
- Estado actual.

---

## Ver Dashboard

**Actor**: Administrador, Operador

**Indicadores disponibles**:
- Total de clientes registrados.
- Total de vehículos registrados.
- Órdenes pendientes (PENDIENTE).
- Órdenes en diagnóstico (EN_DIAGNOSTICO).
- Órdenes en proceso (EN_PROCESO).
- Órdenes esperando repuestos (ESPERANDO_REPUESTOS).
- Órdenes listas para entrega (LISTO_PARA_ENTREGA).
- Órdenes entregadas hoy (ENTREGADO).
- Ingresos del día (suma de totalGeneral de órdenes ENTREGADO hoy).
- Productos con bajo stock.
- Total de usuarios del sistema.

---

## Administrar Inventario

**Actor**: Administrador

**Flujo principal**:
1. El usuario accede al módulo de Inventario.
2. Visualiza todos los productos con su stock actual y mínimo.
3. Los productos con stock por debajo del mínimo se marcan como "Bajo Stock".
4. Puede registrar movimientos:
   - **Entrada**: Aumenta el stock (ej. compra a proveedor).
   - **Salida**: Disminuye el stock (ej. uso en orden, pérdida).
5. Cada movimiento queda registrado con fecha, usuario y motivo.

**Reglas**:
- No se permiten salidas que dejen el stock en negativo.
- Los movimientos son auditables (quién, cuándo, por qué).
- Se puede consultar el historial de movimientos por producto.

---

## Registrar Ficha Técnica del Vehículo

**Actor**: Administrador, Operador

**Flujo principal**:
1. El usuario accede al módulo de Vehículos.
2. Crea o edita un vehículo.
3. Completa campos adicionales de ficha técnica: motor, combustible, VIN, año.
4. El sistema guarda la información sin afectar el historial existente.

**Reglas**:
- Todos los campos de ficha técnica son opcionales.
- Se pueden actualizar en cualquier momento sin perder datos históricos.

---

## Consultar Historial del Vehículo

**Actor**: Administrador, Operador

**Flujo principal**:
1. El usuario accede al detalle de un vehículo.
2. Visualiza el historial completo de órdenes ordenado de más reciente a más antigua.
3. Cada entrada incluye: fecha, número de orden, estado, servicios realizados, productos utilizados, total, técnico, observaciones.
4. El historial es de solo lectura.

---

## Ver Línea de Tiempo del Vehículo

**Actor**: Administrador, Operador

**Flujo principal**:
1. El usuario accede a la vista de línea de tiempo del vehículo.
2. Visualiza eventos cronológicos con fecha, tipo de servicio, kilometraje y total.
3. Útil para entender la frecuencia y tipo de servicios que ha recibido el vehículo.

---

## Ver Estadísticas del Vehículo

**Actor**: Administrador, Operador

**Indicadores disponibles**:
- Total de visitas al taller.
- Total invertido históricamente.
- Fecha de la última visita.
- Promedio de días entre visitas.
- Servicio más frecuente.
- Producto más utilizado.

---

## Registrar Recomendación de Mantenimiento Preventivo

**Actor**: Administrador, Operador

**Flujo principal**:
1. El usuario accede al detalle de un vehículo.
2. Agrega una recomendación de mantenimiento.
3. Especifica: tipo (ACEITE, FILTROS, FRENOS, ALINEACION, BALANCEO, OTRO).
4. Define el tipo de programación: por KILOMETRAJE, FECHA, o AMBOS.
5. Ingresa el intervalo en kilómetros y/o días.
6. El sistema guarda la recomendación para cálculo de próximos mantenimientos.

---

## Consultar Próximos Mantenimientos

**Actor**: Administrador, Operador

**Flujo principal**:
1. El usuario accede al detalle de un vehículo.
2. Visualiza los próximos mantenimientos calculados automáticamente.
3. Cada mantenimiento muestra:
   - Próximo kilometraje recomendado.
   - Próxima fecha estimada.
   - Kilómetros y días restantes.
   - Alerta visual (NORMAL, PRONTO, PROXIMO).

**Cálculo automático**:
- **Próximo kilometraje** = Último kilometraje registrado + Intervalo de la recomendación.
- **Próxima fecha** = Fecha de la última orden + Intervalo en días.
- Las alertas se generan cuando faltan ≤ 1000 km (PRONTO), ≤ 500 km (PROXIMO), ≤ 30 días (PRONTO), o ≤ 15 días (PROXIMO).

---

## Ver Dashboard de Vehículos

**Actor**: Administrador, Operador

**Indicadores disponibles** (adicionales):
- **Vehículos próximos a mantenimiento**: Cantidad de vehículos con al menos una recomendación activa.
- **Vehículos sin visitas en 12 meses**: Vehículos sin órdenes registradas en el último año.
- **Clientes frecuentes**: Top 5 clientes con más órdenes de trabajo.
- **Vehículos con mayor inversión**: Top 5 vehículos con mayor suma de total general histórico.

---

## Abrir Caja

**Actor**: Administrador, Operador

**Flujo principal**:
1. El usuario accede al módulo de Caja.
2. Ingresa el monto inicial en efectivo.
3. Opcionalmente agrega una observación.
4. Envía la apertura.
5. El sistema valida que el usuario no tenga otra caja abierta.
6. La caja se crea en estado **ABIERTA**.
7. Se registra un movimiento de tipo **APERTURA** en la bitácora financiera.

**Validaciones**:
- No se permite doble apertura: si el usuario ya tiene una caja abierta, el sistema lo rechaza.

---

## Cerrar Caja

**Actor**: Administrador, Operador

**Flujo principal**:
1. El usuario accede al módulo de Caja.
2. Visualiza el resumen de ingresos del período.
3. Ingresa el monto contado en físico.
4. Opcionalmente agrega observaciones.
5. Envía el cierre.
6. El sistema calcula:
   - **Total ingresos**: Suma de pagos registrados durante la caja.
   - **Total esperado**: Monto inicial + total ingresos.
   - **Diferencia**: Monto contado - total esperado.
7. La caja se marca como **CERRADA**.
8. Se registra un movimiento de tipo **CIERRE**.

**Reglas**:
- No se puede cerrar una caja que ya esté cerrada.
- No se puede cerrar una caja que no existe.

---

## Registrar Pago en Orden

**Actor**: Administrador, Operador

**Flujo principal**:
1. El usuario accede al detalle de una Orden de Trabajo.
2. Visualiza el total general y el saldo pendiente.
3. Selecciona un método de pago.
4. Ingresa el valor a pagar.
5. Opcionalmente agrega una observación.
6. Envía el pago.
7. El sistema valida:
   - Valor mayor a cero.
   - Valor no excede saldo pendiente.
   - Método de pago existe y está activo.
8. El pago se registra.
9. El estado financiero de la orden se actualiza automáticamente:
   - Si `totalPagado >= totalGeneral` → **PAGADA**.
   - Si `totalPagado > 0` → **PARCIAL**.
10. Se registra un movimiento financiero tipo **INGRESO_PAGO**.

**Escenarios**:
- **Pago único**: La orden pasa de SIN_PAGAR a PAGADA.
- **Pago parcial**: La orden pasa de SIN_PAGAR a PARCIAL (o de PARCIAL a PAGADA si es el último pago).
- **Múltiples pagos**: Se pueden registrar varios pagos hasta cubrir el total.

---

## Anular Pago

**Actor**: Administrador

**Flujo principal**:
1. El usuario accede al detalle de la orden.
2. Visualiza la lista de pagos registrados.
3. Selecciona un pago para anular.
4. Confirma la anulación.
5. El sistema marca el pago como anulado (`pagoAnulado = true`).
6. El registro original no se elimina.
7. El estado financiero de la orden se recalcula automáticamente.

**Reglas**:
- Solo ADMIN puede anular pagos.
- No se puede anular un pago ya anulado.
- El pago anulado sigue siendo visible en el historial.
- Se registra un movimiento de tipo **ANULACION**.

---

## Ver Dashboard Financiero

**Actor**: Administrador, Operador

**Indicadores disponibles** (adicionales a los operativos):
- **Caja abierta**: Indica si el usuario tiene una caja abierta actualmente.
- **Órdenes sin pagar**: Cantidad de órdenes con estado financiero SIN_PAGAR.
- **Órdenes parciales**: Cantidad de órdenes con estado financiero PARCIAL.
- **Órdenes pagadas**: Cantidad de órdenes con estado financiero PAGADA.
- **Saldo pendiente total**: Suma de todos los saldos pendientes de órdenes no pagadas.

---

## Crear Orden de Compra

**Actor**: Administrador

**Flujo principal**:
1. El usuario accede al módulo de Compras.
2. Hace clic en "Nueva Orden de Compra".
3. Selecciona opcionalmente un proveedor.
4. Agrega productos con cantidad y precio unitario.
5. Opcionalmente agrega observaciones.
6. Envía la orden.
7. El sistema genera un número de orden automático (`OC-XXXXXXXX`).
8. La orden se crea en estado **BORRADOR**.

**Reglas**:
- Debe incluir al menos un producto.
- El proveedor es opcional.
- El total se calcula automáticamente (suma de subtotales).

---

## Cambiar Estado de Orden de Compra

**Actor**: Administrador

**Flujo principal**:
1. El usuario accede al detalle de la orden.
2. Cambia el estado según las transiciones disponibles.
3. El sistema valida la transición.
4. Si el nuevo estado es **RECIBIDA**, el sistema actualiza el inventario automáticamente.

**Transiciones válidas**:

| Desde | Hacia |
|-------|-------|
| BORRADOR | ENVIADA, CANCELADA |
| ENVIADA | RECIBIDA, CANCELADA |
| RECIBIDA | — (terminal) |
| CANCELADA | — (terminal) |

**Reglas**:
- No se permiten transiciones inválidas.
- Al pasar a **RECIBIDA** se incrementa el stock en inventario y se registra movimiento tipo ENTRADA.
- Las órdenes terminales (RECIBIDA, CANCELADA) no admiten más cambios.

---

## Eliminar Orden de Compra

**Actor**: Administrador

**Flujo principal**:
1. El usuario accede al detalle de la orden.
2. Solo se permite eliminar órdenes en estado **BORRADOR**.
3. La eliminación es lógica (soft delete).

**Reglas**:
- No se puede eliminar una orden ENVIADA, RECIBIDA o CANCELADA.

---

## Consultar Recomendaciones de Compra

**Actor**: Administrador

**Flujo principal**:
1. El usuario accede al módulo de Compras.
2. Visualiza la lista de productos recomendados para comprar.
3. Cada recomendación muestra: stock actual, mínimo, máximo, punto de reposición, cantidad sugerida, último proveedor y alerta.
4. Las alertas indican urgencia: AGOTADO (stock en cero), REPONER (stock bajo).

**Reglas**:
- Las recomendaciones son solo informativas. No generan compras automáticas.

---

## Consultar Estadísticas de Proveedor

**Actor**: Administrador

**Flujo principal**:
1. El usuario accede al perfil de un proveedor.
2. Visualiza estadísticas de compra: total comprado, número de órdenes recibidas, promedio de días de entrega, última compra, productos suministrados.

---

## Ver Dashboard de Inventario

**Actor**: Administrador

**Indicadores disponibles** (adicionales):
- **Productos bajo mínimo**: Cantidad de productos con stock por debajo del mínimo configurado.
- **Productos agotados**: Cantidad de productos con stock en cero.
- **Valor total del inventario**: Suma de stock actual × precio de venta de todos los productos.
- **Compras del mes**: Órdenes de compra recibidas en el mes actual.
- **Proveedores más utilizados**: Top proveedores con más órdenes recibidas.

---

## Ver Perfil Inteligente del Cliente

**Actor**: Administrador, Operador

**Flujo principal**:
1. El usuario accede al perfil CRM de un cliente.
2. Visualiza datos básicos del cliente.
3. Visualiza métricas calculadas: fecha registro, primera/última visita, total órdenes, vehículos, total gastado, promedio por orden.
4. Visualiza la clasificación automática (NUEVO, FRECUENTE, VIP, INACTIVO).
5. Visualiza notas internas del cliente.

**Cálculos automáticos**:
- Clasificación según reglas: INACTIVO (sin visitas >12 meses) → VIP (≥$5M) → FRECUENTE (≥5 órdenes) → NUEVO.
- Promedio por orden = total gastado / número de órdenes.

---

## Consultar Historial Económico del Cliente

**Actor**: Administrador, Operador

**Indicadores disponibles**:
- Total invertido históricamente.
- Cantidad de órdenes realizadas.
- Promedio gastado por orden.
- Mayor compra realizada.
- Fecha de la última compra.
- Total pendiente de pago.
- Total cancelado (pagado).

---

## Agregar Nota Interna a Cliente

**Actor**: Administrador, Operador

**Flujo principal**:
1. El usuario accede al perfil CRM del cliente.
2. Agrega un comentario u observación.
3. El sistema registra: usuario, fecha, hora y comentario.
4. La nota queda inmutable (no se puede editar ni eliminar).

**Reglas**:
- Las notas nunca se eliminan ni modifican.
- Funcionan como bitácora de auditoría.

---

## Consultar Clientes Inactivos

**Actor**: Administrador

**Flujo principal**:
1. El usuario accede al módulo CRM.
2. Consulta clientes sin visitas en los últimos 6 o 12 meses.
3. El sistema muestra: nombre, teléfono, email, última visita, meses sin visita, total órdenes históricas.

**Propósito**: Identificar oportunidades de recuperación de clientes.

---

## Consultar Ranking de Clientes

**Actor**: Administrador

**Flujo principal**:
1. El usuario accede al módulo CRM.
2. Selecciona el tipo de ranking: invertido, visitas, servicios, vehículos.
3. El sistema muestra top 10 clientes con su valor en la categoría seleccionada.

---

## Ver Dashboard CRM

**Actor**: Administrador

**Indicadores disponibles**:
- **Clientes nuevos este mes**: Clientes registrados en el mes actual.
- **Clientes activos**: Clientes con al menos una orden en los últimos 6 meses.
- **Clientes frecuentes**: Clientes con clasificación FRECUENTE (≥5 órdenes).
- **Clientes VIP**: Clientes con clasificación VIP (≥$5M gastados).
- **Clientes inactivos 12 meses**: Clientes sin visitas en el último año.
- **Clientes con mantenimiento próximo**: Clientes cuyos vehículos tienen recomendaciones activas.
- **Top 10 facturación**: Clientes con mayor total gastado.
- **Top 10 visitas**: Clientes con más órdenes realizadas.

---

## Configurar Empresa

**Actor**: Administrador

**Flujo principal**:
1. El usuario accede al módulo de Configuración.
2. Visualiza los datos actuales de la empresa (nombre, NIT, dirección, etc.).
3. Modifica los campos deseados.
4. Guarda los cambios.
5. El sistema actualiza la configuración de la empresa.

**Reglas**:
- Solo existe un registro activo de empresa.
- Los campos son opcionales excepto nombre y NIT (validación implícita).

---

## Configurar Parámetros del Sistema

**Actor**: Administrador

**Flujo principal**:
1. El usuario accede al módulo de Configuración > Parámetros.
2. Visualiza la lista de parámetros globales (IVA, descuento máximo, días inactivo, umbrales, etc.).
3. Puede crear nuevos parámetros o modificar existentes.
4. Al guardar, el nuevo valor está disponible inmediatamente en todo el sistema.

**Parámetros predefinidos**: IVA_DEFECTO (19%), DESCUENTO_MAXIMO (30), DIAS_INACTIVO (365), MONTO_MINIMO_VIP (5000000), ORDENES_FRECUENTE (5), umbrales km y días para alertas de mantenimiento.

---

## Configurar Numeración Automática

**Actor**: Administrador

**Flujo principal**:
1. El usuario accede a Configuración > Numeración.
2. Visualiza las configuraciones por módulo (ORDEN_TRABAJO, ORDEN_COMPRA, PAGO, CAJA).
3. Puede modificar prefijo, sufijo, longitud del número y si se reinicia anualmente.

**Reglas**:
- La numeración se incrementa automáticamente al usar el endpoint `POST /api/configuracion/numeracion/{modulo}/generar`.
- El formato es: `prefijo + número_zero-padded + sufijo`.
- No se permite modificar el número actual directamente.

---

## Administrar Impuestos

**Actor**: Administrador

**Flujo principal**:
1. El usuario accede a Configuración > Impuestos.
2. Visualiza los impuestos configurados (IVA 19%, IVA 5%, Exento).
3. Puede crear nuevos impuestos o modificar existentes.
4. Marca cuál es el impuesto por defecto para nuevas operaciones.

---

## Configurar Horarios de Atención

**Actor**: Administrador

**Flujo principal**:
1. El usuario accede a Configuración > Horarios.
2. Visualiza los 7 días de la semana con sus horarios de apertura y cierre.
3. Puede modificar horarios o desactivar días (ej. DOMINGO).

---

## Gestionar Días Festivos

**Actor**: Administrador

**Flujo principal**:
1. El usuario accede a Configuración > Festivos.
2. Agrega una fecha festiva con descripción.
3. El sistema valida que no exista duplicado.
4. Puede eliminar festivos existentes.

---

## Consultar Auditoría Global

**Actor**: Administrador

**Flujo principal**:
1. El usuario accede a Configuración > Auditoría.
2. Visualiza todos los eventos registrados ordenados por fecha descendente.
3. Puede filtrar por módulo (CLIENTES, ORDENES, CAJA, etc.).
4. Cada entrada muestra: usuario, fecha, acción, módulo, entidad afectada, valores anterior y nuevo.

**Propósito**: Trazabilidad completa de cambios críticos en el sistema.

---

## Registrar Backup

**Actor**: Administrador

**Flujo principal**:
1. El usuario accede a Configuración > Backups.
2. Registra manualmente un backup con usuario, tamaño y estado.
3. El sistema guarda el registro con fecha automática.

**Nota**: Solo se registran metadatos. El backup real debe ejecutarse externamente.

---

## Administrar Permisos por Módulo

**Actor**: Administrador

**Flujo principal**:
1. El usuario accede a Configuración > Permisos.
2. Selecciona un rol (ej. ADMIN).
3. Visualiza los permisos actuales del rol organizados por módulo.
4. Puede agregar o eliminar permisos específicos.

**Permisos disponibles**: CREAR, LEER, ACTUALIZAR, ELIMINAR.

**Módulos disponibles**: CLIENTES, VEHICULOS, ORDENES, COMPRAS, INVENTARIO, CAJA, PAGOS, USUARIOS, SERVICIOS, PRODUCTOS, PROVEEDORES, CONFIGURACION, CRM, REPORTES.
