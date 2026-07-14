# Seguridad

## Esquema de Autenticación

El sistema utiliza **JWT (JSON Web Tokens)** con **refresh tokens** para la autenticación, implementando un esquema stateless (sin sesiones en servidor). A diferencia del enfoque tradicional con `OncePerRequestFilter`, la versión actual usa `SecurityContextRepository` para integrarse correctamente con `SecurityContextHolderFilter` de Spring Security 6.2, eliminando la necesidad de workarounds como `.permitAll()`.

```
┌─────────┐         ┌──────────┐         ┌──────────┐
│ Cliente │         │ Backend  │         │ Base de  │
│ (React) │         │ (Spring) │         │  Datos   │
└────┬────┘         └────┬─────┘         └────┬─────┘
     │                   │                    │
     │  POST /auth/login │                    │
     │  {username, pass} │                    │
     │──────────────────►│                    │
     │                   │  SELECT usuario    │
     │                   │───────────────────►│
     │                   │◄───────────────────│
     │                   │                    │
     │  200 {accessToken,│                    │
     │       refreshToken}│                   │
     │◄──────────────────│                    │
     │                   │                    │
     │  GET /api/clientes │                   │
     │  Authorization:   │                    │
     │  Bearer <token>   │                    │
     │──────────────────►│                    │
     │                   │ Validar JWT        │
     │                   │ Extraer username   │
     │                   │                    │
     │  200 {data}       │                    │
     │◄──────────────────│                    │
```

## Componentes de seguridad

### JwtTokenProvider

Clase responsable de:
- **Generar access tokens** con expiración de 1 hora (configurable mediante `app.jwt.expiration-ms`).
- **Generar refresh tokens** con expiración de 24 horas (configurable mediante `app.jwt.refresh-expiration-ms`).
- **Validar tokens** (firma, expiración, integridad).
- **Extraer el username** del token.

Los tokens incluyen los siguientes claims:
- `sub`: Username del usuario.
- `rol`: Nombre del rol (ADMIN, OPERADOR, TECNICO).
- `iat`: Fecha de emisión.
- `exp`: Fecha de expiración.

### JwtSecurityContextRepository (reemplaza a JwtAuthenticationFilter)

Implementación de `SecurityContextRepository` que se ejecuta en cada petición HTTP:

1. Extrae el token del header `Authorization: Bearer <token>`.
2. Valida el token (firma + expiración).
3. Si es válido, extrae el username y carga los detalles del usuario.
4. Crea un `UsernamePasswordAuthenticationToken` y lo envuelve en un `DeferredSecurityContext`.
5. Spring Security 6.2 aplica el contexto automáticamente mediante `SecurityContextHolderFilter`.

**Ventajas sobre el enfoque con `OncePerRequestFilter`:**
- No requiere workaround `.permitAll()` para evitar el filtro en rutas públicas.
- Se integra con el flujo nativo de Spring Security 6.2.
- El contexto se carga bajo demanda (lazy) mediante `SupplierDeferredSecurityContext`.
- Las rutas públicas (`/api/auth/**`, `/actuator/health`) no ejecutan validación JWT.

### RestAuthenticationEntryPoint

Maneja errores de **autenticación (401)**. Cuando un usuario no autenticado intenta acceder a una ruta protegida, retorna:

```json
{
  "success": false,
  "message": "No autenticado. Token requerido.",
  "status": 401,
  "error": "Unauthorized",
  "path": "/api/clientes",
  "timestamp": "2026-06-09T12:00:00"
}
```

### RestAccessDeniedHandler

Maneja errores de **autorización (403)**. Cuando un usuario autenticado no tiene permisos para acceder a un recurso, retorna:

```json
{
  "success": false,
  "message": "Acceso denegado. No tienes permisos para realizar esta acción.",
  "status": 403,
  "error": "Forbidden",
  "path": "/api/usuarios",
  "timestamp": "2026-06-09T12:00:00"
}
```

### SecurityConfig

Configuración principal de seguridad usando `SecurityContextRepository`:

