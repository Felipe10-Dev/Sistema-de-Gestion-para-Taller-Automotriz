# Stack Tecnológico

## Backend

| Tecnología | Versión | Propósito |
|-----------|---------|-----------|
| Java | 21 LTS | Lenguaje de programación |
| Spring Boot | 3.2.4 | Framework principal |
| Spring Web | - | API REST |
| Spring Data JPA | - | Acceso a datos / ORM |
| Spring Security | - | Autenticación y autorización (SecurityContextRepository) |
| Spring Validation | - | Validación de entrada |
| Spring Boot Actuator | - | Health check y monitoreo |
| Hibernate | 6.x | Implementación JPA |
| Flyway | 9.x | Migraciones de base de datos |
| PostgreSQL Driver | 42.x | Conexión a PostgreSQL |
| H2 | 2.x | Base de datos embebida para tests (modo PostgreSQL) |
| jjwt (io.jsonwebtoken) | 0.12.5 | Generación y validación de JWT |
| Lombok | 1.18.x | Reducción de boilerplate |
| MapStruct | 1.5.5 | Mapeo de objetos |
| Maven | 3.x | Gestión de dependencias y build |
| Apache HttpClient5 | 5.x (test) | Cliente HTTP para tests (PATCH, 401) |
| Testcontainers | 1.x (test) | Contenedores para tests de integración (opcional) |

### Dependencias principales (pom.xml)

```xml
<dependencies>
    spring-boot-starter-web
    spring-boot-starter-data-jpa
    spring-boot-starter-security
    spring-boot-starter-validation
    spring-boot-starter-actuator
    spring-boot-starter-test
    postgresql
    h2 (test)
    flyway-core
    jjwt-api / jjwt-impl / jjwt-jackson (0.12.5)
    lombok
    mapstruct / mapstruct-processor (1.5.5)
    httpclient5 (test)
    testcontainers / testcontainers-postgresql (test)
</dependencies>
```

### ¿Por qué estas tecnologías?

| Decisión | Motivo |
|----------|--------|
| Spring Boot 3 | Ecosistema maduro, integración con todo, soporte LTS |
| Spring Data JPA | Elimina SQL repetitivo, repositorios automáticos |
| Spring Security | Seguridad declarativa, fácil integración JWT |
| Flyway | Migraciones versionadas, integración Spring Boot |
| jjwt | Librería moderna y activa para JWT |
| lombok | Reduce código repetitivo (getters, constructores) |
| Maven | Estándar en industria Java, predecible |
| H2 (test) | Tests portables sin PostgreSQL local |
| HttpClient5 (test) | Soporte PATCH y manejo correcto de 401 en tests |

---

## Frontend

| Tecnología | Versión | Propósito |
|-----------|---------|-----------|
| React | 18.3.1 | Librería UI |
| TypeScript | 5.5.4 | Tipado estático |
| Vite | 5.4.2 | Bundler y dev server |
| Tailwind CSS | 3.4.10 | Estilos utilitarios |
| React Router | 6.26.0 | Enrutamiento SPA |
| Axios | 1.7.4 | Cliente HTTP |

### Dependencias adicionales

| Paquete | Propósito |
|---------|-----------|
| @heroicons/react | Iconos SVG |
| autoprefixer | Prefixes CSS |
| postcss | Procesador CSS |

### ¿Por qué estas tecnologías?

| Decisión | Motivo |
|----------|--------|
| React 18 | Ecosistema grande, concurrencia, hooks maduros |
| TypeScript | Previene errores en tiempo de compilación |
| Vite | Dev server instantáneo, HMR rápido |
| Tailwind CSS | Desarrollo rápido, diseño consistente |
| React Router | Estándar para SPA con React |
| Axios | Interceptors, manejo de errores, refresh token |

---

## Base de Datos

| Componente | Versión |
|-----------|---------|
| PostgreSQL | 16 Alpine (Docker) |
| Puerto | 5432 |
| Base de datos | `serviteca` |
| Usuario | `serviteca` |
| Contraseña | `serviteca123` |
| H2 (tests) | Modo PostgreSQL (`MODE=PostgreSQL`) |

---

## Herramientas de desarrollo

| Herramienta | Uso |
|-----------|-----|
| Docker / Docker Compose | PostgreSQL local + stack completo |
| Maven | Build backend |
| npm | Build frontend |
| Vite | Dev server frontend |
| IntelliJ IDEA | IDE recomendado backend |
| VS Code | IDE recomendado frontend |
| pgAdmin | Cliente PostgreSQL (incluido en docker-compose) |

---

## Versiones del proyecto

| Componente | Versión actual |
|-----------|---------------|
| API Backend | 1.3.0-SNAPSHOT |
| Frontend | 1.0.0 |
| Esquema BD | V005 (historial estados + observaciones) |
| Tests de integración | 14 tests pasando (0 fallos) |
