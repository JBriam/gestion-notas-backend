# 🎯 Documentación API con Swagger - Guía Rápida

## 📚 Archivos de Documentación

Tu proyecto ahora incluye documentación completa sobre Swagger/OpenAPI:

| Archivo | Descripción |
|---------|-------------|
| **SWAGGER_SUMMARY.md** | ✅ Resumen ejecutivo de todo lo implementado |
| **SWAGGER_DOCUMENTATION.md** | 📖 Guía completa de uso de Swagger/OpenAPI |
| **42CRUNCH_GUIDE.md** | 🔧 Cómo usar la extensión 42Crunch en VS Code |
| **SWAGGER_COMMANDS.md** | ⚡ Comandos útiles y scripts de automatización |

---

## 🚀 Inicio Rápido

### 1. Inicia tu aplicación

```bash
./mvnw spring-boot:run
```

### 2. Accede a Swagger UI

Abre tu navegador en:
```
http://localhost:8080/api/swagger-ui.html
```

### 3. Explora y prueba tu API

- 📋 Ve todos los endpoints organizados por módulos
- 🧪 Prueba cualquier endpoint con "Try it out"
- 📊 Visualiza esquemas de request/response
- 📝 Lee descripciones detalladas

---

## 📖 ¿Qué se documentó?

### ✅ Controladores Completos

- **Cursos** - 15 endpoints
- **Docentes** - 13 endpoints  
- **Estudiantes** - 12 endpoints
- **Notas** - 15 endpoints
- **Usuarios** - 12 endpoints

**Total: 67+ endpoints completamente documentados**

### ✅ Información incluida para cada endpoint

- Resumen (summary)
- Descripción detallada
- Parámetros requeridos/opcionales
- Tipos de datos
- Códigos de respuesta (200, 201, 404, etc.)
- Esquemas de request/response
- Ejemplos

---

## 🔍 Extensión 42Crunch

### ¿Para qué sirve?

La extensión **Swagger Viewer (42Crunch)** en VS Code te permite:

#### 1. **Visualizar** 📺
- Preview en tiempo real de archivos OpenAPI
- Vista dividida (código + preview)
- Navegación inteligente por la estructura

#### 2. **Validar** ✅
- Detecta errores de sintaxis
- Verifica cumplimiento con OpenAPI 3.0
- Valida referencias y tipos de datos

#### 3. **Análisis de Seguridad** 🔒
- Escanea vulnerabilidades
- Recomendaciones de seguridad
- Detecta configuraciones inseguras

#### 4. **Productividad** ⚡
- Autocompletado inteligente
- Snippets de código
- Ir a definición rápido

### Cómo usarla

1. **Exporta tu API**:
   ```bash
   curl http://localhost:8080/api/api-docs > openapi.json
   ```

2. **Abre el archivo en VS Code**

3. **Activa el preview**:
   - Click derecho → "OpenAPI: Show Preview"
   - O presiona `Ctrl+Shift+P` y busca "OpenAPI: Show Preview"

4. **Ejecuta security audit**:
   - `Ctrl+Shift+P` → "42Crunch: Security Audit"

---

## 📥 Exportar Especificación

### JSON
```bash
curl http://localhost:8080/api/api-docs > openapi.json
```

### YAML
```bash
curl http://localhost:8080/api/v3/api-docs.yaml > openapi.yaml
```

### PowerShell (Windows)
```powershell
Invoke-WebRequest -Uri "http://localhost:8080/api/api-docs" -OutFile "openapi.json"
```

---

## 🤖 Generar Clientes Automáticamente

### JavaScript/TypeScript
```bash
npm install -g @openapitools/openapi-generator-cli
openapi-generator-cli generate -i openapi.json -g typescript-axios -o ./client
```

### Python
```bash
openapi-generator-cli generate -i openapi.json -g python -o ./client
```

### Java
```bash
openapi-generator-cli generate -i openapi.json -g java -o ./client
```

---

## 🧪 Probar API con Swagger UI

### Ejemplo: Crear un curso

1. Ve a Swagger UI: `http://localhost:8080/api/swagger-ui.html`
2. Expande la sección **"Cursos"**
3. Click en **POST /cursos**
4. Click en **"Try it out"**
5. Ingresa el JSON:
   ```json
   {
     "nombre": "Matemáticas Avanzadas",
     "codigoCurso": "MAT301",
     "descripcion": "Cálculo diferencial e integral",
     "creditos": 4,
     "idDocente": 1
   }
   ```
