-- ========================================
-- SCRIPT DE DATOS DE PRUEBA
-- Sistema de Gestión de Notas
-- ========================================

-- Usar la base de datos
USE gestion_notas;

-- Limpiar datos existentes (opcional)
-- DELETE FROM nota;
-- DELETE FROM curso;
-- DELETE FROM estudiante;
-- DELETE FROM docente;
-- DELETE FROM usuario;

-- ========================================
-- 1. INSERTAR USUARIOS
-- ========================================

INSERT INTO usuario (username, email, password, rol, activo, fecha_creacion) VALUES
-- Administrador
('admin', 'admin@colegio.edu.pe', 'admin123', 'ADMIN', true, NOW()),

-- Docentes
('prof.garcia', 'garcia@colegio.edu.pe', 'prof123', 'DOCENTE', true, NOW()),
('prof.martinez', 'martinez@colegio.edu.pe', 'prof123', 'DOCENTE', true, NOW()),
('prof.rodriguez', 'rodriguez@colegio.edu.pe', 'prof123', 'DOCENTE', true, NOW()),

-- Estudiantes
('est.juan', 'juan.perez@estudiante.edu.pe', 'est123', 'ESTUDIANTE', true, NOW()),
('est.ana', 'ana.garcia@estudiante.edu.pe', 'est123', 'ESTUDIANTE', true, NOW()),
('est.pedro', 'pedro.gomez@estudiante.edu.pe', 'est123', 'ESTUDIANTE', true, NOW()),
('est.maria', 'maria.rodriguez@estudiante.edu.pe', 'est123', 'ESTUDIANTE', true, NOW()),
('est.carlos', 'carlos.sanchez@estudiante.edu.pe', 'est123', 'ESTUDIANTE', true, NOW());

-- ========================================
-- 2. INSERTAR DOCENTES
-- ========================================

INSERT INTO docente (id_usuario, nombres, apellidos, telefono, distrito, especialidad, fecha_contratacion) VALUES
-- Admin también puede ser docente
(1, 'Carlos', 'Administrador', '999123456', 'Miraflores', 'Administración Educativa', '2020-01-15'),

-- Docentes
(2, 'María Elena', 'García López', '999234567', 'San Isidro', 'Matemáticas', '2019-03-10'),
(3, 'José Luis', 'Martínez Vega', '999345678', 'Surco', 'Historia y Geografía', '2020-08-22'),
(4, 'Ana Sofía', 'Rodríguez Silva', '999456789', 'La Molina', 'Ciencias Naturales', '2018-02-05');

-- ========================================
-- 3. INSERTAR ESTUDIANTES
-- ========================================

INSERT INTO estudiante (id_usuario, nombres, apellidos, email, telefono, distrito, fecha_nacimiento, codigo_estudiante) VALUES
(5, 'Juan Carlos', 'Pérez Mendoza', 'juan.perez@estudiante.edu.pe', '987123456', 'Pueblo Libre', '2006-05-15', 'EST000001'),
(6, 'Ana Lucía', 'García Torres', 'ana.garcia@estudiante.edu.pe', '987234567', 'Jesús María', '2005-11-20', 'EST000002'),
(7, 'Pedro Miguel', 'Gómez Vargas', 'pedro.gomez@estudiante.edu.pe', '987345678', 'Lince', '2006-02-10', 'EST000003'),
(8, 'María Fernanda', 'Rodríguez Castro', 'maria.rodriguez@estudiante.edu.pe', '987456789', 'Magdalena', '2005-09-18', 'EST000004'),
(9, 'Carlos Andrés', 'Sánchez Ramos', 'carlos.sanchez@estudiante.edu.pe', '987567890', 'San Miguel', '2006-07-25', 'EST000005');

-- ========================================
-- 4. INSERTAR CURSOS
-- ========================================

INSERT INTO curso (nombre, codigo_curso, descripcion, creditos, activo, id_docente) VALUES
('Matemáticas I', 'MAT001', 'Curso básico de matemáticas con álgebra y aritmética', 4, true, 2),
('Historia del Perú', 'HIS001', 'Historia del Perú desde épocas precolombinas hasta la actualidad', 3, true, 3),
('Biología General', 'BIO001', 'Introducción a la biología y estudio de los seres vivos', 4, true, 4),
('Química Básica', 'QUI001', 'Fundamentos de química inorgánica y orgánica', 4, true, 4),
('Física I', 'FIS001', 'Mecánica clásica y principios básicos de la física', 4, true, 2);

-- ========================================
-- 5. INSERTAR NOTAS
-- ========================================

