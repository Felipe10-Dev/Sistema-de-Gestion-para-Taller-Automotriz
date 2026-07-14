# Arquitectura del Sistema

## Tipo de Arquitectura: Monolito Modular

El sistema está construido como un **monolito modular**: una sola aplicación desplegable dividida internamente por módulos independientes. Cada módulo tiene una responsabilidad única y bien definida, con sus propias capas internas.

```
Frontend (React)
      │
      ▼
   REST API  (JSON)
      │
      ▼
Spring Boot (Monolito Modular)
      │
      ▼
  PostgreSQL
```

## Flujo de una petición

```
Usuario (navegador)
    │
    ▼
React App ──axios──► /api/*
    │                    │
    │              [Proxy reverso Vite]
    │                    │
    ▼                    ▼
    ◄────── JSON ──── Spring Boot
                          │
                     Controlador
                          │
                        Servicio
                          │
                     Repositorio (JPA)
                          │
                     PostgreSQL
```

## Justificación de la arquitectura

### ¿Por qué Monolito Modular y no Microservicios?

1. **Equipo pequeño**: Un equipo pequeño o una sola persona puede mantener un monolito mucho más eficientemente.
2. **Complejidad inicial**: Microservicios agregan complejidad innecesaria (red, descubrimiento, circuit breakers, eventos) para un MVP.
3. **Rendimiento**: Sin latencia de red entre servicios, el monolito es más rápido.
4. **Consistencia transaccional**: Todas las operaciones dentro de una misma base de datos y una misma transacción ACID.
5. **Despliegue simple**: Un solo artefacto para desplegar.
6. **Evolución controlada**: La modularidad permite en el futuro extraer módulos a microservicios si es necesario.

### ¿Por qué no microservicios en el MVP?

- Mayor costo operativo inicial.
- Curva de aprendizaje más alta.
- Debugging y tracing más complejo.
- La división de bases de datos agrega consistencia eventual.
- El domino del problema no requiere escalamiento independiente aún.

## Principios SOLID aplicados

| Principio | Aplicación |
|-----------|-----------|
| **S** - Single Responsibility | Cada módulo tiene una única responsabilidad. Cada clase tiene un propósito claro. |
| **O** - Open/Closed | Las entidades y servicios están abiertos a extensión pero cerrados a modificación. |
| **L** - Liskov Substitution | Las interfaces y herencias respetan el contrato base. |
| **I** - Interface Segregation | Las interfaces son específicas para cada necesidad. |
| **D** - Dependency Injection | Spring maneja la inyección de dependencias vía constructor. |

## Clean Code

- Nombres descriptivos en clases, métodos y variables.
- Métodos cortos con una sola responsabilidad.
- Sin comentarios innecesarios (el código se auto-documenta).
- DTOs para separar la capa de presentación del modelo interno.
- Manejo centralizado de excepciones con `@RestControllerAdvice`.
- Mappers para transformar entidades en DTOs.

## Responsabilidades por capas

### Controller
- Recibir y validar la petición HTTP inicial.
- Delegar al servicio correspondiente.
- Devolver la respuesta en formato estándar (`ApiResponse`).
- **Nunca** contiene lógica de negocio.

### Service
- Contiene toda la lógica de negocio.
- Orquesta llamadas a repositorios y otros servicios.
- Aplica validaciones de negocio.
- Maneja transacciones.
- **Nunca** recibe o devuelve entidades JPA directamente.

### Repository
- Capa de acceso a datos gestionada por Spring Data JPA.
- Define consultas personalizadas con `@Query`.
- **Nunca** contiene lógica de negocio.

### Entity
- Representación del modelo de datos JPA.
- Define relaciones, restricciones y mapeo.
- Sin lógica de negocio.

### DTO
- Objetos planos para la comunicación con el exterior.
- Separan la representación interna (entity) de la externa (response).
- Validaciones con Jakarta Validation.

### Mapper
- Transforma entidades a DTOs y viceversa.
- Mantiene el desacoplamiento entre capas.

### Validator
- Validaciones de negocio específicas (unicidad, reglas de dominio).
- Separadas del service para mantenerlo limpio.

### Exception
- Excepciones personalizadas para distintos casos de error.
- Manejadas globalmente por `GlobalExceptionHandler`.

## Capa transversal (shared)

El módulo `shared/` contiene:
- `dto/`: `ApiResponse<T>` y `PagedResponse<T>` (formato de respuesta único).
- `exception/`: Excepciones base y `GlobalExceptionHandler`.
- `config/`: Configuraciones globales (auditoría, CORS, seguridad).
- `util/`: Clases base y utilidades compartidas.

## Principios de diseño

- Nunca duplicar lógica (DRY).
- Nunca acceder a la base de datos desde los controladores.
- Toda la lógica pertenece a la capa Service.
- Utilizar DTOs para las respuestas.
- Validar todas las entradas.
- Centralizar el manejo de excepciones.
- Mantener el código desacoplado y fácil de extender.
