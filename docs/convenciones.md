# Convenciones del Proyecto

Estas reglas son **obligatorias** para todo el código del proyecto. Mantenerlas garantiza consistencia, legibilidad y mantenibilidad.

---

## Nomenclatura

### Backend (Java)

| Elemento | Convención | Ejemplo |
|----------|-----------|---------|
| Paquetes | minúsculas, singular | `com.serviteca.cliente`, `com.serviteca.orden` |
| Clases | PascalCase | `ClienteService`, `OrdenTrabajo` |
| Interfaces | PascalCase | `ClienteRepository` |
| Métodos | camelCase | `findAll()`, `findByClienteId()` |
| Variables | camelCase | `nombreCliente`, `fechaIngreso` |
| Constantes | UPPER_SNAKE_CASE | `ESTADOS_VALIDOS`, `MAX_INTENTOS` |
| Atributos JPA | snake_case (BD) | `fecha_creacion`, `numero_documento` |
| Tablas BD | snake_case, plural | `ordenes_trabajo`, `movimientos_inventario` |

### Frontend (TypeScript/React)

| Elemento | Convención | Ejemplo |
|----------|-----------|---------|
| Archivos TS/TSX | PascalCase para componentes | `LoginPage.tsx`, `DataTable.tsx` |
| Archivos TS (no componente) | camelCase | `api.ts`, `formatters.ts` |
| Componentes React | PascalCase | `ProtectedRoute`, `MainLayout` |
| Funciones hooks | camelCase, prefijo `use` | `useAuth()` |
| Variables | camelCase | `accessToken`, `userData` |
| Interfaces | PascalCase | `LoginResponse`, `Cliente` |
| Tipos | PascalCase | `ApiResponse<T>` |
| Carpetas | minúsculas, plural | `pages`, `services`, `components/ui` |

---

## Estructura de módulos (Backend)

Cada módulo debe seguir exactamente esta estructura:

```
modulo/
├── controller/
│   └── XxxController.java
├── service/
│   └── XxxService.java
├── repository/
│   └── XxxRepository.java
├── entity/
│   └── Xxx.java
├── dto/
│   ├── XxxRequest.java
│   └── XxxResponse.java
├── mapper/
│   └── XxxMapper.java
├── validator/          (opcional)
│   └── XxxValidator.java
└── exception/          (opcional)
    └── XxxException.java
```

---

## DTOs

- **Request DTO**: Solo campos necesarios para crear/actualizar. Validaciones con Jakarta Validation.
- **Response DTO**: Puede contener campos adicionales (nombres de relaciones, fechas formateadas, etc.).
- No exponer entidades JPA en los controllers.
- Los DTOs son clases planas con getters/setters.

```java
// Request - Solo campos de entrada
public class ClienteRequest {
    @NotBlank String tipoDocumento;
    @NotBlank String numeroDocumento;
    @NotBlank String nombre;
    // ...
}

// Response - Puede incluir datos de relaciones
public class ClienteResponse {
    Long id;
    String tipoDocumento;
    String numeroDocumento;
    String nombre;
    boolean activo;
    // ...
}
```

---

## Services

- Toda la lógica de negocio va en los services.
- Los services reciben DTOs y devuelven DTOs (nunca entidades).
- Inyección de dependencias por constructor (no `@Autowired` en campos).
- Anotar con `@Service`.
- `@Transactional` en métodos que modifican múltiples tablas.

```java
@Service
public class ClienteService {
    private final ClienteRepository clienteRepository;

    public ClienteService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    public ClienteResponse create(ClienteRequest request) {
        // lógica de negocio
    }
}
```

---

## Repositories

- Extienden de `JpaRepository<T, Long>`.
- Nombres descriptivos para consultas derivadas: `findByNumeroDocumento`, `existsByPlaca`.
- Consultas JPQL con `@Query` para búsquedas complejas.
- Anotar con `@Repository`.

---

## Controllers

- Anotar con `@RestController` y `@RequestMapping("/api/xxx")`.
- Métodos cortos: solo reciben parámetros, llaman al service, devuelven `ResponseEntity<ApiResponse<T>>`.
- Validar con `@Valid`.
- Inyección de dependencias por constructor.

```java
@RestController
@RequestMapping("/api/clientes")
public class ClienteController {
    private final ClienteService clienteService;

    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ClienteResponse>>> findAll() {
        return ResponseEntity.ok(ApiResponse.success(clienteService.findAll()));
    }
}
```

---

## Excepciones

- Usar excepciones personalizadas del paquete `shared/exception/`.
- No capturar excepciones en los controllers (el `GlobalExceptionHandler` se encarga).
- Tipos de excepción:
  - `ResourceNotFoundException` → 404
  - `BadRequestException` → 400
  - `DuplicateResourceException` → 409
  - `AccessDeniedException` → 403 (de Spring Security)

---

## Mappers

- Cada módulo tiene su propio mapper.
- Métodos: `toResponse(Entity)` y opcionalmente `toEntity(Request)` o `updateEntity(Request, Entity)`.
- Sin dependencias externas (mappers manuales).

---

## Validadores

- Validaciones de negocio que requieren acceso a base de datos (unicidad, reglas de dominio).
- Se inyectan en el service correspondiente.

---

## Formato de commits

```
<tipo>: <descripción breve>

<tipo>: feat | fix | refactor | docs | test | chore
```

Ejemplos:
```
feat: agregar CRUD de clientes con paginación
fix: validar placa duplicada al actualizar vehículo
docs: documentar endpoint de login
refactor: extraer lógica de validación a ClienteValidator
```

---

## Formato de ramas

```
main        → Producción
develop     → Desarrollo
feature/xxx → Nueva funcionalidad
fix/xxx     → Corrección de bug
docs/xxx    → Documentación
```

Ejemplos:
```
feature/crud-clientes
fix/validacion-placa-duplicada
docs/api-endpoints
```

---

## Convenciones generales

1. **No duplicar código**. Si se repite más de dos veces, extraer a método/clase compartida.
2. **No comentarios innecesarios**. El código debe autodocumentarse con nombres descriptivos.
3. **Métodos cortos**. Un método no debe superar las 30 líneas.
4. **Una responsabilidad por clase**. Si una clase hace dos cosas, dividir.
5. **Inyección por constructor**. Nunca usar `@Autowired` en campos.
6. **`@Transactional` en services**, no en controllers.
7. **Validar siempre en el backend**. El frontend puede validar para UX, pero el backend es la autoridad.
8. **Nunca exponer entidades JPA**. Siempre usar DTOs.
9. **Logs en DEBUG para desarrollo**, INFO para producción.
10. **Las respuestas siempre envueltas en `ApiResponse<T>`**.
