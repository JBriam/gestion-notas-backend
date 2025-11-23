# Datos de Prueba - Sistema de Gestión de Notas

Los siguientes datos se cargan automáticamente al iniciar la aplicación por primera vez.

## Usuario Administrador

- **Email:** `admin@colegio.edu.pe`
- **Password:** `123456`
- **Rol:** ADMIN

## Docentes

| Código | Nombre | Email | Especialidad |
|--------|--------|-------|--------------|
| DOC001 | Carlos Rodríguez Pérez | carlos.rodriguez@colegio.edu.pe | Matemáticas |
| DOC002 | María González Silva | maria.gonzalez@colegio.edu.pe | Ciencias |
| DOC003 | José Martínez López | jose.martinez@colegio.edu.pe | Historia |

**Password para todos:** `123456`

## Estudiantes

| Código | Nombre | Email | Teléfono |
|--------|--------|-------|----------|
| EST001 | Ana García Torres | ana.garcia@estudiante.edu.pe | 987654321 |
| EST002 | Luis Fernández Ruiz | luis.fernandez@estudiante.edu.pe | 987654322 |
| EST003 | Carmen López Mendoza | carmen.lopez@estudiante.edu.pe | 987654323 |
| EST004 | Pedro Sánchez Vega | pedro.sanchez@estudiante.edu.pe | 987654324 |

**Password para todos:** `123456`

## Cursos

| Código | Nombre | Créditos | Docente Asignado |
|--------|--------|----------|------------------|
| CUR001 | Matemática I | 4 | Carlos Rodríguez |
| CUR002 | Física I | 4 | María González |
| CUR003 | Historia del Perú | 3 | José Martínez |
| CUR004 | Química General | 4 | María González |

## Notas

Cada estudiante tiene notas en varios cursos con diferentes tipos de evaluación:
- **PARCIAL**: Examen parcial
- **TAREA**: Tareas asignadas
- **PRACTICA**: Prácticas calificadas
- **FINAL**: Examen final

Las notas se generan aleatoriamente entre 10 y 20 puntos.

## Recarga de Datos

Los datos de prueba **solo se cargan una vez** al iniciar la aplicación. Si quieres recargarlos:

1. **Elimina la base de datos:**
   ```powershell
   Remove-Item gestion_notas.db
   ```

2. **Reinicia la aplicación** y los datos se volverán a crear automáticamente.

## Seguridad

**IMPORTANTE:** Todos los usuarios tienen la password `123456` para propósitos de prueba. 

**Cambiar las contraseñas en producción.**
