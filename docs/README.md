# Serviteca - Sistema de Gestión para Serviteca

Sistema moderno, rápido, estable y mantenible para la administración de una serviteca (taller automotriz). Construido con una base sólida que permite evolucionar hacia múltiples sedes o múltiples empresas en futuras versiones.

## Objetivo Principal

Obtener un MVP completamente funcional, estable y fácil de mantener que sirva como base para evolucionar progresivamente hacia una plataforma empresarial completa.

## Tecnologías

| Capa | Tecnología |
|------|-----------|
| Backend | Java 21, Spring Boot 3, Spring Security 6, Spring Data JPA, Hibernate |
| Base de datos | PostgreSQL 16, Flyway, H2 (tests) |
| Frontend | React 18, TypeScript, Tailwind CSS, Vite |
| Autenticación | JWT + Refresh Tokens con SecurityContextRepository |
| Monitoreo | Spring Boot Actuator (`/actuator/health`) |

## Requisitos

- Java 21+
- Maven 3.x
- Node.js 18+
- Docker Desktop (para PostgreSQL y stack completo)

## Cómo levantar el proyecto

### 1. Variables de entorno

Copia `.env.example` a `.env` y ajusta los valores:

```bash
cp .env.example .env
```

### 2. Stack completo (Docker)

```bash
# Desde la raíz del proyecto
docker compose up -d
```

Esto inicia:
- **PostgreSQL 16** en `localhost:5432`
- **Backend** en `http://localhost:8080` (con healthcheck vía `/actuator/health`)
- **Frontend** en `http://localhost:5173`
- **pgAdmin** en `http://localhost:5050`

### 3. Solo backend (desarrollo)

```bash
cd backend
# Asegúrate de tener PostgreSQL corriendo (docker compose up -d postgres)
mvn clean install -DskipTests
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

El backend se levanta en `http://localhost:8080`.

### 4. Solo frontend (desarrollo)

```bash
cd frontend
npm install
npm run dev
```

El frontend se levanta en `http://localhost:5173` con proxy a `localhost:8080`.

### 5. Ejecutar tests

```bash
cd backend
mvn test
```

Usa H2 en modo PostgreSQL (no requiere PostgreSQL corriendo). 11 tests de integración.

## Usuario inicial

| Campo | Valor |
|-------|-------|
| Usuario | `admin` |
| Contraseña | `admin123` |
| Rol | ADMIN |

El usuario se crea automáticamente con la migración `V002__seed_data.sql` ejecutada por Flyway al iniciar el backend.

## Health Check

```bash
curl http://localhost:8080/actuator/health
# {"status":"UP","components":{"db":{"status":"UP"},"diskSpace":{"status":"UP"},"ping":{"status":"UP"}}}
```

## Estructura general

```
serviteca-sas/
├── backend/              # Spring Boot (Java 21)
│   ├── src/main/java/    # Código fuente
│   └── src/main/resources/ # Configuración y migrations
├── frontend/             # React + TypeScript + Vite
│   └── src/              # Código fuente
├── docs/                 # Documentación del proyecto
├── .env.example          # Template de variables de entorno
└── docker-compose.yml    # PostgreSQL + Backend + Frontend + pgAdmin
```

## Módulos del sistema

| Módulo | Propósito |
|--------|-----------|
| auth | Autenticación JWT (login, register, refresh) |
| usuario | Gestión de usuarios del sistema |
| rol | Roles y permisos |
| cliente | Registro de clientes |
| vehiculo | Vehículos asociados a clientes |
| orden | Órdenes de trabajo (núcleo del sistema) |
| servicio | Servicios ofrecidos (mano de obra) |
| producto | Productos e insumos |
| categoria | Categorías para servicios y productos |
| inventario | Control de stock y movimientos |
| proveedor | Proveedores de productos |
| dashboard | Indicadores y resúmenes |

## Licencia

Proyecto privado - Uso interno.
