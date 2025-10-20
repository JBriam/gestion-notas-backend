package edu.college.gestion_notas_backend.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import edu.college.gestion_notas_backend.model.Usuario;
import edu.college.gestion_notas_backend.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UsuarioService usuarioService;

    @Override
    public void run(String... args) throws Exception {
        crearUsuarioAdminPorDefecto();
    }

    private void crearUsuarioAdminPorDefecto() {
        String emailAdmin = "admin@colegio.edu.pe";
        
        try {
            // Verificar si ya existe un usuario admin
            if (usuarioService.existeUsuarioPorEmail(emailAdmin)) {
                log.info("Usuario admin ya existe en el sistema");
                return;
            }

            // ✅ Dejar que UsuarioService.crearUsuario() encripte automáticamente
            Usuario usuarioAdmin = Usuario.builder()
                    .email(emailAdmin)
                    .password("123456") // ✅ Texto plano - el servicio lo encriptará
                    .rol(Usuario.Rol.ADMIN)
                    .activo(true)
                    .build();

            Usuario adminCreado = usuarioService.crearUsuario(usuarioAdmin);
            
            log.info("✅ Usuario admin creado exitosamente con ID: {}", adminCreado.getIdUsuario());
            log.info("📧 Email: {}", adminCreado.getEmail());
            log.info("🔑 Password temporal: 123456 (¡Cambiar después del primer login!)");
            
        } catch (Exception e) {
            log.error("❌ Error al crear usuario admin por defecto: {}", e.getMessage());
        }
    }
}
