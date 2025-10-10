# Configuración React + Vite con Backend Spring Boot

## ✅ CORS configurado para React + Vite (puerto 5173)

### 📍 **Configuración actualizada:**
- **Frontend React**: `http://localhost:5173` (Vite)
- **Backend Spring Boot**: `http://localhost:8080/api`

## 🚀 Código para tu Frontend React

### **1. Configuración de Axios (recomendado)**

Instala Axios en tu proyecto React:
```bash
npm install axios
```

Crea un archivo `src/services/api.js`:
```javascript
import axios from 'axios';

// Configuración base de la API
const api = axios.create({
  baseURL: 'http://localhost:8080/api',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  },
  withCredentials: true, // Para cookies/sesiones
});

// Interceptor para manejar errores globalmente
api.interceptors.response.use(
  (response) => response,
  (error) => {
    console.error('Error en API:', error.response?.data || error.message);
    return Promise.reject(error);
  }
);

export default api;
```

### **2. Servicios para cada entidad**

Crea `src/services/authService.js`:
```javascript
import api from './api';

export const authService = {
  // Login
  login: async (credentials) => {
    const response = await api.post('/usuarios/login', credentials);
    return response.data;
  },

  // Crear usuario
  crearUsuario: async (usuario) => {
    const response = await api.post('/usuarios', usuario);
    return response.data;
  },

  // Obtener usuarios por rol
  obtenerUsuariosPorRol: async (rol) => {
    const response = await api.get(`/usuarios/rol/${rol}`);
    return response.data;
  }
};
```

Crea `src/services/estudianteService.js`:
```javascript
import api from './api';

export const estudianteService = {
  // Obtener todos los estudiantes
  obtenerTodos: async () => {
    const response = await api.get('/estudiantes');
    return response.data;
  },

  // Obtener estudiante por ID
  obtenerPorId: async (id) => {
    const response = await api.get(`/estudiantes/${id}`);
    return response.data;
  },

  // Crear estudiante
  crear: async (estudiante) => {
    const response = await api.post('/estudiantes', estudiante);
    return response.data;
  },

  // Actualizar estudiante
  actualizar: async (id, estudiante) => {
    const response = await api.put(`/estudiantes/${id}`, estudiante);
    return response.data;
  },

  // Actualizar perfil
  actualizarPerfil: async (id, perfil) => {
    const response = await api.put(`/estudiantes/${id}/perfil`, perfil);
    return response.data;
  },

  // Eliminar estudiante
  eliminar: async (id) => {
    await api.delete(`/estudiantes/${id}`);
  },

  // Buscar por nombre
  buscarPorNombre: async (nombre) => {
    const response = await api.get(`/estudiantes/buscar?nombre=${nombre}`);
    return response.data;
  }
};
```

Crea `src/services/notaService.js`:
```javascript
import api from './api';

export const notaService = {
  // Obtener notas por estudiante
  obtenerPorEstudiante: async (idEstudiante) => {
    const response = await api.get(`/notas/estudiante/${idEstudiante}`);
    return response.data;
  },

  // Crear nota
  crear: async (nota) => {
    const response = await api.post('/notas', nota);
    return response.data;
  },

  // Calcular promedio por estudiante
  calcularPromedio: async (idEstudiante) => {
    const response = await api.get(`/notas/promedio/estudiante/${idEstudiante}`);
    return response.data;
  },

  // Obtener estado académico
  obtenerEstado: async (idEstudiante, idCurso) => {
    const response = await api.get(`/notas/estado/estudiante/${idEstudiante}/curso/${idCurso}`);
    return response.data;
  }
};
```

### **3. Componente de Login**