-- Notas para Juan Carlos Pérez (id_estudiante = 1)
INSERT INTO nota (id_estudiante, id_curso, nota, tipo_evaluacion, fecha_registro, observaciones) VALUES
-- Matemáticas I
(1, 1, 16.50, 'PARCIAL', '2024-09-01 10:00:00', 'Buen desempeño en álgebra'),
(1, 1, 18.00, 'FINAL', '2024-09-20 14:00:00', 'Excelente mejora en el examen final'),
(1, 1, 15.00, 'TAREA', '2024-09-10 08:00:00', 'Ejercicios bien resueltos'),

-- Historia del Perú
(1, 2, 17.00, 'PARCIAL', '2024-09-05 11:00:00', 'Conoce bien la época incaica'),
(1, 2, 16.00, 'FINAL', '2024-09-22 15:00:00', 'Buena síntesis histórica'),

-- Biología General
(1, 3, 15.50, 'PARCIAL', '2024-09-03 09:00:00', 'Entiende conceptos básicos'),
(1, 3, 17.50, 'PRACTICA', '2024-09-12 16:00:00', 'Excelente trabajo en laboratorio');

-- Notas para Ana Lucía García (id_estudiante = 2)
INSERT INTO nota (id_estudiante, id_curso, nota, tipo_evaluacion, fecha_registro, observaciones) VALUES
-- Matemáticas I
(2, 1, 18.50, 'PARCIAL', '2024-09-01 10:00:00', 'Destacada estudiante en matemáticas'),
(2, 1, 19.00, 'FINAL', '2024-09-20 14:00:00', 'Sobresaliente en todos los temas'),
(2, 1, 18.00, 'TAREA', '2024-09-10 08:00:00', 'Ejercicios perfectos'),

-- Química Básica
(2, 4, 17.00, 'PARCIAL', '2024-09-04 10:30:00', 'Muy buena en fórmulas químicas'),
(2, 4, 18.50, 'PRACTICA', '2024-09-15 14:30:00', 'Excelente en experimentos'),

-- Física I
(2, 5, 16.50, 'PARCIAL', '2024-09-06 11:30:00', 'Comprende bien los conceptos'),
(2, 5, 17.00, 'TAREA', '2024-09-13 09:00:00', 'Problemas bien planteados');

-- Notas para Pedro Miguel Gómez (id_estudiante = 3)
INSERT INTO nota (id_estudiante, id_curso, nota, tipo_evaluacion, fecha_registro, observaciones) VALUES
-- Historia del Perú
(3, 2, 14.00, 'PARCIAL', '2024-09-05 11:00:00', 'Debe reforzar fechas importantes'),
(3, 2, 15.50, 'FINAL', '2024-09-22 15:00:00', 'Ha mejorado considerablemente'),
(3, 2, 16.00, 'TAREA', '2024-09-11 08:30:00', 'Buena investigación'),

-- Biología General
(3, 3, 13.50, 'PARCIAL', '2024-09-03 09:00:00', 'Necesita estudiar más los conceptos'),
(3, 3, 14.00, 'FINAL', '2024-09-21 16:00:00', 'Ligera mejora'),

-- Física I
(3, 5, 12.50, 'PARCIAL', '2024-09-06 11:30:00', 'Dificultades con las fórmulas'),
(3, 5, 13.00, 'TAREA', '2024-09-13 09:00:00', 'Debe practicar más ejercicios');

-- Notas para María Fernanda Rodríguez (id_estudiante = 4)
INSERT INTO nota (id_estudiante, id_curso, nota, tipo_evaluacion, fecha_registro, observaciones) VALUES
-- Matemáticas I
(4, 1, 17.50, 'PARCIAL', '2024-09-01 10:00:00', 'Muy buena en cálculos'),
(4, 1, 16.00, 'FINAL', '2024-09-20 14:00:00', 'Buen desempeño general'),

-- Química Básica
(4, 4, 15.50, 'PARCIAL', '2024-09-04 10:30:00', 'Comprende reacciones básicas'),
(4, 4, 16.50, 'PRACTICA', '2024-09-15 14:30:00', 'Buena técnica en laboratorio'),

-- Historia del Perú
(4, 2, 18.00, 'PARCIAL', '2024-09-05 11:00:00', 'Excelente conocimiento histórico'),
(4, 2, 17.50, 'FINAL', '2024-09-22 15:00:00', 'Muy buena síntesis'),

