# 🚗 Serviteca SAS — Sistema de Gestión para Taller Automotriz

Sistema moderno, rápido, seguro y mantenible para la administración completa de una serviteca (taller automotriz). Incluye gestión de clientes, vehículos, órdenes de trabajo, inventario, facturación y más. Construido con una arquitectura modular y escalable que permite evolucionar hacia múltiples sedes o multiempresa en futuras versiones.

---

## 📋 Tabla de Contenidos

- [Stack Tecnológico](#stack-tecnológico)
- [Módulos del Sistema](#módulos-del-sistema)
- [Requisitos Previos](#requisitos-previos)
- [Ejecutar con Docker (recomendado)](#ejecutar-con-docker-recomendado)
- [Ejecutar en Desarrollo (sin Docker)](#ejecutar-en-desarrollo-sin-docker)
  - [1. Base de datos](#1-base-de-datos)
  - [2. Backend](#2-backend)
  - [3. Frontend](#3-frontend)
- [Variables de Entorno](#variables-de-entorno)
- [Usuario Inicial](#usuario-inicial)
- [Ejecutar Tests](#ejecutar-tests)
- [Estructura del Proyecto](#estructura-del-proyecto)
- [API Endpoints](#api-endpoints-endpoints-principales)
- [Monitoreo](#monitoreo)
- [Comandos Útiles](#comandos-útiles)
- [Solución de Problemas](#solución-de-problemas)

---

## 🧰 Stack Tecnológico

| Capa | Tecnología | Versión |
|------|-----------|---------|
| **Backend** | Java, Spring Boot 3, Spring Security 6, Spring Data JPA, Hibernate | Java 21 / Spring Boot 3.2.4 |
| **Base de datos** | PostgreSQL, Flyway (migraciones), H2 (tests) | PostgreSQL 16 |
| **Frontend** | React, TypeScript, Tailwind CSS, Vite | React 18 / TS 5.5 |
| **Autenticación** | JWT + Refresh Tokens (SecurityContextRepository) | jjwt 0.12.5 |
| **Monitoreo** | Spring Boot Actuator | — |
| **Containerización** | Docker, Docker Compose | — |
| **Proxy reverso** | Nginx (frontend en producción) | — |
| **Logs** | Logstash Logback Encoder | — |
| **Mapper** | MapStruct 1.5.5 | — |
| **Tests** | JUnit 5, Testcontainers, Spring Security Test | — |

---

## 📦 Módulos del Sistema

| Módulo | Propósito |
|--------|-----------|
| `auth` | Autenticación JWT (login, register, refresh, logout) |
| `usuario` | Gestión de usuarios del sistema (CRUD, activar/inactivar) |
| `rol` | Roles y asignación de permisos |
| `cliente` | Registro y gestión de clientes |
| `vehiculo` | Vehículos asociados a clientes (placa, marca, modelo, año) |
| `orden` | Órdenes de trabajo — núcleo del sistema (creación, seguimiento, cierre) |
| `servicio` | Servicios ofrecidos (mano de obra con precio sugerido) |
| `producto` | Productos e insumos del taller |
| `categoria` | Categorías para servicios y productos |
| `inventario` | Control de stock, entradas, salidas, ajustes y transferencias |
| `proveedor` | Proveedores de productos e insumos |
| `dashboard` | Indicadores, gráficos y resúmenes del negocio |
| `caja` | Apertura, cierre y movimientos de caja |
| `compras` | Órdenes de compra a proveedores |
| `historial-vehiculo` | Historial completo de servicios por vehículo |
| `crm` | Seguimiento a clientes, recordatorios y comunicaciones |

---

## ⚙️ Requisitos Previos

| Herramienta | Versión Mínima | Descargas |
|------------|----------------|-----------|
| **Docker Desktop** | Cualquier versión reciente | [descargar](https://www.docker.com/products/docker-desktop/) |
| **Java** (para dev sin Docker) | 21+ | [descargar](https://adoptium.net/) |
| **Maven** (para dev sin Docker) | 3.x | [descargar](https://maven.apache.org/download.cgi) |
| **Node.js** (para dev sin Docker) | 18+ | [descargar](https://nodejs.org/) |
| **Git** | Cualquier versión | [descargar](https://git-scm.com/) |

Verifica las versiones instaladas:

```bash
java -version
mvn -version
node -v
npm -v
docker --version
docker compose version
```

---

## 🐳 Ejecutar con Docker (recomendado)

Esta es la forma más rápida y simple. Levanta todo el stack completo con un solo comando.

### 1. Clonar el repositorio

```bash
git clone <url-del-repo>
cd serviteca-sas
```

### 2. Configurar variables de entorno (opcional)

```bash
cp .env.example .env
```

Los valores por defecto funcionan para desarrollo local. Solo ajusta si necesitas credenciales diferentes.

### 3. Levantar todo el stack

```bash
docker compose up -d
```

Este comando descarga las imágenes, compila el backend y frontend, e inicia **4 servicios**:

| Servicio | URL | Descripción |
|----------|-----|-------------|
| **PostgreSQL 16** | `postgres:5432` (red interna) | Base de datos |
| **Backend** | [http://localhost:8080](http://localhost:8080) | API REST (Spring Boot) |
| **Frontend** | [http://localhost:5173](http://localhost:5173) | Interfaz de usuario (React + Nginx) |
| **pgAdmin** | [http://localhost:5050](http://localhost:5050) | Administrador gráfico de PostgreSQL |

### 4. Verificar que todo funciona

```bash
# Health check del backend
curl http://localhost:8080/actuator/health

# Ver logs en tiempo real
docker compose logs -f

# Ver estado de los servicios
docker compose ps
```

### 5. Detener los servicios

```bash
docker compose down        # Detiene y elimina los contenedores
docker compose down -v     # Lo mismo + elimina volúmenes (borra la BD)
```

---

## 💻 Ejecutar en Desarrollo (sin Docker)

Si prefieres correr los servicios de forma nativa para desarrollo más ágil (hot reload, debugging, etc.).

### 1. Base de datos

Necesitas PostgreSQL corriendo. La forma más fácil es usar Docker solo para la BD:

```bash
docker compose up -d postgres pgadmin
```

Esto levanta PostgreSQL en `localhost:5432` y pgAdmin en `http://localhost:5050`.

Credenciales por defecto de la BD:
- **Base de datos:** `serviteca`
- **Usuario:** `serviteca`
- **Contraseña:** `serviteca123`

> Si ya tienes PostgreSQL nativo instalado, asegúrate de que el puerto `5432` esté libre y crea la base de datos `serviteca` manualmente.

### 2. Backend (Spring Boot)

```bash
cd backend

# Compilar (sin tests para ir más rápido)
mvn clean install -DskipTests

# Ejecutar con perfil de desarrollo
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

El backend se levanta en [http://localhost:8080](http://localhost:8080).

**Características del perfil `dev`:**
- Logs detallados (DEBUG) de seguridad, SQL y la aplicación
- SQL de Hibernate formateado y visible en consola
- Actuator expone detalles completos de salud

**Hot reload:** Si usas un IDE como IntelliJ IDEA, puedes ejecutar la clase `ServitecaApplication` directamente y los cambios se reflejan al recompilar (`Ctrl+Shift+F9`).

### 3. Frontend (React + Vite)

Abre otra terminal:

```bash
cd frontend

# Instalar dependencias (solo la primera vez)
npm install

# Iniciar servidor de desarrollo con hot reload
npm run dev
```

El frontend se levanta en [http://localhost:5173](http://localhost:5173) con proxy automático al backend en `localhost:8080`.

**Hot reload:** Los cambios en archivos del frontend se reflejan al instante sin recargar manual.

---

## 🔐 Variables de Entorno

Copia `.env.example` a `.env` y personaliza si es necesario:

```bash
cp .env.example .env
```

| Variable | Descripción | Valor por Defecto |
|----------|-------------|-------------------|
| `DB_URL` | URL de conexión a PostgreSQL | `jdbc:postgresql://postgres:5432/serviteca` |
| `DB_USERNAME` | Usuario de BD | `serviteca` |
| `DB_PASSWORD` | Contraseña de BD | `serviteca123` |
| `JWT_SECRET` | Clave secreta para firmar JWT | (valor generado de 64 caracteres hex) |
| `JWT_EXPIRATION_MS` | Tiempo de expiración del token (ms) | `3600000` (1 hora) |
| `JWT_REFRESH_EXPIRATION_MS` | Tiempo de expiración del refresh token (ms) | `86400000` (24 horas) |
| `PGADMIN_DEFAULT_EMAIL` | Email para pgAdmin | `admin@serviteca.com` |
| `PGADMIN_DEFAULT_PASSWORD` | Contraseña para pgAdmin | `admin123` |

> ⚠️ En producción, **cambia obligatoriamente** `JWT_SECRET`, `DB_PASSWORD` y las credenciales de pgAdmin.

---

## 👤 Usuario Inicial

Al iniciar el backend por primera vez, Flyway ejecuta automáticamente la migración `V002__seed_data.sql` que crea un usuario administrador:

| Campo | Valor |
|-------|-------|
| **Usuario** | `admin` |
| **Contraseña** | `admin123` |
| **Rol** | `ADMIN` |

Endpoint de login:

```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username": "admin", "password": "admin123"}'
```

Respuesta exitosa:

```json
{
  "accessToken": "eyJhbGciOiJIUzUxMiJ9...",
  "refreshToken": "eyJhbGciOiJIUzUxMiJ9...",
  "expiresIn": 3600000,
  "type": "Bearer"
}
```

---

## 🧪 Ejecutar Tests

### Backend (11 tests de integración)

```bash
cd backend
mvn test
```

Los tests usan **H2 en modo PostgreSQL** (no requieren PostgreSQL corriendo). Para pruebas más reales con **Testcontainers** (requiere Docker):

```bash
mvn test -Dtestcontainers=true
```

### Frontend

```bash
cd frontend
npm test
```

---

## 📁 Estructura del Proyecto

```
serviteca-sas/
├── backend/                          # Spring Boot (Java 21)
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/serviteca/
│   │   │   │   ├── auth/             # Autenticación JWT
│   │   │   │   ├── common/           # Utilidades, excepciones globales, config
│   │   │   │   ├── config/           # Configuraciones Spring (Security, CORS, etc.)
│   │   │   │   └── modules/          # Módulos del negocio
│   │   │   │       ├── cliente/
│   │   │   │       ├── vehiculo/
│   │   │   │       ├── orden/
│   │   │   │       ├── servicio/
│   │   │   │       ├── producto/
│   │   │   │       ├── inventario/
│   │   │   │       ├── proveedor/
│   │   │   │       ├── caja/
│   │   │   │       ├── compras/
│   │   │   │       ├── dashboard/
│   │   │   │       └── ...           # Cada módulo tiene controller, service, repository, dto, entity, mapper
│   │   │   └── resources/
│   │   │       ├── application.yml        # Config principal
│   │   │       ├── application-dev.yml    # Perfil desarrollo
│   │   │       ├── application-prod.yml   # Perfil producción
│   │   │       ├── application-test.yml   # Perfil tests
│   │   │       └── db/migration/          # Migraciones Flyway (V001__, V002__, etc.)
│   │   └── test/java/com/serviteca/
│   ├── Dockerfile
│   ├── pom.xml
│   └── logs/                        # Logs de la aplicación
│
├── frontend/                         # React + TypeScript + Vite
│   ├── src/
│   │   ├── components/               # Componentes reutilizables
│   │   ├── pages/                    # Páginas/rutas
│   │   ├── hooks/                    # Custom hooks
│   │   ├── services/                 # Llamadas API (axios)
│   │   ├── store/                    # Estado global (Context API)
│   │   ├── types/                    # Tipos TypeScript
│   │   └── utils/                    # Utilidades
│   ├── index.html
│   ├── vite.config.ts
│   ├── tailwind.config.js
│   ├── tsconfig.json
│   ├── nginx.conf                    # Config Nginx para producción
│   └── Dockerfile
│
├── docs/                             # Documentación detallada
│   ├── README.md                     # Documentación principal
│   ├── stack-tecnologico.md
│   ├── modelo-datos.md
│   ├── arquitectura.md
│   ├── api.md
│   ├── modulos.md
│   ├── seguridad.md
│   ├── casos-de-uso.md
│   ├── flujo-orden-trabajo.md
│   ├── estructura-proyecto.md
│   ├── multiempresa.md
│   ├── roadmap.md
│   └── ... (24 archivos de documentación)
│
├── .env.example                      # Template de variables de entorno
├── docker-compose.yml                # Orquestación de servicios
└── README.md                         # Este archivo
```

---

## 🌐 API Endpoints (Endpoints principales)

### Autenticación (`/api/v1/auth`)

| Método | Endpoint | Descripción | Auth |
|--------|----------|-------------|------|
| POST | `/api/v1/auth/login` | Iniciar sesión | ❌ |
| POST | `/api/v1/auth/register` | Registrar usuario | ❌ |
| POST | `/api/v1/auth/refresh` | Refrescar token | ❌ |
| POST | `/api/v1/auth/logout` | Cerrar sesión | ✅ |

### Clientes (`/api/v1/clientes`)

| Método | Endpoint | Descripción | Auth |
|--------|----------|-------------|------|
| GET | `/api/v1/clientes` | Listar clientes | ✅ |
| GET | `/api/v1/clientes/{id}` | Obtener cliente por ID | ✅ |
| POST | `/api/v1/clientes` | Crear cliente | ✅ |
| PUT | `/api/v1/clientes/{id}` | Actualizar cliente | ✅ |
| DELETE | `/api/v1/clientes/{id}` | Eliminar cliente | ✅ |

### Órdenes de Trabajo (`/api/v1/ordenes`)

| Método | Endpoint | Descripción | Auth |
|--------|----------|-------------|------|
| GET | `/api/v1/ordenes` | Listar órdenes | ✅ |
| GET | `/api/v1/ordenes/{id}` | Obtener orden | ✅ |
| POST | `/api/v1/ordenes` | Crear orden | ✅ |
| PUT | `/api/v1/ordenes/{id}` | Actualizar orden | ✅ |
| PATCH | `/api/v1/ordenes/{id}/estado` | Cambiar estado | ✅ |

> Para la lista completa de endpoints, consulta `docs/api.md`.

---

## 📊 Monitoreo

El backend expone endpoints de monitoreo vía Spring Boot Actuator:

```bash
# Health check general
curl http://localhost:8080/actuator/health

# Información de la aplicación
curl http://localhost:8080/actuator/info

# Métricas
curl http://localhost:8080/actuator/metrics
```

Respuesta esperada del health check:

```json
{
  "status": "UP",
  "components": {
    "db": { "status": "UP" },
    "diskSpace": { "status": "UP" },
    "ping": { "status": "UP" }
  }
}
```

---

## 🛠️ Comandos Útiles

```bash
# === Docker ===
docker compose up -d                    # Levantar servicios
docker compose down                     # Detener y eliminar contenedores
docker compose logs -f                  # Ver logs en tiempo real
docker compose logs -f backend          # Logs solo del backend
docker compose ps                       # Estado de servicios
docker compose restart backend          # Reiniciar solo el backend
docker compose build --no-cache backend # Reconstruir imagen sin caché

# === Backend (Maven) ===
mvn clean install                       # Compilar y empaquetar
mvn clean install -DskipTests           # Compilar sin tests
mvn test                                # Ejecutar tests
mvn spring-boot:run                     # Ejecutar backend
mvn spring-boot:run -Dspring-boot.run.profiles=dev  # Ejecutar con perfil dev

# === Frontend (npm) ===
npm install                             # Instalar dependencias
npm run dev                             # Iniciar servidor desarrollo (hot reload)
npm run build                           # Compilar para producción
npm run preview                         # Vista previa de build producción

# === Base de datos ===
docker compose exec postgres psql -U serviteca -d serviteca  # Consola SQL
```

---

## 🔧 Solución de Problemas

### Error: `Address already in use` (puerto ocupado)

```bash
# Buscar qué está usando el puerto
netstat -ano | findstr :8080

# En Windows, matar el proceso:
taskkill /PID <PID> /F
```

### Error: Backend no conecta a PostgreSQL

```bash
# Verificar que PostgreSQL está corriendo
docker compose ps

# Ver logs del backend
docker compose logs backend

# Probar conexión manual
docker compose exec postgres psql -U serviteca -d serviteca -c "SELECT 1;"
```

### Error: `npm install` falla

```bash
# Limpiar caché y reintentar
cd frontend
rm -rf node_modules package-lock.json
npm cache clean --force
npm install
```

### Error: Maven no compila

```bash
cd backend
mvn clean
mvn dependency:resolve
mvn compile
```

### Error: Flyway migration fails

```bash
# Verificar migraciones aplicadas
docker compose exec postgres psql -U serviteca -d serviteca -c "SELECT version, script, installed_on FROM flyway_schema_history;"

# Si una migración falló, reparar:
# (opción drástica: borrar volúmenes y reiniciar)
docker compose down -v
docker compose up -d
```

### El frontend muestra pantalla en blanco

```bash
# Abrir consola del navegador (F12) para ver errores
# Verificar que el backend está accesible
curl http://localhost:8080/actuator/health

# Si usas desarrollo, verifica el proxy en vite.config.ts
```

---

## 📚 Documentación Adicional

Toda la documentación detallada está en la carpeta `docs/`:

- `stack-tecnologico.md` — Justificación del stack
- `modelo-datos.md` — Diagrama entidad-relación
- `arquitectura.md` — Decisiones de arquitectura
- `api.md` — Documentación completa de la API
- `seguridad.md` — Esquema de autenticación y autorización
- `casos-de-uso.md` — Casos de uso del sistema
- `roadmap.md` — Hoja de ruta y próximas features
- `multiempresa.md` — Plan de evolución a multiempresa
- `convenciones.md` — Convenciones de código

---

## 🧱 Convenciones de Código

- **Backend:** Paquete por módulo (`com.serviteca.modules.<modulo>`), cada módulo con su propio controller, service, repository, entity, dto y mapper.
- **Frontend:** Componentes en PascalCase, archivos en kebab-case para páginas.
- **Base de datos:** Migraciones Flyway con formato `V<numero>__<descripcion>.sql`.
- **API:** RESTful, versionada (`/api/v1/`), respuestas uniformes con `ApiResponse<T>`.
- **Errores:** Manejo global con `@RestControllerAdvice`, códigos de error específicos.
- **DTOs:** Uso de DTOs de entrada (request) y salida (response) separados de las entidades. MapStruct para el mapeo.

---

## 📄 Licencia

Proyecto privado — Uso interno de Serviteca SAS.
=======

>>>>>>> a62cdce976901caa50c4c4a79c056b3c513e1f86
