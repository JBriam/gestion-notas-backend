package edu.college.gestion_notas_backend.config;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import edu.college.gestion_notas_backend.model.Curso;
import edu.college.gestion_notas_backend.model.Docente;
import edu.college.gestion_notas_backend.model.Estudiante;
import edu.college.gestion_notas_backend.model.Nota;
import edu.college.gestion_notas_backend.model.Usuario;
import edu.college.gestion_notas_backend.service.CursoService;
import edu.college.gestion_notas_backend.service.DocenteService;
import edu.college.gestion_notas_backend.service.EstudianteService;
import edu.college.gestion_notas_backend.service.NotaService;
import edu.college.gestion_notas_backend.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UsuarioService usuarioService;
    private final DocenteService docenteService;
    private final EstudianteService estudianteService;
    private final CursoService cursoService;
    private final NotaService notaService;

    @Override
    public void run(String... args) throws Exception {
        log.info("Iniciando carga de datos de prueba...");
        crearUsuarioAdminPorDefecto();
        crearDatosDePrueba();
        log.info("Datos de prueba cargados exitosamente");
    }

    private void crearUsuarioAdminPorDefecto() {
        String emailAdmin = "admin@colegio.edu.pe";
        
        try {
            // Verificar si ya existe un usuario admin
            if (usuarioService.existeUsuarioPorEmail(emailAdmin)) {
                log.info("Usuario admin ya existe en el sistema");
                return;
            }

            // Dejar que UsuarioService.crearUsuario() encripte automáticamente
            Usuario usuarioAdmin = Usuario.builder()
                    .email(emailAdmin)
                    .password("123456") // Texto plano - el servicio lo encriptará
                    .rol(Usuario.Rol.ADMIN)
                    .activo(true)
                    .build();

            Usuario adminCreado = usuarioService.crearUsuario(usuarioAdmin);
            
            log.info("Usuario admin creado exitosamente con ID: {}", adminCreado.getIdUsuario());
            log.info("Email: {}", adminCreado.getEmail());
            log.info("Password temporal: 123456 (¡Cambiar después del primer login!)");
            
        } catch (Exception e) {
            log.error("Error al crear usuario admin por defecto: {}", e.getMessage());
        }
    }

    private void crearDatosDePrueba() {
        try {
            // Verificar si ya existen datos
            if (docenteService.obtenerTodosLosDocentes().size() > 0) {
                log.info("Ya existen datos de prueba, omitiendo creación");
                return;
            }

            log.info("Creando datos de prueba...");
            // 1. Crear Docentes
            Docente docente1 = crearDocente(
                "DOC001",
                "Carlos",
                "Rodríguez Pérez",
                "carlos.rodriguez@colegio.edu.pe",
                "999888777",
                "Matemáticas"
            );

            Docente docente2 = crearDocente(
                "DOC002",
                "María",
                "González Silva",
                "maria.gonzalez@colegio.edu.pe",
                "999888666",
                "Ciencias"
            );

            Docente docente3 = crearDocente(
                "DOC003",
                "José",
                "Martínez López",
                "jose.martinez@colegio.edu.pe",
                "999888555",
                "Historia"
            );

            log.info("{} docentes creados", 3);

            // 2. Crear Estudiantes
            Estudiante estudiante1 = crearEstudiante(
                "EST001",
                "Ana",
                "García Torres",
                "ana.garcia@estudiante.edu.pe",
                "987654321",
                "Av. Principal 123",
                "12345678"
            );

            Estudiante estudiante2 = crearEstudiante(
                "EST002",
                "Luis",
                "Fernández Ruiz",
                "luis.fernandez@estudiante.edu.pe",
                "987654322",
                "Jr. Secundaria 456",
                "87654321"
            );

            Estudiante estudiante3 = crearEstudiante(
                "EST003",
                "Carmen",
                "López Mendoza",
                "carmen.lopez@estudiante.edu.pe",
                "987654323",
                "Calle Tercera 789",
                "11223344"
            );

            Estudiante estudiante4 = crearEstudiante(
                "EST004",
                "Pedro",
                "Sánchez Vega",
                "pedro.sanchez@estudiante.edu.pe",
                "987654324",
                "Av. Los Olivos 321",
                "44332211"
            );

            log.info("{} estudiantes creados", 4);

            // 3. Crear Cursos
            Curso curso1 = crearCurso("CUR001", "Matemática I", "Álgebra y Geometría", 4, docente1);
            Curso curso2 = crearCurso("CUR002", "Física I", "Mecánica Clásica", 4, docente2);
            Curso curso3 = crearCurso("CUR003", "Historia del Perú", "Historia y Civilización", 3, docente3);
            Curso curso4 = crearCurso("CUR004", "Química General", "Fundamentos de Química", 4, docente2);

            log.info("{} cursos creados", 4);

            // 4. Crear Notas
            crearNotas(estudiante1, curso1, curso2, curso3);
            crearNotas(estudiante2, curso1, curso2, curso4);
            crearNotas(estudiante3, curso2, curso3, curso4);
            crearNotas(estudiante4, curso1, curso3, curso4);

            log.info("Notas creadas para todos los estudiantes");

        } catch (Exception e) {
            log.error("Error al crear datos de prueba: {}", e.getMessage(), e);
        }
    }

    private Docente crearDocente(String codigo, String nombres, String apellidos, 
                                  String email, String telefono, String especialidad) {
        try {
            // Crear usuario
            Usuario usuario = Usuario.builder()
                .email(email)
                .password("123456")
                .rol(Usuario.Rol.DOCENTE)
                .activo(true)
                .build();
            Usuario usuarioCreado = usuarioService.crearUsuario(usuario);

            // Crear docente
            Docente docente = Docente.builder()
                .usuario(usuarioCreado)
                .codigoDocente(codigo)
                .nombres(nombres)
                .apellidos(apellidos)
                .telefono(telefono)
                .especialidad(especialidad)
                .fechaContratacion(LocalDate.now().minusYears(2))
                .build();

            return docenteService.crearDocente(docente);
        } catch (Exception e) {
            log.error("Error al crear docente {}: {}", codigo, e.getMessage());
            return null;
        }
    }

    private Estudiante crearEstudiante(String codigo, String nombres, String apellidos,
                                       String email, String telefono, String direccion, String dniDocumento) {
        try {
            // Crear usuario
            Usuario usuario = Usuario.builder()
                .email(email)
                .password("123456")
                .rol(Usuario.Rol.ESTUDIANTE)
                .activo(true)
                .build();
            Usuario usuarioCreado = usuarioService.crearUsuario(usuario);

            // Crear estudiante
            Estudiante estudiante = Estudiante.builder()
                .usuario(usuarioCreado)
                .codigoEstudiante(codigo)
                .nombres(nombres)
                .apellidos(apellidos)
                .telefono(telefono)
                .direccion(direccion)
                .fechaNacimiento(LocalDate.of(2005, 3, 15))
                .build();

            return estudianteService.crearEstudiante(estudiante);
        } catch (Exception e) {
            log.error("Error al crear estudiante {}: {}", codigo, e.getMessage());
            return null;
        }
    }

    private Curso crearCurso(String codigo, String nombre, String descripcion, 
                            int creditos, Docente docente) {
        try {
            Curso curso = Curso.builder()
                .codigoCurso(codigo)
                .nombre(nombre)
                .descripcion(descripcion)
                .creditos(creditos)
                .docente(docente)
                .activo(true)
                .build();

            return cursoService.crearCurso(curso);
        } catch (Exception e) {
            log.error("Error al crear curso {}: {}", codigo, e.getMessage());
            return null;
        }
    }

    private void crearNotas(Estudiante estudiante, Curso... cursos) {
        try {
            for (Curso curso : cursos) {
                if (curso != null) {
                    // Crear varias notas por curso
                    crearNota(estudiante, curso, Nota.TipoEvaluacion.PARCIAL, generarNotaAleatoria());
                    crearNota(estudiante, curso, Nota.TipoEvaluacion.TAREA, generarNotaAleatoria());
                    crearNota(estudiante, curso, Nota.TipoEvaluacion.PRACTICA, generarNotaAleatoria());
                    crearNota(estudiante, curso, Nota.TipoEvaluacion.FINAL, generarNotaAleatoria());
                }
            }
        } catch (Exception e) {
            log.error("Error al crear notas: {}", e.getMessage());
        }
    }

    private void crearNota(Estudiante estudiante, Curso curso, 
                          Nota.TipoEvaluacion tipo, BigDecimal valor) {
        try {
            Nota nota = Nota.builder()
                .estudiante(estudiante)
                .curso(curso)
                .tipoEvaluacion(tipo)
                .nota(valor)
                .fechaRegistro(LocalDateTime.now())
                .observaciones("Nota de prueba")
                .build();

            notaService.crearNota(nota);
        } catch (Exception e) {
            log.error("Error al crear nota individual: {}", e.getMessage());
        }
    }

    private BigDecimal generarNotaAleatoria() {
        // Generar notas entre 10 y 20
        int nota = 10 + (int) (Math.random() * 11);
        return BigDecimal.valueOf(nota);
    }
}
