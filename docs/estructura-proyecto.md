# Estructura del Proyecto

## Raíz del proyecto

```
serviteca-sas/
├── backend/                # Aplicación Spring Boot
├── frontend/               # Aplicación React + Vite
├── docs/                   # Documentación del proyecto
├── .env.example            # Template de variables de entorno
└── docker-compose.yml      # Servicios Docker (PostgreSQL + Backend + Frontend + pgAdmin)
```

---

## Backend (`backend/`)

```
backend/
├── pom.xml
├── .gitignore
└── src/
    ├── main/
    │   ├── java/com/serviteca/
    │   │   ├── ServitecaApplication.java      # Punto de entrada
    │   │   ├── auth/                           # Autenticación
    │   │   │   ├── controller/
    │   │   │   ├── service/
    │   │   │   └── dto/
    │   │   ├── security/                       # Seguridad JWT
    │   │   │   ├── jwt/
    │   │   │   │   ├── JwtTokenProvider.java       # Generación/validación de tokens
    │   │   │   │   └── JwtSecurityContextRepository.java  # SecurityContextRepository (Spring Security 6.2)
    │   │   │   └── config/
    │   │   │       ├── SecurityConfig.java             # Configuración de seguridad
    │   │   │       ├── RestAuthenticationEntryPoint.java  # 401 en JSON
    │   │   │       └── RestAccessDeniedHandler.java       # 403 en JSON
    │   │   ├── shared/                         # Capa transversal
    │   │   │   ├── config/
    │   │   │   ├── dto/
    │   │   │   ├── exception/
    │   │   │   └── util/
    │   │   ├── usuario/                        # Módulo usuarios
    │   │   ├── rol/                            # Módulo roles
    │   │   ├── cliente/                        # Módulo clientes
    │   │   ├── vehiculo/                       # Módulo vehículos
    │   │   ├── orden/                          # Módulo órdenes
    │   │   ├── servicio/                       # Módulo servicios
    │   │   ├── categoria/                      # Módulo categorías
    │   │   ├── producto/                       # Módulo productos
    │   │   ├── proveedor/                      # Módulo proveedores
    │   │   ├── inventario/                     # Módulo inventario
    │   │   └── dashboard/                      # Módulo dashboard
    │   └── resources/
    │       ├── application.yml                 # Config principal (logs, actuator, etc.)
    │       ├── application-dev.yml             # Config desarrollo
    │       ├── application-prod.yml            # Config producción
    │       ├── application-test.yml            # Config tests (H2 modo PostgreSQL)
    │       └── db/migration/
    │           ├── V001__initial_schema.sql    # Esquema inicial
    │           ├── V002__seed_data.sql         # Datos semilla
    │           └── V003__...                   # Migraciones adicionales
    └── test/
        └── java/com/serviteca/
            ├── integration/
            │   ├── AbstractIntegrationTest.java           # Base con perfil test
            │   ├── AuthIntegrationTest.java               # 4 tests (login, refresh, register, 401)
            │   ├── ClienteVehiculoIntegrationTest.java    # 4 tests (CRUD cliente, vehiculo, auth)
            │   └── OrdenTrabajoIntegrationTest.java       # 3 tests (crear, flujo estados, health)
            └── ... (unit tests)
```

### Estructura de cada módulo

Cada módulo sigue la misma estructura interna:

```
modulo/
├── controller/      # Controladores REST
├── service/         # Lógica de negocio
├── repository/      # Acceso a datos (Spring Data JPA)
├── entity/          # Entidades JPA
├── dto/             # DTOs de request y response
├── mapper/          # Mapeo entity ↔ dto
├── validator/       # Validaciones de negocio (opcional)
└── exception/       # Excepciones específicas (opcional)
```

### Responsabilidades de cada módulo

