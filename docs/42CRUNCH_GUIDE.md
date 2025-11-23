# Guía Rápida de Uso: Extensión Swagger (42Crunch)

## ¿Qué es 42Crunch Swagger Viewer?

Es una extensión de VS Code que te permite visualizar, validar y trabajar con especificaciones OpenAPI/Swagger directamente en tu editor.

---

## Exportar tu API desde Spring Boot

### Opción 1: Descargar manualmente

```bash
# Con la aplicación corriendo
curl http://localhost:8080/api/v3/api-docs > openapi.json

# O en formato YAML
curl http://localhost:8080/api/v3/api-docs.yaml > openapi.yaml
```

### Opción 2: Usar PowerShell (Windows)

```powershell
# Descargar JSON
Invoke-WebRequest -Uri "http://localhost:8080/api/v3/api-docs" -OutFile "openapi.json"

# Descargar YAML
Invoke-WebRequest -Uri "http://localhost:8080/api/v3/api-docs.yaml" -OutFile "openapi.yaml"
```

---

## Visualizar tu API con la Extensión

### Método 1: Preview Rápido

1. Abre el archivo `openapi.json` o `openapi.yaml`
2. Presiona `Ctrl + Shift + P` (Windows/Linux) o `Cmd + Shift + P` (Mac)
3. Escribe: **"OpenAPI: Show Preview"**
4. Se abrirá un panel lateral con la visualización

### Método 2: Botón de Preview

1. Abre el archivo OpenAPI
2. Busca el icono de "ojo" en la esquina superior derecha
3. Click para abrir la vista previa

### Método 3: Split View (Vista Dividida)

1. `Ctrl/Cmd + Shift + P`
2. Escribe: **"OpenAPI: Show Two Column Layout"**
3. Editas el código a la izquierda, ves el preview a la derecha

---

## Funciones Principales

### 1. Navegación Inteligente

**Ver Outline (Estructura)**:
- Click en el icono de lista en el explorador
- Ve todos los endpoints organizados
- Click en cualquier endpoint para saltar a su definición

**Ir a Definición**:
- Coloca el cursor sobre un `$ref`
- Presiona `F12` o `Ctrl + Click`
- Te lleva directamente a la definición del componente

### 2. Validación Automática

**Ver errores en tiempo real**:
- Los errores aparecen subrayados en rojo
- Hover sobre el error para ver el mensaje
- Panel de "Problems" muestra todos los errores

**Tipos de validación**:
- Sintaxis JSON/YAML correcta
- Cumplimiento con estándares OpenAPI 3.0
- Referencias válidas (`$ref`)
- Tipos de datos correctos
- Campos requeridos presentes

### 3. Análisis de Seguridad

**Ejecutar audit de seguridad**:

1. `Ctrl/Cmd + Shift + P`
2. Escribe: **"42Crunch: Security Audit"**
3. Ve el reporte con:
   - Vulnerabilidades encontradas
   - Nivel de severidad (Critical, High, Medium, Low)
   - Recomendaciones de corrección

**Problemas comunes detectados**:
- Falta de autenticación/autorización
- Endpoints sin validación
- Información sensible expuesta
- CORS mal configurado
- Rate limiting ausente

### 4. Autocompletado

**Mientras editas**:
- Presiona `Ctrl + Space` para sugerencias
- Aparecen snippets de componentes comunes
- Completa automáticamente nombres de propiedades
- Sugiere valores válidos para enums

---

## Personalizar la Vista

### Configuración de la Extensión

**Acceder a configuración**:
1. `Ctrl/Cmd + ,` para abrir Settings
2. Busca: "42Crunch" o "OpenAPI"

**Opciones útiles**:

```json
{
  // Actualización automática del preview
  "openapi.preview.autoRefresh": true,
  
  // Mostrar descripción completa
  "openapi.preview.showDescription": true,
  
  // Tema del preview
  "openapi.preview.theme": "dark", // o "light"
  
  // Validación estricta
  "openapi.validation.strict": true
}
```

---

## Casos de Uso Prácticos

### Caso 1: Documentar Nuevo Endpoint

**Flujo de trabajo**:

1. Inicia tu app Spring Boot
2. Descarga la especificación actualizada:
   ```bash
   curl http://localhost:8080/api/v3/api-docs > openapi.json
   ```
3. Abre en VS Code con split view
4. Verifica que el nuevo endpoint esté documentado
5. Revisa descripciones y esquemas
6. Ejecuta security audit

### Caso 2: Compartir API con Frontend

1. Genera el archivo OpenAPI actualizado
2. Comparte `openapi.json` con el equipo frontend
3. Ellos pueden:
   - Importarlo en Postman/Insomnia
   - Generar clientes TypeScript/JavaScript
   - Ver documentación interactiva

### Caso 3: Detección de Problemas

**La extensión te ayuda a encontrar**:

```yaml
# Problema: Falta descripción
/api/cursos:
  get:
    summary: Get courses

# Solución: Agregar descripción
/api/cursos:
  get:
    summary: Obtener todos los cursos
    description: Recupera la lista completa de cursos...
```

---

## Integración con Git

### Guardar la Especificación en el Repo

