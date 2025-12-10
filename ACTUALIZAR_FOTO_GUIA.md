# Actualizar Foto de Perfil - Guía de Implementación

## Descripción
Este documento explica cómo usar la funcionalidad de actualizar foto de perfil implementada en las ramas:
- Backend: `feature/actualizar-foto-perfil-backend`
- Frontend: `feature/actualizar-foto-perfil-frontend`

## Backend - Implementación

### 1. Endpoint Creado
**URL:** `PUT /estudiantes/{id}/foto`

**Descripción:** Actualiza la foto de perfil de un estudiante.

**Parámetros:**
- `id` (PathVariable): ID del estudiante
- `foto` (RequestParam, MultipartFile): Archivo de imagen

**Validaciones:**
- El archivo no debe estar vacío
- El archivo debe ser una imagen (JPEG, PNG, GIF, WebP)
- Tamaño máximo: 5MB
- El estudiante debe existir

**Respuesta (200 OK):**
```json
{
  "idEstudiante": 1,
  "nombres": "Juan",
  "apellidos": "Pérez",
  "email": "juan@example.com",
  "foto": "/uploads/estudiantes/uuid-nombre.jpg",
  ...
}
```

**Errores:**
- 400: Archivo inválido o estudiante no encontrado
- 500: Error al guardar la foto

### 2. Servicios Implementados

**EstudianteService.java**
```java
public Estudiante actualizarFoto(Integer id, org.springframework.web.multipart.MultipartFile foto)
```
- Busca el estudiante por ID
- Elimina la foto anterior si existe
- Guarda la nueva foto
- Retorna el estudiante actualizado

**FileStorageService.java**
```java
public String storeFile(MultipartFile file, String subDirectory)
```
- Genera un nombre único para el archivo
- Almacena en la carpeta `uploads/estudiantes/`
- Retorna solo el nombre del archivo

### 3. Configuración necesaria

Asegúrate de que el archivo `application-dev.properties` tenga configurada la ruta de subida:
```properties
file.upload-dir=./uploads
```

## Frontend - Implementación

### 1. Componentes Creados

**ActualizarFotoModal.tsx**
- Modal para seleccionar y actualizar foto
- Validación de archivo en el cliente
- Preview de imagen antes de enviar
- Soporte para drag & drop

**FotoService.ts**
- Servicio para llamar al API de actualización
- Validación de imagen (tipo y tamaño)
- Manejo de errores

### 2. Cómo Usar

#### Importar el componente:
```tsx
import { ActualizarFotoModal } from '../Modals/ActualizarFotoModal';
```

#### Usar en un componente:
```tsx
const [modalAbierto, setModalAbierto] = useState(false);
const [fotoActual, setFotoActual] = useState<string | null>(null);

return (
  <>
    {/* Botón para abrir modal */}
    <button onClick={() => setModalAbierto(true)}>
      Cambiar foto
    </button>

    {/* Modal */}
    <ActualizarFotoModal
      isOpen={modalAbierto}
      usuarioId={usuarioId}
      usuarioNombre={usuarioNombre}
      tipoUsuario="estudiante"
      fotoActual={fotoActual}
      onClose={() => setModalAbierto(false)}
      onFotoActualizada={(nuevaFoto) => {
        setFotoActual(nuevaFoto);
      }}
    />
  </>
);
```

### 3. Props del Modal

```typescript
interface ActualizarFotoModalProps {
  isOpen: boolean;                              // Controla si el modal está visible
  usuarioId: number;                            // ID del estudiante o docente
  usuarioNombre: string;                        // Nombre del usuario
  tipoUsuario: 'estudiante' | 'docente';       // Tipo de usuario
  fotoActual?: string;                          // URL de la foto actual (opcional)
  onClose: () => void;                          // Callback cuando se cierra el modal
  onFotoActualizada: (nuevaFoto: string) => void; // Callback cuando se actualiza exitosamente
}
```

### 4. Validaciones en el Cliente

El componente valida automáticamente:
- ✅ Tipo de archivo (solo imágenes)
- ✅ Tamaño máximo (5MB)
- ✅ Archivo no vacío

## Pruebas

### Con Postman (Backend)
1. Crear una solicitud PUT a: `http://localhost:8080/api/estudiantes/1/foto`
2. En Body → form-data:
   - Key: `foto`
   - Value: Seleccionar archivo de imagen
3. Enviar solicitud

### En el Frontend
1. Abrir el modal haciendo clic en "Cambiar foto"
2. Seleccionar una imagen (o arrastrarla)
3. Ver la vista previa
4. Hacer clic en "Actualizar foto"

## Integración en Perfiles

Para integrar la funcionalidad en la página de perfil:

```tsx
// En el componente de perfil (Profile.tsx, etc.)
import { ActualizarFotoModal } from '../Modals/ActualizarFotoModal';

export const Perfil: React.FC = () => {
  const [modalFotoAbierto, setModalFotoAbierto] = useState(false);
  const [fotoUsuario, setFotoUsuario] = useState(user?.foto || '');

  return (
    <div>
      {/* Foto actual */}
      <img src={fotoUsuario} alt="Foto de perfil" />
      
      {/* Botón para cambiar foto */}
      <button onClick={() => setModalFotoAbierto(true)}>
        Cambiar foto
      </button>

      {/* Modal */}
      <ActualizarFotoModal
        isOpen={modalFotoAbierto}
        usuarioId={user?.id || 0}
        usuarioNombre={user?.nombre || ''}
        tipoUsuario="estudiante"
        onClose={() => setModalFotoAbierto(false)}
        onFotoActualizada={(nuevaFoto) => {
          setFotoUsuario(nuevaFoto);
        }}
      />
    </div>
  );
};
```

## Archivos Modificados

### Backend
- `EstudianteController.java` - Agregado endpoint PUT
- `EstudianteService.java` - Agregado método actualizarFoto()

### Frontend
- `FotoService.ts` - Nuevo servicio de API
- `ActualizarFotoModal.tsx` - Nuevo componente modal
- `ActualizarFotoModal.css` - Estilos del modal

## Notas Importantes

1. La foto se guarda en el servidor en la carpeta `uploads/estudiantes/`
2. El nombre del archivo se genera automáticamente (UUID)
3. Se elimina la foto anterior cuando se sube una nueva
4. El endpoint está protegido por autenticación (si aplica)
5. Se valida tanto en cliente como en servidor

## Próximos pasos (Opcional)

1. Implementar la misma funcionalidad para docentes
2. Agregar opción de recortar/editar imagen antes de subir
3. Implementar compresión de imagen en el cliente
4. Agregar caché-busting para mostrar foto actualizada inmediatamente
5. Historial de cambios de foto
