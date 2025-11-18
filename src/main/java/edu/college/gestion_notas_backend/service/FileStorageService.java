package edu.college.gestion_notas_backend.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import edu.college.gestion_notas_backend.config.FileStorageConfig;

@Service
public class FileStorageService {

    private final Path fileStorageLocation;

    @Autowired
    public FileStorageService(FileStorageConfig fileStorageConfig) {
        this.fileStorageLocation = Paths.get(fileStorageConfig.getUploadDir())
                .toAbsolutePath().normalize();

        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new RuntimeException("No se pudo crear el directorio para almacenar los archivos.", ex);
        }
    }

    /**
     * Almacena un archivo en el directorio base
     */
    public String storeFile(MultipartFile file) {
        return storeFile(file, null);
    }

    /**
     * Almacena un archivo en un subdirectorio específico
     * @param file Archivo a almacenar
     * @param subDirectory Subdirectorio donde guardar (ej: "estudiantes", "docentes")
     * @return Solo el nombre del archivo (sin subdirectorio)
     */
    public String storeFile(MultipartFile file, String subDirectory) {
        try {
            String fileName = StringUtils.cleanPath(file.getOriginalFilename());
            String fileExtension = "";
            
            int dotIndex = fileName.lastIndexOf('.');
            if (dotIndex > 0) {
                fileExtension = fileName.substring(dotIndex);
            }
            
            // Generar nombre único para el archivo
            String uniqueFileName = UUID.randomUUID().toString() + fileExtension;
            
            // Determinar la ubicación de destino
            Path targetLocation;
            
            if (subDirectory != null && !subDirectory.trim().isEmpty()) {
                // Crear subdirectorio si no existe
                Path subDirPath = this.fileStorageLocation.resolve(subDirectory);
                Files.createDirectories(subDirPath);
                
                targetLocation = subDirPath.resolve(uniqueFileName);
            } else {
                targetLocation = this.fileStorageLocation.resolve(uniqueFileName);
            }
            
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            // Retornar solo el nombre del archivo
            return uniqueFileName;
        } catch (IOException ex) {
            throw new RuntimeException("No se pudo almacenar el archivo.", ex);
        }
    }

    /**
     * Elimina un archivo del sistema
     * @param fileName Nombre del archivo
     * @param subDirectory Subdirectorio donde se encuentra (ej: "estudiantes", "docentes")
     */
    public void deleteFile(String fileName, String subDirectory) {
        try {
            if (fileName == null || fileName.trim().isEmpty()) {
                return;
            }
            
            Path targetLocation;
            if (subDirectory != null && !subDirectory.trim().isEmpty()) {
                targetLocation = this.fileStorageLocation.resolve(subDirectory).resolve(fileName);
            } else {
                targetLocation = this.fileStorageLocation.resolve(fileName);
            }
            
            Files.deleteIfExists(targetLocation);
        } catch (IOException ex) {
            throw new RuntimeException("No se pudo eliminar el archivo.", ex);
        }
    }
}