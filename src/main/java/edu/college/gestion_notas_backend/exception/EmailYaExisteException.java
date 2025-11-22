package edu.college.gestion_notas_backend.exception;

public class EmailYaExisteException extends RuntimeException {
    public EmailYaExisteException(String email) {
        super("El correo electrónico ya está registrado: " + email);
    }
}