```jsx
// src/components/Login.jsx
import React, { useState } from 'react';
import { authService } from '../services/authService';

const Login = ({ onLogin }) => {
  const [credentials, setCredentials] = useState({
    username: '',
    password: ''
  });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError('');

    try {
      const response = await authService.login(credentials);
      
      if (response.success) {
        onLogin(response.usuario);
        console.log('Login exitoso:', response);
      } else {
        setError(response.message || 'Error en login');
      }
    } catch (error) {
      setError('Error de conexión con el servidor');
      console.error('Error:', error);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="login-container">
      <form onSubmit={handleSubmit}>
        <h2>Iniciar Sesión</h2>
        
        {error && <div className="error">{error}</div>}
        
        <div className="form-group">
          <label>Usuario:</label>
          <input
            type="text"
            value={credentials.username}
            onChange={(e) => setCredentials({
              ...credentials,
              username: e.target.value
            })}
            placeholder="Ingresa tu usuario"
            required
          />
        </div>

        <div className="form-group">
          <label>Contraseña:</label>
          <input
            type="password"
            value={credentials.password}
            onChange={(e) => setCredentials({
              ...credentials,
              password: e.target.value
            })}
            placeholder="Ingresa tu contraseña"
            required
          />
        </div>

        <button type="submit" disabled={loading}>
          {loading ? 'Iniciando sesión...' : 'Iniciar Sesión'}
        </button>
      </form>
    </div>
  );
};

export default Login;
```

### **4. Componente de Lista de Estudiantes**

```jsx
// src/components/EstudiantesList.jsx
import React, { useState, useEffect } from 'react';
import { estudianteService } from '../services/estudianteService';

const EstudiantesList = () => {
  const [estudiantes, setEstudiantes] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    cargarEstudiantes();
  }, []);

  const cargarEstudiantes = async () => {
    try {
      const data = await estudianteService.obtenerTodos();
      setEstudiantes(data);
    } catch (error) {
      setError('Error al cargar estudiantes');
      console.error('Error:', error);
    } finally {
      setLoading(false);
    }
  };

  if (loading) return <div>Cargando estudiantes...</div>;
  if (error) return <div className="error">{error}</div>;

  return (
    <div className="estudiantes-list">
      <h2>Lista de Estudiantes</h2>
      
      <table>
        <thead>
          <tr>
            <th>Código</th>
            <th>Nombres</th>
            <th>Apellidos</th>
            <th>Email</th>
            <th>Distrito</th>
            <th>Acciones</th>
          </tr>
        </thead>
        <tbody>
          {estudiantes.map((estudiante) => (
            <tr key={estudiante.idEstudiante}>
              <td>{estudiante.codigoEstudiante}</td>
              <td>{estudiante.nombres}</td>
              <td>{estudiante.apellidos}</td>
              <td>{estudiante.email}</td>
              <td>{estudiante.distrito}</td>
              <td>
                <button onClick={() => console.log('Ver detalles', estudiante)}>
                  Ver
                </button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
};

export default EstudiantesList;
```

### **5. Hook personalizado para manejo de estado**

```jsx
// src/hooks/useApi.js
import { useState, useEffect } from 'react';

export const useApi = (apiFunction, dependencies = []) => {
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchData = async () => {
      try {
        setLoading(true);
        setError(null);
        const result = await apiFunction();
        setData(result);
      } catch (err) {
        setError(err.message || 'Error desconocido');
      } finally {
        setLoading(false);
      }
    };

    fetchData();
  }, dependencies);

  return { data, loading, error, refetch: () => fetchData() };
};
```

## 🧪 **Credenciales de Prueba**

```javascript
// Administrador
{
  username: "admin",
  password: "admin123"
}

// Docente
{
  username: "prof.garcia",
  password: "prof123"
}

// Estudiante
{
  username: "est.juan",
  password: "est123"
}
```

## 🔧 **URLs de prueba en tu React**

```javascript
// Ejemplos de peticiones
const testAPI = async () => {
  try {
    // Login
    const loginResponse = await authService.login({
      username: 'admin',
      password: 'admin123'
    });
    console.log('Login:', loginResponse);

    // Obtener estudiantes
    const estudiantes = await estudianteService.obtenerTodos();
    console.log('Estudiantes:', estudiantes);

    // Obtener cursos
    const cursos = await api.get('/cursos');
    console.log('Cursos:', cursos.data);
  } catch (error) {
    console.error('Error:', error);
  }
};
```

## ✅ **Tu configuración está lista:**

- ✅ **CORS configurado** para `http://localhost:5173`
- ✅ **Backend corriendo** en `http://localhost:8080/api`
- ✅ **Servicios React** listos para usar
- ✅ **Datos de prueba** disponibles

¡Ahora puedes conectar tu React con el backend sin problemas de CORS! 🎉