**Estructura recomendada**:

```
gestion-notas-backend/
├── src/
├── docs/
│   └── api/
│       ├── openapi.json
│       └── openapi.yaml
├── pom.xml
└── README.md
```

**Script para actualizar**:

```bash
#!/bin/bash
# update-openapi.sh

# Asegurar que la app esté corriendo
curl -f http://localhost:8080/api/v3/api-docs > docs/api/openapi.json
curl -f http://localhost:8080/api/v3/api-docs.yaml > docs/api/openapi.yaml

echo "Especificación OpenAPI actualizada"
```

**PowerShell version**:

```powershell
# update-openapi.ps1

Invoke-WebRequest -Uri "http://localhost:8080/api/v3/api-docs" -OutFile "docs/api/openapi.json"
Invoke-WebRequest -Uri "http://localhost:8080/api/v3/api-docs.yaml" -OutFile "docs/api/openapi.yaml"

Write-Host "Especificación OpenAPI actualizada" -ForegroundColor Green
```

---

## Comparar Versiones de API

### Detectar Cambios (Breaking Changes)

**Con la extensión**:

1. Guarda versión anterior: `openapi-v1.0.json`
2. Genera nueva versión: `openapi-v1.1.json`
3. Abre ambos archivos lado a lado
4. Usa VS Code: `Ctrl + Shift + P` → "Compare Active File With..."
5. Identifica cambios importantes

**Herramienta externa recomendada**:

```bash
# Instalar oasdiff
npm install -g oasdiff

# Comparar dos versiones
oasdiff diff openapi-v1.0.json openapi-v1.1.json

# Ver solo breaking changes
oasdiff breaking openapi-v1.0.json openapi-v1.1.json
```

---

## Comandos Rápidos

### Atajos de Teclado

| Acción | Windows/Linux | Mac |
|--------|---------------|-----|
| Abrir Preview | `Ctrl + Shift + P` → "OpenAPI: Show Preview" | `Cmd + Shift + P` |
| Ir a Definición | `F12` | `F12` |
| Ver Referencias | `Shift + F12` | `Shift + F12` |
| Formatear Documento | `Shift + Alt + F` | `Shift + Option + F` |
| Panel de Problemas | `Ctrl + Shift + M` | `Cmd + Shift + M` |
| Security Audit | `Ctrl + Shift + P` → "42Crunch: Security Audit" | `Cmd + Shift + P` |

---

## Tips y Trucos

### 1. Live Reload

Activa auto-refresh para que el preview se actualice automáticamente:

```json
"openapi.preview.autoRefresh": true
```

### 2. Validar Antes de Commit

Agrega un pre-commit hook:

```bash
# .git/hooks/pre-commit

#!/bin/bash
if [ -f "docs/api/openapi.json" ]; then
    echo "Validando especificación OpenAPI..."
    npx @apidevtools/swagger-cli validate docs/api/openapi.json
    
    if [ $? -ne 0 ]; then
        echo "Especificación OpenAPI inválida"
        exit 1
    fi
fi
```

### 3. Snippets Personalizados

Crea snippets para componentes comunes:

```json
// .vscode/openapi.code-snippets
{
  "New Endpoint": {
    "scope": "json,yaml",
    "prefix": "oapi-endpoint",
    "body": [
      "\"${1:path}\": {",
      "  \"${2:get}\": {",
      "    \"summary\": \"${3:Summary}\",",
      "    \"description\": \"${4:Description}\",",
      "    \"responses\": {",
      "      \"200\": {",
      "        \"description\": \"Successful response\"",
      "      }",
      "    }",
      "  }",
      "}"
    ]
  }
}
```

---

## Características Avanzadas

### 1. Linting Personalizado

Configura reglas específicas para tu equipo:

```json
// .spectral.yaml
extends: spectral:oas
rules:
  operation-description: error
  operation-tags: error
  info-contact: error
  info-description: error
```

### 2. Generar Mocks

Usa la especificación para crear un servidor mock:

```bash
# Con Prism
npm install -g @stoplight/prism-cli

# Iniciar mock server
prism mock docs/api/openapi.json

# Ahora puedes hacer requests a http://localhost:4010
```

### 3. Testing Automatizado

```bash
# Con Dredd
npm install -g dredd

# Probar API contra especificación
dredd docs/api/openapi.yaml http://localhost:8080
```

---

## Checklist de Mejores Prácticas

- [ ] Especificación OpenAPI en el repositorio
- [ ] Preview revisado antes de cada commit
- [ ] Security audit ejecutado regularmente
- [ ] Sin errores de validación
- [ ] Descripciones completas en todos los endpoints
- [ ] Ejemplos incluidos en request/response
- [ ] Esquemas reutilizables en `components`
- [ ] Versión de API actualizada en `info.version`

---

## Recursos Útiles

- **Documentación 42Crunch**: https://docs.42crunch.com/
- **OpenAPI Guide**: https://oai.github.io/Documentation/
- **Swagger Best Practices**: https://swagger.io/docs/specification/about/
- **VS Code API**: https://code.visualstudio.com/api

---

¡Ahora tienes todas las herramientas para documentar y mantener tu API profesionalmente!
