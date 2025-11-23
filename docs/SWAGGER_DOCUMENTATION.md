# Documentación de Swagger/OpenAPI

## Resumen

Tu API REST ahora está completamente documentada con **Swagger/OpenAPI 3.0** utilizando la biblioteca **SpringDoc OpenAPI**. Esto proporciona documentación interactiva, pruebas de endpoints en vivo y generación automática de esquemas.

---

## Acceso a la Documentación

### Después de iniciar tu aplicación:

1. **Swagger UI (Interfaz Interactiva)**
   ```
   http://localhost:8080/api/swagger-ui/index.html
   ```
   - Interfaz visual para explorar y probar todos los endpoints
   - Permite ejecutar peticiones directamente desde el navegador
   - Visualiza esquemas de request/response
   - Incluye ejemplos y descripciones detalladas

2. **OpenAPI JSON**
   ```
   http://localhost:8080/api/v3/api-docs
   ```
   - Especificación OpenAPI en formato JSON
   - Útil para importar en herramientas como Postman, Insomnia, etc.

3. **OpenAPI YAML**
   ```
   http://localhost:8080/api/v3/api-docs.yaml
   ```
   - Especificación OpenAPI en formato YAML

---

## Estructura de la Documentación

### SwaggerConfig.java

Configuración global de OpenAPI que incluye:
- **Título**: API Sistema de Gestión de Notas
- **Versión**: 1.0.0
- **Descripción**: Gestión integral de estudiantes, docentes, cursos y notas
- **Contacto**: Información del equipo de desarrollo
- **Servidores**: URLs de desarrollo y producción
- **Licencia**: MIT License

### Controladores Documentados

Todos los controladores están organizados por etiquetas (Tags):

#### 1. **Cursos** (`/cursos`)
- Crear, actualizar, eliminar cursos
- Buscar cursos por nombre, código, docente
- Activar/desactivar cursos
- Asignar docentes a cursos
- Estadísticas por docente

#### 2. **Docentes** (`/docentes`)
- Crear docente completo (usuario + perfil)
- Crear docente asociado a usuario existente
- Buscar por nombre, especialidad, distrito
- Actualizar perfil de docente
- Obtener docentes activos
- Estadísticas por especialidad

#### 3. **Estudiantes** (`/estudiantes`)
- Crear estudiante completo (usuario + perfil)
- Crear estudiante asociado a usuario existente
- Buscar por nombre, código, distrito
- Actualizar perfil de estudiante
- Obtener estudiantes por usuario
- Estadísticas por distrito

#### 4. **Notas** (`/notas`)
- Registrar calificaciones
- Obtener notas por estudiante/curso
- Calcular promedios generales y por curso
- Obtener estado académico (APROBADO/DESAPROBADO)
- Filtrar por tipo de evaluación
- Obtener mejores notas por curso
- Estadísticas por tipo de evaluación

#### 5. **Usuarios** (`/usuarios`)
- Login con email y contraseña
- Crear usuario con rol específico
- Registro completo (usuario + perfil)
- Gestión de usuarios (CRUD completo)
- Cambiar contraseña
- Activar/desactivar usuarios

---

## Anotaciones Utilizadas

### A nivel de clase:
```java
@Tag(name = "Nombre", description = "Descripción del controlador")
```

### A nivel de método:
```java
@Operation(
    summary = "Título corto del endpoint",
    description = "Descripción detallada de lo que hace"
)

@ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Éxito"),
    @ApiResponse(responseCode = "404", description = "No encontrado"),
    // ...
})
```

### A nivel de parámetros:
```java
@Parameter(description = "Descripción del parámetro", required = true)
```

---

## Extensión Swagger de 42Crunch en VS Code

### ¿Para qué sirve?

La extensión **Swagger Viewer (42Crunch)** proporciona herramientas avanzadas para trabajar con OpenAPI/Swagger:

### Funcionalidades Principales:

1. **Visualización en Tiempo Real**
   - Preview de archivos OpenAPI (`.yaml`, `.json`)
   - Actualización automática al editar
   - Renderizado similar a Swagger UI

2. **Validación y Linting**
   - Detecta errores de sintaxis en especificaciones OpenAPI
   - Verifica cumplimiento con estándares OpenAPI 3.0
   - Sugiere mejoras y buenas prácticas

3. **Análisis de Seguridad**
   - Escaneo de vulnerabilidades en definiciones de API
   - Recomendaciones de seguridad
   - Detección de configuraciones inseguras

4. **Autocompletado Inteligente**
   - Sugerencias de esquemas OpenAPI
   - Validación de referencias ($ref)
   - Snippets para componentes comunes

