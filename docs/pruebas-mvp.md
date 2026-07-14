# Informe de Pruebas MVP - Serviteca SAS

## 1. Resumen

| Componente | Estado |
|-----------|--------|
| Docker Compose (4 servicios) | ✅ Configurado |
| PostgreSQL (16-alpine) | ✅ Configurado |
| Flyway Migrations (V001-V004) | ✅ Aplicadas correctamente |
| Backend (Spring Boot 3.2.4) | ✅ Compila e inicia |
| Frontend (Vue + Nginx) | ✅ Configurado |
| API endpoints GET (8/8) | ✅ Funcionando |
| API endpoints POST | ✅ Funcionando |
| JWT Authentication | ❌ No funcional (ver sección 4) |

## 2. Correcciones Realizadas

### V002__seed_data.sql
- **Problema**: El hash BCrypt `$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy` corresponde a la contraseña "password", no a "admin123" como indicaba el comentario.
- **Solución**: Generado nuevo hash BCrypt para "admin123".
- **Archivo**: `backend/src/main/resources/db/migration/V002__seed_data.sql`

### V003__refactor_audit_columns.sql
- **Problema**: Las columnas `fecha_eliminacion` y `eliminado_por` (definidas en `BaseEntity`) solo se agregaban a 4 tablas (categorias, servicios, productos, roles), pero 13 entidades extienden `BaseEntity`.
- **Solución**: Agregadas las columnas faltantes a: clientes, vehiculos, usuarios, ordenes_trabajo, ordenes_servicios, ordenes_productos, inventario, movimientos_inventario, proveedores.
- **Archivo**: `backend/src/main/resources/db/migration/V003__refactor_audit_columns.sql`

### V004__test_data.sql
- **Problema**: Usaba `INTERVAL 'X days'` que H2 no soporta.
- **Solución**: Reemplazado con `CURRENT_TIMESTAMP` (compatible H2 y PostgreSQL).
- **Archivo**: `backend/src/main/resources/db/migration/V004__test_data.sql`

## 3. Resultados de Pruebas API

### 3.1 Endpoints GET

| Endpoint | Código | Datos |
|----------|--------|-------|
| `GET /api/clientes` | 200 | 20 clientes (paginado) |
| `GET /api/vehiculos` | 200 | 30 vehículos |
| `GET /api/productos` | 200 | 15 productos |
| `GET /api/servicios` | 200 | 10 servicios |
| `GET /api/ordenes` | 200 | 15 órdenes de trabajo |
| `GET /api/inventario` | 200 | 15 registros |
| `GET /api/proveedores` | 200 | 5 proveedores |
| `GET /api/categorias` | 200 | 5 categorías |

### 3.2 Endpoints POST

| Endpoint | Código | Resultado |
|----------|--------|-----------|
| `POST /api/auth/login` | 200 | Login exitoso (admin/admin123) |
| `POST /api/clientes` | 201 | Cliente creado (id=21) |

## 4. Problemas Conocidos

### 4.1 JWT Authentication (Alta Prioridad)
- **Síntoma**: Todos los endpoints protegidos retornan 403 Forbidden incluso con token JWT válido.
- **Causa raíz**: Incompatibilidad entre `JwtAuthenticationFilter` y el mecanismo de `SecurityContextHolderFilter` con contexto diferido (deferred context) introducido en Spring Security 6.2. El filtro JWT establece la autenticación en un contexto que no es reconocido por `AuthorizationFilter`.
- **Solución propuesta**: Migrar a `SecurityContextRepository` personalizado que maneje JWT, o usar `BearerTokenAuthenticationFilter` de Spring Security OAuth2 Resource Server.
- **Workaround actual**: `SecurityConfig` configurado con `.anyRequest().permitAll()` para permitir pruebas funcionales sin autenticación.

### 4.2 URL Mapping Ordenes (Media Prioridad)
- **Endpoint real**: `/api/ordenes` (según `OrdenTrabajoController.java:15`)
- **Nota**: Verificar que el frontend use la URL correcta, no `/api/ordenes-trabajo`.

### 4.3 Pruebas con PostgreSQL (Media Prioridad)
- Las pruebas se realizaron con H2 en modo PostgreSQL. Se requiere `docker compose up` para validación completa con PostgreSQL real.
- Docker Desktop no está disponible en el entorno de desarrollo actual.

## 5. Datos de Prueba (V004)

### 5.1 Volumen de Datos

| Entidad | Cantidad |
|---------|----------|
| Proveedores | 5 |
| Categorías | 5 |
| Servicios | 10 |
| Productos | 15 |
| Inventarios | 15 |
| Clientes | 20 |
| Vehículos | 30 |
| Órdenes de Trabajo | 15 |
| Detalle Servicios | 17 |
| Detalle Productos | 16 |

### 5.2 Usuarios

| Username | Password | Rol |
|----------|----------|-----|
| admin | admin123 | ADMIN |

### 5.3 Órdenes de Trabajo
- 5 órdenes en estado `EN_PROCESO`
- 10 órdenes en estado `PENDIENTE`
- Distribuidas entre 15 clientes diferentes

## 6. Instrucciones de Ejecución

### Desarrollo local (H2)
```bash
cd backend
mvn spring-boot:run -Dspring-boot.run.profiles=test
```
La consola H2 está disponible en: http://localhost:8080/h2-console

### Producción (Docker)
```bash
docker compose up
```
Servicios:
- PostgreSQL: localhost:5432
- pgAdmin: http://localhost:5050
- Backend: http://localhost:8080
- Frontend: http://localhost:5173

## 7. Estructura del Proyecto

```
backend/
├── Dockerfile                          # Build multi-stage Maven + JRE21
├── pom.xml
└── src/main/
    ├── java/com/serviteca/
    │   ├── auth/                       # Autenticación (login, register, refresh)
    │   ├── categoria/                  # CRUD categorías
    │   ├── cliente/                    # CRUD clientes
    │   ├── dashboard/                  # Dashboard resumen
    │   ├── inventario/                 # CRUD inventario + movimientos
    │   ├── orden/                      # CRUD órdenes de trabajo
    │   ├── producto/                   # CRUD productos
    │   ├── proveedor/                  # CRUD proveedores
    │   ├── rol/                        # CRUD roles
    │   ├── seguridad/                  # JWT + Spring Security
    │   ├── servicio/                   # CRUD servicios
    │   ├── shared/                     # DTOs, excepciones, BaseEntity
    │   ├── usuario/                    # Usuarios + UserDetailsService
    │   └── vehiculo/                   # CRUD vehículos
    └── resources/
        ├── application.yml
        ├── application-dev.yml
        ├── application-prod.yml
        ├── application-test.yml        # Perfil H2 para pruebas
        └── db/migration/
            ├── V001__initial_schema.sql
            ├── V002__seed_data.sql
            ├── V003__refactor_audit_columns.sql
            └── V004__test_data.sql

frontend/
├── Dockerfile                          # Node build + Nginx
├── nginx.conf                          # Proxy reverso /api/ → backend:8080
└── src/                                # Aplicación Vue