```java
.authorizeHttpRequests(auth -> auth
    .requestMatchers("/api/auth/**").permitAll()
    .requestMatchers("/actuator/health").permitAll()
    .requestMatchers("/api/usuarios/**").hasRole("ADMIN")
    .requestMatchers("/api/roles/**").hasRole("ADMIN")
    .anyRequest().authenticated()
)
.securityContextRepository(new JwtSecurityContextRepository(tokenProvider, userDetailsService))
```

| Ruta | Acceso |
|------|--------|
| `/api/auth/**` | Público (login, register, refresh) |
| `/actuator/health` | Público (health check) |
| `/api/usuarios/**` | Solo ADMIN |
| `/api/roles/**` | Solo ADMIN |
| `/api/**` (demás rutas) | Cualquier usuario autenticado |

**Nota:** No se usa `.permitAll()` como workaround. Las rutas públicas realmente no ejecutan validación JWT.

### UserDetailsServiceImpl

Implementación personalizada de `UserDetailsService` que:
1. Busca el usuario por username en la base de datos (con `@Transactional(readOnly = true)` para evitar LazyInitializationException en la carga del rol).
2. Construye un `User` de Spring Security con:
   - Username.
   - Password (BCrypt).
   - Authorities: `ROLE_ADMIN`, `ROLE_OPERADOR` o `ROLE_TECNICO`.
   - Enabled: basado en el campo `activo` del usuario.

## Roles del sistema

| Rol | Descripción |
|-----|-------------|
| `ADMIN` | Acceso completo a todas las funcionalidades. Gestión de usuarios y roles. |
| `OPERADOR` | Operaciones diarias: clientes, vehículos, órdenes, servicios, productos. |
| `TECNICO` | Acceso limitado a órdenes asignadas y consulta de información. |

## Manejo de contraseñas

- Las contraseñas se almacenan usando **BCrypt** (implementación de Spring Security).
- No se almacenan contraseñas en texto plano.
- No se exponen contraseñas en ninguna respuesta de la API.

## CORS

Configuración para desarrollo:

```yaml
allowedOrigins: http://localhost:5173
allowedMethods: GET, POST, PUT, DELETE, PATCH
allowedHeaders: *
allowCredentials: true
```

En producción, CORS permite el origen del frontend desplegado.

## Refresh Token Flow

1. El cliente obtiene `accessToken` y `refreshToken` al hacer login.
2. Cuando el `accessToken` expira (1 hora), el cliente intenta renovarlo.
3. El interceptor de Axios detecta el error 401 y automáticamente:
   - Envía el `refreshToken` a `POST /api/auth/refresh`.
   - Si es válido, obtiene nuevos tokens y reintenta la petición original.
   - Si el refresh también expira, redirige al login.

## Health Check

El endpoint `/actuator/health` está expuesto públicamente para monitoreo:

- **Perfil dev**: Muestra detalles completos (DB, disk space, ping).
- **Perfil prod**: Muestra solo estado UP/ DOWN (sin detalles).
- Se usa en `docker-compose.yml` como healthcheck del backend.

## Protección en el Frontend

- **AuthContext**: Provee el estado de autenticación a toda la aplicación.
- **ProtectedRoute**: Componente que redirige a `/login` si el usuario no está autenticado.
- **Interceptor Axios**: Agrega automáticamente el token JWT a todas las peticiones y maneja la renovación automática.

## Buenas prácticas de seguridad

1. **Nunca** almacenar secretos en el código fuente.
2. El `JWT_SECRET` debe ser una clave segura y diferente en producción (configurable vía `app.jwt.secret` o variable de entorno).
3. Todas las validaciones se realizan en el backend (el frontend es solo para UX).
4. Uso de `@Valid` en todos los DTOs de entrada.
5. Manejo centralizado de excepciones para evitar fugas de información.
6. Las contraseñas nunca se incluyen en logs.
7. Política de CORS restrictiva en producción.
8. Variables de entorno externalizadas en `.env.example` y requeridas en `application-prod.yml`.