| Módulo | Responsabilidad |
|--------|----------------|
| `shared` | Clases base, excepciones globales, DTOs genéricos, configuración compartida |
| `security` | JWT (SecurityContextRepository), configuración de seguridad, CORS, handlers 401/403 |
| `auth` | Login, registro, refresh de tokens |
| `usuario` | CRUD de usuarios del sistema |
| `rol` | CRUD de roles |
| `cliente` | CRUD de clientes (con paginación y búsqueda) |
| `vehiculo` | CRUD de vehículos asociados a clientes |
| `orden` | Órdenes de trabajo, cambio de estados, gestión de servicios/productos asociados |
| `servicio` | Catálogo de servicios ofrecidos |
| `categoria` | Categorías para agrupar servicios/productos |
| `producto` | Catálogo de productos e insumos |
| `proveedor` | Proveedores de productos |
| `inventario` | Control de stock, movimientos de entrada/salida |
| `dashboard` | Indicadores y resúmenes del sistema |

---

## Frontend (`frontend/`)

```
frontend/
├── index.html
├── package.json
├── vite.config.ts
├── tsconfig.json
├── tailwind.config.js
├── postcss.config.js
├── .gitignore
└── src/
    ├── main.tsx                  # Punto de entrada
    ├── App.tsx                   # Router principal
    ├── index.css                 # Estilos globales + Tailwind
    ├── components/               # Componentes reutilizables
    │   └── ui/
    │       ├── DataTable.tsx     # Tabla genérica con columnas configurables
    │       └── Modal.tsx         # Modal genérico
    ├── pages/                    # Páginas del sistema
    │   ├── auth/
    │   │   └── LoginPage.tsx
    │   ├── dashboard/
    │   │   └── DashboardPage.tsx
    │   ├── usuarios/
    │   │   └── UsuariosPage.tsx
    │   ├── clientes/
    │   ├── vehiculos/
    │   ├── ordenes/
    │   ├── servicios/
    │   ├── productos/
    │   ├── inventario/
    │   ├── proveedores/
    │   └── categorias/
    ├── layouts/                  # Layouts de página
    │   ├── MainLayout.tsx        # Layout principal (sidebar + header)
    │   └── Sidebar.tsx           # Navegación lateral
    ├── modules/                  # Lógica específica de módulos (futuro)
    ├── hooks/                    # Custom hooks
    ├── services/                 # Servicios HTTP
    │   └── api.ts                # Axios instance con interceptors JWT
    ├── routes/                   # Configuración de rutas
    │   └── ProtectedRoute.tsx    # Ruta protegida con autenticación
    ├── contexts/                 # Contextos de React
    │   └── AuthContext.tsx       # Contexto de autenticación
    ├── utils/                    # Utilidades
    │   └── formatters.ts         # Formatos de moneda, fecha, estados
    ├── types/                    # Tipos TypeScript
    │   └── index.ts              # Interfaces de todos los modelos
    └── assets/                   # Recursos estáticos
```

### Responsabilidades Frontend

| Carpeta | Responsabilidad |
|---------|----------------|
| `components/ui` | Componentes UI reutilizables (DataTable, Modal) |
| `pages/*` | Páginas completas que combinan componentes y lógica |
| `layouts` | Estructura visual (sidebar, header) |
| `services` | Configuración de Axios con interceptors |
| `contexts` | Estado global (autenticación) |
| `routes` | Protección de rutas |
| `utils` | Funciones auxiliares de formato |
| `types` | Tipos TypeScript que reflejan los DTOs del backend |

---

## Buenas prácticas

1. **Unidireccional**: Frontend → API → Backend → BD. Nunca al revés.
2. **DTOs**: El frontend nunca recibe entidades JPA directamente.
3. **Validación doble**: El backend valida siempre, el frontend valida para UX.
4. **Misma estructura**: Cada módulo backend tiene la misma estructura interna.
5. **Nomenclatura consistente**: Los nombres de archivos y carpetas siguen convenciones establecidas.
6. **Responsabilidad única**: Cada archivo hace una sola cosa.
