# 🚀 Comandos Útiles - Swagger/OpenAPI

## 📥 Exportar Especificación OpenAPI

### Windows (PowerShell)

```powershell
# Exportar como JSON
Invoke-WebRequest -Uri "http://localhost:8080/api/api-docs" -OutFile "openapi.json"

# Exportar como YAML
Invoke-WebRequest -Uri "http://localhost:8080/api/v3/api-docs.yaml" -OutFile "openapi.yaml"

# Crear carpeta de documentación y exportar
New-Item -ItemType Directory -Force -Path "docs/api"
Invoke-WebRequest -Uri "http://localhost:8080/api/api-docs" -OutFile "docs/api/openapi.json"
Invoke-WebRequest -Uri "http://localhost:8080/api/v3/api-docs.yaml" -OutFile "docs/api/openapi.yaml"
```

### Linux/Mac (Bash)

```bash
# Exportar como JSON
curl http://localhost:8080/api/api-docs -o openapi.json

# Exportar como YAML
curl http://localhost:8080/api/v3/api-docs.yaml -o openapi.yaml

# Crear carpeta y exportar
mkdir -p docs/api
curl http://localhost:8080/api/api-docs -o docs/api/openapi.json
curl http://localhost:8080/api/v3/api-docs.yaml -o docs/api/openapi.yaml
```

---

## 🔍 Validar Especificación

### Con swagger-cli (Node.js)

```bash
# Instalar
npm install -g @apidevtools/swagger-cli

# Validar
swagger-cli validate openapi.json
swagger-cli validate openapi.yaml

# Validar y ver detalles
swagger-cli validate openapi.json --debug
```

### Con openapi-generator (Java)

```bash
# Descargar JAR
wget https://repo1.maven.org/maven2/org/openapitools/openapi-generator-cli/7.0.1/openapi-generator-cli-7.0.1.jar -O openapi-generator-cli.jar

# Validar
java -jar openapi-generator-cli.jar validate -i openapi.yaml
```

---

## 🤖 Generar Clientes

### JavaScript/TypeScript

```bash
# Instalar generador
npm install -g @openapitools/openapi-generator-cli

# Generar cliente JavaScript
openapi-generator-cli generate \
  -i openapi.json \
  -g javascript \
  -o ./client-js

# Generar cliente TypeScript (Axios)
openapi-generator-cli generate \
  -i openapi.json \
  -g typescript-axios \
  -o ./client-ts
```

### Python

```bash
# Cliente Python
openapi-generator-cli generate \
  -i openapi.json \
  -g python \
  -o ./client-python

# Con Poetry
openapi-generator-cli generate \
  -i openapi.json \
  -g python \
  -o ./client-python \
  --additional-properties=packageName=gestion_notas_client
```

### Java

```bash
# Cliente Java con RestTemplate
openapi-generator-cli generate \
  -i openapi.json \
  -g java \
  -o ./client-java \
  --library=resttemplate

# Cliente Java con OkHttp
openapi-generator-cli generate \
  -i openapi.json \
  -g java \
  -o ./client-java \
  --library=okhttp-gson
```

### C# (.NET)

```bash
# Cliente C#
openapi-generator-cli generate \
  -i openapi.json \
  -g csharp-netcore \
  -o ./client-csharp
```

---

## 🧪 Testing Automatizado

### Con Dredd

```bash
# Instalar
npm install -g dredd

# Ejecutar tests
dredd openapi.yaml http://localhost:8080

# Con archivo de configuración
dredd init
dredd
```

### Con Postman/Newman

```bash
# Importar en Postman
# File → Import → Pega la URL: http://localhost:8080/api/api-docs

# Exportar colección de Postman
# File → Export → Guarda como collection.json

# Ejecutar con Newman
npm install -g newman
newman run collection.json
```

### Con REST Assured (Java)

```xml
<!-- En pom.xml -->
<dependency>
    <groupId>io.rest-assured</groupId>
    <artifactId>rest-assured</artifactId>
    <scope>test</scope>
</dependency>
```

```java
// Test con REST Assured
@Test
public void testGetCursos() {
    given()
        .when()
        .get("http://localhost:8080/api/cursos")
        .then()
        .statusCode(200)
        .body("size()", greaterThan(0));
}
```

---

## 🎨 Generar Documentación HTML

### Con Redoc

```bash
# Instalar
npm install -g redoc-cli

# Generar HTML
redoc-cli bundle openapi.yaml -o docs/index.html

# Servir en vivo
redoc-cli serve openapi.yaml --watch
```

### Con Swagger UI Standalone

```bash
# Descargar Swagger UI
git clone https://github.com/swagger-api/swagger-ui.git
cd swagger-ui/dist

# Copiar tu especificación
cp /path/to/openapi.json ./

# Editar index.html y cambiar la URL
# url: "openapi.json"

# Servir con servidor HTTP simple
python -m http.server 8000
# O con Node.js
npx http-server
```

---

## 🔄 Comparar Versiones

### Con oasdiff

```bash
# Instalar
npm install -g oasdiff

# Comparar dos versiones
oasdiff diff openapi-v1.json openapi-v2.json

# Solo breaking changes
oasdiff breaking openapi-v1.json openapi-v2.json

# Formato markdown
oasdiff diff openapi-v1.json openapi-v2.json --format markdown > CHANGELOG.md
```

---

## 🎭 Mock Server

### Con Prism (Stoplight)

```bash
# Instalar
npm install -g @stoplight/prism-cli

# Iniciar mock server
prism mock openapi.yaml

# En puerto específico
prism mock openapi.yaml -p 4010

# Con ejemplos dinámicos
prism mock openapi.yaml --dynamic
```

### Con Mockoon

