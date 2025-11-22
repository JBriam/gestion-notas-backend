package edu.college.gestion_notas_backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import edu.college.gestion_notas_backend.dto.response.ErrorResponseDTO;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EmailYaExisteException.class)
    public ResponseEntity<ErrorResponseDTO> handleEmailYaExisteException(EmailYaExisteException ex) {
        ErrorResponseDTO error = new ErrorResponseDTO(
            HttpStatus.CONFLICT.value(),
            ex.getMessage(),
            "EMAIL_DUPLICADO"
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponseDTO> handleRuntimeException(RuntimeException ex) {
        ErrorResponseDTO error = new ErrorResponseDTO(
            HttpStatus.BAD_REQUEST.value(),
            ex.getMessage(),
            "ERROR_VALIDACION"
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
}