6. Click en **"Execute"**
7. Ve la respuesta en tiempo real

---

## 📦 Integración con Postman

1. Abre Postman
2. Click en **Import**
3. Pega la URL: `http://localhost:8080/api/api-docs`
4. Se generará automáticamente una colección completa con todos los endpoints

---

## 🎨 Personalización

### Configuración actual en `application.properties`:

```properties
# Rutas personalizadas
springdoc.api-docs.path=/api-docs
springdoc.swagger-ui.path=/swagger-ui.html

# Ordenamiento y filtros
springdoc.swagger-ui.operationsSorter=method
springdoc.swagger-ui.tagsSorter=alpha
springdoc.swagger-ui.filter=true

# Funcionalidades adicionales
springdoc.swagger-ui.display-request-duration=true
springdoc.swagger-ui.try-it-out-enabled=true
```

---

## 📚 Recursos

### Documentación Interna
- Lee **SWAGGER_DOCUMENTATION.md** para guía completa
- Consulta **42CRUNCH_GUIDE.md** para usar la extensión
- Revisa **SWAGGER_COMMANDS.md** para comandos avanzados
- Verifica **SWAGGER_SUMMARY.md** para resumen completo

### Enlaces Externos
- [SpringDoc OpenAPI](https://springdoc.org/)
- [OpenAPI Specification](https://swagger.io/specification/)
- [42Crunch Extension](https://marketplace.visualstudio.com/items?itemName=42Crunch.vscode-openapi)
- [Swagger Editor](https://editor.swagger.io/)

---

## ✨ Beneficios

### Para ti:
- ✅ Documentación automática y siempre actualizada
- ✅ Pruebas de API sin herramientas adicionales
- ✅ Exportación a múltiples formatos
- ✅ Generación automática de clientes

### Para tu equipo:
- ✅ Frontend puede ver contratos claros
- ✅ QA puede automatizar pruebas
- ✅ Nuevos miembros entienden la API rápidamente
- ✅ Colaboración más eficiente

---

## 🎯 Próximos Pasos Sugeridos

1. **Prueba la documentación**
   - Inicia tu app
   - Explora Swagger UI
   - Prueba algunos endpoints

2. **Exporta la especificación**
   - Descarga openapi.json
   - Ábrelo con la extensión 42Crunch
   - Ejecuta un security audit

3. **Integra con tu workflow**
   - Agrega scripts de validación
   - Configura pre-commit hooks
   - Automatiza generación de clientes

4. **Comparte con el equipo**
   - Envía la URL de Swagger UI
   - Comparte archivos de documentación
   - Genera colección de Postman

---

## 🆘 Solución de Problemas

### Swagger UI no carga
```bash
# Verifica que la aplicación esté corriendo
curl http://localhost:8080/api/actuator/health

# Limpia y recompila
./mvnw clean install
```

### Cambios no se reflejan
```bash
# Limpia caché de Maven
./mvnw clean

# Recompila
./mvnw compile

# Reinicia la aplicación
```

### Errores de compilación
```bash
# Actualiza dependencias
./mvnw dependency:resolve

# Verifica versiones
./mvnw dependency:tree
```

---

## 💡 Tips

- 🔄 **Mantén la documentación actualizada**: Swagger se genera automáticamente
- 📝 **Escribe buenas descripciones**: Son el valor real de la documentación
- 🧪 **Usa "Try it out"**: Es la forma más rápida de probar endpoints
- 📦 **Exporta regularmente**: Mantén una copia en tu repositorio
- 🔍 **Usa 42Crunch**: Para validar y asegurar tu API

---

## ✅ Checklist

- [x] Swagger configurado
- [x] Todos los controladores documentados
- [x] Swagger UI accesible
- [x] Especificación OpenAPI generada
- [x] Documentación completa creada
- [x] Extensión 42Crunch explicada
- [x] Comandos útiles documentados

---

**🎉 ¡Tu API está completamente documentada!**

Ahora tienes una documentación profesional, interactiva y siempre actualizada para tu API REST de Gestión de Notas.

---

**Última actualización**: Noviembre 2025  
**Versión de la API**: 1.0.0  
**SpringDoc OpenAPI**: 2.8.14