-- Biología General
(4, 3, 16.00, 'PARCIAL', '2024-09-03 09:00:00', 'Entiende bien los ecosistemas'),
(4, 3, 17.00, 'PRACTICA', '2024-09-12 16:00:00', 'Excelente observación microscópica');

-- Notas para Carlos Andrés Sánchez (id_estudiante = 5)
INSERT INTO nota (id_estudiante, id_curso, nota, tipo_evaluacion, fecha_registro, observaciones) VALUES
-- Física I
(5, 5, 11.00, 'PARCIAL', '2024-09-06 11:30:00', 'Dificultades con conceptos básicos'),
(5, 5, 12.00, 'FINAL', '2024-09-23 11:30:00', 'Pequeña mejora, necesita refuerzo'),
(5, 5, 10.50, 'TAREA', '2024-09-13 09:00:00', 'Ejercicios incompletos'),

-- Matemáticas I
(5, 1, 13.00, 'PARCIAL', '2024-09-01 10:00:00', 'Necesita reforzar álgebra'),
(5, 1, 14.50, 'FINAL', '2024-09-20 14:00:00', 'Ha mejorado con tutorías'),

-- Biología General
(5, 3, 14.00, 'PARCIAL', '2024-09-03 09:00:00', 'Interés en temas ambientales'),
(5, 3, 15.00, 'PRACTICA', '2024-09-12 16:00:00', 'Buen trabajo en equipo');

-- ========================================
-- CONSULTAS DE VERIFICACIÓN
-- ========================================

-- Verificar usuarios creados
SELECT 'USUARIOS CREADOS:' as INFO;
SELECT u.id_usuario, u.username, u.email, u.rol, u.activo FROM usuario u ORDER BY u.rol, u.username;

-- Verificar docentes
SELECT 'DOCENTES CREADOS:' as INFO;
SELECT d.id_docente, d.nombres, d.apellidos, d.especialidad, u.username 
FROM docente d 
JOIN usuario u ON d.id_usuario = u.id_usuario;

-- Verificar estudiantes
SELECT 'ESTUDIANTES CREADOS:' as INFO;
SELECT e.id_estudiante, e.nombres, e.apellidos, e.codigo_estudiante, e.distrito, u.username 
FROM estudiante e 
JOIN usuario u ON e.id_usuario = u.id_usuario;

-- Verificar cursos
SELECT 'CURSOS CREADOS:' as INFO;
SELECT c.id_curso, c.nombre, c.codigo_curso, c.creditos, 
       CONCAT(d.nombres, ' ', d.apellidos) as docente
FROM curso c 
LEFT JOIN docente d ON c.id_docente = d.id_docente;

-- Verificar notas con información completa
SELECT 'NOTAS CREADAS:' as INFO;
SELECT 
    CONCAT(e.nombres, ' ', e.apellidos) as estudiante,
    c.nombre as curso,
    n.nota,
    n.tipo_evaluacion,
    n.fecha_registro
FROM nota n
JOIN estudiante e ON n.id_estudiante = e.id_estudiante
JOIN curso c ON n.id_curso = c.id_curso
ORDER BY e.nombres, c.nombre, n.tipo_evaluacion;

-- Promedios por estudiante
SELECT 'PROMEDIOS POR ESTUDIANTE:' as INFO;
SELECT 
    CONCAT(e.nombres, ' ', e.apellidos) as estudiante,
    ROUND(AVG(n.nota), 2) as promedio_general,
    CASE 
        WHEN AVG(n.nota) >= 18 THEN 'EXCELENTE'
        WHEN AVG(n.nota) >= 16 THEN 'MUY BUENO' 
        WHEN AVG(n.nota) >= 14 THEN 'BUENO'
        WHEN AVG(n.nota) >= 11 THEN 'REGULAR'
        ELSE 'DESAPROBADO'
    END as estado_academico
FROM estudiante e
JOIN nota n ON e.id_estudiante = n.id_estudiante
GROUP BY e.id_estudiante, e.nombres, e.apellidos
ORDER BY promedio_general DESC;

-- ========================================
-- DATOS DE ACCESO
-- ========================================
SELECT 'CREDENCIALES DE ACCESO:' as INFO;
SELECT 
    'ADMIN' as ROL,
    'admin' as USERNAME, 
    'admin123' as PASSWORD
UNION ALL
SELECT 
    'DOCENTE' as ROL,
    'prof.garcia' as USERNAME, 
    'prof123' as PASSWORD
UNION ALL
SELECT 
    'ESTUDIANTE' as ROL,
    'est.juan' as USERNAME, 
    'est123' as PASSWORD;

-- ========================================
-- FIN DEL SCRIPT
-- ========================================