```bash
# Instalar Mockoon CLI
npm install -g @mockoon/cli

# Importar OpenAPI y crear mock
mockoon-cli start --data openapi.json --port 3001
```

---

## 📊 Análisis de Seguridad

### Con 42Crunch Platform

```bash
# Registrarse en 42crunch.com y obtener API key

# Instalar CLI
npm install -g @42crunch/api-security-audit

# Auditar
api-security-audit openapi.json --token YOUR_TOKEN
```

### Con OWASP ZAP

```bash
# Descargar OWASP ZAP
# https://www.zaproxy.org/download/

# Importar OpenAPI
zap-cli open-url http://localhost:8080/api/api-docs

# Escanear
zap-cli active-scan http://localhost:8080/api/
```

---

## 📦 Publicar Documentación

### GitHub Pages

```bash
# Generar HTML con Redoc
redoc-cli bundle openapi.yaml -o index.html

# Crear rama gh-pages
git checkout -b gh-pages
git add index.html
git commit -m "Add API documentation"
git push origin gh-pages

# Accesible en: https://username.github.io/repo-name/
```

### SwaggerHub

```bash
# Instalar SwaggerHub CLI
npm install -g swaggerhub-cli

# Login
swaggerhub login

# Publicar
swaggerhub api:create my-org/gestion-notas/1.0.0 -f openapi.yaml --published=publish
```

---

## 🔧 Scripts Útiles

### Script de actualización automática (Bash)

```bash
#!/bin/bash
# update-openapi.sh

echo "🔄 Actualizando especificación OpenAPI..."

# Esperar que el servidor esté disponible
while ! curl -s http://localhost:8080/api/api-docs > /dev/null; do
    echo "⏳ Esperando servidor..."
    sleep 2
done

# Descargar especificaciones
mkdir -p docs/api
curl -s http://localhost:8080/api/api-docs > docs/api/openapi.json
curl -s http://localhost:8080/api/v3/api-docs.yaml > docs/api/openapi.yaml

# Validar
swagger-cli validate docs/api/openapi.json

# Generar HTML
redoc-cli bundle docs/api/openapi.yaml -o docs/api/index.html

echo "✅ Especificación actualizada correctamente"
```

### Script de actualización (PowerShell)

```powershell
# update-openapi.ps1

Write-Host "🔄 Actualizando especificación OpenAPI..." -ForegroundColor Cyan

# Esperar que el servidor esté disponible
$serverReady = $false
while (-not $serverReady) {
    try {
        $response = Invoke-WebRequest -Uri "http://localhost:8080/api/api-docs" -UseBasicParsing -TimeoutSec 2
        $serverReady = $true
    } catch {
        Write-Host "⏳ Esperando servidor..." -ForegroundColor Yellow
        Start-Sleep -Seconds 2
    }
}

# Crear directorio
New-Item -ItemType Directory -Force -Path "docs/api" | Out-Null

# Descargar especificaciones
Invoke-WebRequest -Uri "http://localhost:8080/api/api-docs" -OutFile "docs/api/openapi.json"
Invoke-WebRequest -Uri "http://localhost:8080/api/v3/api-docs.yaml" -OutFile "docs/api/openapi.yaml"

# Validar (si tienes swagger-cli instalado)
if (Get-Command swagger-cli -ErrorAction SilentlyContinue) {
    swagger-cli validate docs/api/openapi.json
}

# Generar HTML (si tienes redoc-cli instalado)
if (Get-Command redoc-cli -ErrorAction SilentlyContinue) {
    redoc-cli bundle docs/api/openapi.yaml -o docs/api/index.html
}

Write-Host "✅ Especificación actualizada correctamente" -ForegroundColor Green
```

---

## 🎯 Pre-commit Hook

### Git Hook para validar OpenAPI

```bash
#!/bin/bash
# .git/hooks/pre-commit

# Verificar si existe el archivo OpenAPI
if [ -f "docs/api/openapi.json" ]; then
    echo "🔍 Validando especificación OpenAPI..."
    
    # Validar con swagger-cli
    if command -v swagger-cli &> /dev/null; then
        swagger-cli validate docs/api/openapi.json
        
        if [ $? -ne 0 ]; then
            echo "❌ Especificación OpenAPI inválida"
            exit 1
        fi
        
        echo "✅ Especificación OpenAPI válida"
    else
        echo "⚠️  swagger-cli no instalado, omitiendo validación"
    fi
fi

exit 0
```

---

## 📝 GitHub Actions

### Workflow para validar OpenAPI

```yaml
# .github/workflows/validate-openapi.yml

name: Validate OpenAPI

on:
  push:
    paths:
      - 'src/**'
      - 'docs/api/**'
  pull_request:

jobs:
  validate:
    runs-on: ubuntu-latest
    
    steps:
      - uses: actions/checkout@v3
      
      - name: Set up Java
        uses: actions/setup-java@v3
        with:
          java-version: '21'
          distribution: 'temurin'
      
      - name: Start Application
        run: |
          ./mvnw spring-boot:start &
          sleep 30
      
      - name: Download OpenAPI Spec
        run: |
          mkdir -p docs/api
          curl http://localhost:8080/api/api-docs > docs/api/openapi.json
      
      - name: Validate OpenAPI
        uses: char0n/swagger-editor-validate@v1
        with:
          definition-file: docs/api/openapi.json
      
      - name: Stop Application
        run: ./mvnw spring-boot:stop
```

---

## 🎉 Todo Listo

Estos comandos te ayudarán a:
- ✅ Exportar y validar tu especificación
- ✅ Generar clientes en múltiples lenguajes
- ✅ Crear servidores mock para desarrollo
- ✅ Automatizar testing
- ✅ Publicar documentación
- ✅ Mantener calidad del código

¡Guarda este archivo para referencia rápida!