5. **Navegación Mejorada**
   - Ir a definición de esquemas
   - Ver referencias de componentes
   - Outline de la estructura API

### Cómo Usarla:

#### Ver tu API documentada:

1. Inicia tu aplicación Spring Boot
2. Descarga la especificación OpenAPI:
   ```bash
   curl http://localhost:8080/api/v3/api-docs -o openapi.json
   ```
3. Abre el archivo `openapi.json` en VS Code
4. Click derecho → **"OpenAPI: Show Preview"**

#### Exportar desde tu aplicación:

También puedes agregar el archivo al proyecto automáticamente:

```java
// En application.properties
springdoc.api-docs.path=/api-docs
springdoc.swagger-ui.path=/swagger-ui.html

# Guardar especificación como archivo
springdoc.api-docs.version=OPENAPI_3_0
```

### Comandos Útiles de la Extensión:

- `Ctrl/Cmd + Shift + P` → "OpenAPI: Show Preview"
- `Ctrl/Cmd + Shift + P` → "OpenAPI: Show Two Column Layout"
- `Ctrl/Cmd + Shift + P` → "42Crunch: Security Audit"

---

## Probar la API con Swagger UI

### Pasos:

1. **Inicia tu aplicación**:
   ```bash
   ./mvnw spring-boot:run
   ```

2. **Abre Swagger UI**:
   - Navega a `http://localhost:8080/api/swagger-ui/index.html`

3. **Explora los endpoints**:
   - Expande cualquier sección (Cursos, Docentes, etc.)
   - Click en un endpoint para ver detalles

4. **Ejecuta una petición**:
   - Click en **"Try it out"**
   - Completa los parámetros requeridos
   - Click en **"Execute"**
   - Visualiza la respuesta en tiempo real

### Ejemplo: Crear un Curso

```json
POST /api/cursos

{
  "nombre": "Matemáticas Avanzadas",
  "codigoCurso": "MAT301",
  "descripcion": "Cálculo diferencial e integral",
  "creditos": 4,
  "idDocente": 1
}
```

---

## Integración con Otras Herramientas

### Postman/Insomnia:

1. Exporta la especificación OpenAPI:
   ```
   http://localhost:8080/api/v3/api-docs
   ```
2. En Postman: **Import** → Pega la URL
3. Se generará automáticamente una colección completa

### Generación de Clientes:

Usa **OpenAPI Generator** para crear clientes en diferentes lenguajes:

```bash
# Instalar
npm install @openapitools/openapi-generator-cli -g

# Generar cliente JavaScript
openapi-generator-cli generate -i http://localhost:8080/api/v3/api-docs \
  -g javascript -o ./client-js

# Generar cliente Python
openapi-generator-cli generate -i http://localhost:8080/api/v3/api-docs \
  -g python -o ./client-python
```

---

## Personalización Adicional

### Configurar en application.properties:

```properties
# Cambiar rutas
springdoc.api-docs.path=/api-docs
springdoc.swagger-ui.path=/swagger-ui.html

# Ordenar operaciones
springdoc.swagger-ui.operationsSorter=method

# Habilitar filtro de búsqueda
springdoc.swagger-ui.filter=true

# Modo de visualización
springdoc.swagger-ui.display-request-duration=true
springdoc.swagger-ui.default-models-expand-depth=1
```

### Agregar Autenticación:

Si implementas JWT o Basic Auth:

```java
@Bean
public OpenAPI customOpenAPI() {
    return new OpenAPI()
        .info(new Info()...)
        .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
        .components(new Components()
            .addSecuritySchemes("bearerAuth", 
                new SecurityScheme()
                    .type(SecurityScheme.Type.HTTP)
                    .scheme("bearer")
                    .bearerFormat("JWT")));
}
```

---

## Recursos Adicionales

- **SpringDoc OpenAPI**: https://springdoc.org/
- **OpenAPI Specification**: https://swagger.io/specification/
- **42Crunch Extension**: https://marketplace.visualstudio.com/items?itemName=42Crunch.vscode-openapi
- **Swagger Editor**: https://editor.swagger.io/

---

## Checklist de Verificación

- [x] Dependencia SpringDoc agregada al pom.xml
- [x] SwaggerConfig.java configurado
- [x] Todos los controladores documentados con @Tag
- [x] Endpoints documentados con @Operation
- [x] Respuestas documentadas con @ApiResponses
- [x] Parámetros documentados con @Parameter
- [x] Swagger UI accesible
- [x] Especificación OpenAPI generada

---

## ¡Listo!

Tu API está completamente documentada. Ahora puedes:
- Compartir la documentación con tu equipo
- Generar clientes automáticamente
- Probar endpoints sin herramientas adicionales
- Mantener documentación actualizada automáticamente
