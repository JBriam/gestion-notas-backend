# Configuración CORS - Sistema de Gestión de Notas

## ✅ ¿Qué se ha configurado?

### 1. **CorsConfig.java** - Configuración principal
- Permite comunicación con cualquier origen (desarrollo)
- Métodos HTTP: GET, POST, PUT, DELETE, PATCH, OPTIONS
- Headers: Todos permitidos (`*`)
- Credenciales: Habilitadas
- Cache de preflight: 1 hora

### 2. **CorsFilterConfig.java** - Configuración alternativa
- Filtro CORS adicional para mayor control
- Headers expuestos para el frontend
- Configuración más granular

## 🚀 Configuraciones por tipo de Frontend

### **React (puerto 3000)**
```java
// En CorsConfig.java, reemplaza:
configuration.setAllowedOriginPatterns(Arrays.asList("*"));

// Por:
configuration.setAllowedOrigins(Arrays.asList(
    "http://localhost:3000",
    "https://tu-app-react.com"
));
```

### **Angular (puerto 4200)**
```java
configuration.setAllowedOrigins(Arrays.asList(
    "http://localhost:4200",
    "https://tu-app-angular.com"
));
```

### **Vue.js (puerto 8080)**
```java
configuration.setAllowedOrigins(Arrays.asList(
    "http://localhost:8080", // Vue dev server
    "https://tu-app-vue.com"
));
```

### **Next.js (puerto 3000)**
```java
configuration.setAllowedOrigins(Arrays.asList(
    "http://localhost:3000",
    "https://tu-app-nextjs.vercel.app"
));
```

## 🔧 Configuración actual (Desarrollo)

```java
// Permite TODOS los orígenes - Solo para desarrollo
configuration.setAllowedOriginPatterns(Arrays.asList("*"));
```

## 🛡️ Configuración para Producción

### **Ejemplo con múltiples dominios:**
```java
configuration.setAllowedOrigins(Arrays.asList(
    "http://localhost:3000",           // Desarrollo local
    "https://app.tudominio.com",       // Aplicación principal
    "https://admin.tudominio.com",     // Panel de administración
    "https://tudominio.github.io"      // GitHub Pages
));
```

### **Headers de seguridad adicionales:**
```java
configuration.setExposedHeaders(Arrays.asList(
    "Authorization",        // Para tokens JWT
    "Content-Type",        // Tipo de contenido
    "X-Total-Count",       // Para paginación
    "X-Total-Pages",       // Para paginación
    "Cache-Control",       // Control de cache
    "X-Requested-With"     // Headers AJAX
));
```

## 📋 URLs de tu API

Con `server.servlet.context-path=/api` en application.properties:

### **Base URL:** `http://localhost:8080/api`

### **Endpoints principales:**
- **Login:** `POST /api/usuarios/login`
- **Estudiantes:** `GET /api/estudiantes`
- **Docentes:** `GET /api/docentes`
- **Cursos:** `GET /api/cursos`
- **Notas:** `GET /api/notas`

## 🧪 Pruebas desde Frontend

### **JavaScript/Fetch:**
```javascript
// Login
fetch('http://localhost:8080/api/usuarios/login', {
    method: 'POST',
    headers: {
        'Content-Type': 'application/json',
    },
    credentials: 'include', // Para cookies
    body: JSON.stringify({
        username: 'admin',
        password: 'admin123'
    })
})
.then(response => response.json())
.then(data => console.log(data));
```

### **Axios (React/Vue/Angular):**
```javascript
import axios from 'axios';

const api = axios.create({
    baseURL: 'http://localhost:8080/api',
    withCredentials: true,
    headers: {
        'Content-Type': 'application/json'
    }
});

// Login
const login = async (credentials) => {
    const response = await api.post('/usuarios/login', credentials);
    return response.data;
};

// Obtener estudiantes
const getEstudiantes = async () => {
    const response = await api.get('/estudiantes');
    return response.data;
};
```

## 🔍 Verificar CORS

### **Herramientas de prueba:**
1. **Postman** - Simula requests desde navegador
2. **Insomnia** - Cliente REST
3. **Browser DevTools** - Revisa errores CORS en consola
4. **CORS Tester** - Extensiones de navegador

### **Comandos curl para pruebas:**
```bash
# Preflight request (OPTIONS)
curl -X OPTIONS \
  -H "Origin: http://localhost:3000" \
  -H "Access-Control-Request-Method: POST" \
  -H "Access-Control-Request-Headers: Content-Type" \
  http://localhost:8080/api/usuarios/login -v

# Request real
curl -X POST \
  -H "Content-Type: application/json" \
  -H "Origin: http://localhost:3000" \
  -d '{"username":"admin","password":"admin123"}' \
  http://localhost:8080/api/usuarios/login -v
```

## ⚠️ Errores comunes y soluciones

### **Error: "CORS policy: No 'Access-Control-Allow-Origin'"**
- ✅ Verifica que el origen está en `allowedOrigins`
- ✅ Asegúrate que el servidor esté corriendo
- ✅ Revisa que no haya múltiples configuraciones CORS

### **Error: "CORS policy: The request client is not a secure context"**
- ✅ Usa HTTPS en producción
- ✅ Para desarrollo, usa `localhost` en lugar de IP

### **Error: Preflight request failed**
- ✅ Asegúrate que OPTIONS está en `allowedMethods`
- ✅ Verifica que los headers están permitidos

## 🚀 ¡Tu configuración está lista!

Con la configuración actual puedes conectar tu frontend desde cualquier origen. 
Recuerda cambiar a dominios específicos cuando vayas a producción por seguridad.

### **Próximos pasos:**
1. ✅ Remover `@CrossOrigin` de los demás controladores
2. ✅ Probar conexión desde tu frontend
3. ✅ Ajustar orígenes específicos para producción
4. ✅ Implementar autenticación JWT si es necesario