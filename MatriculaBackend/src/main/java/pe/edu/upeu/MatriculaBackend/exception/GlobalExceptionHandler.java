package pe.edu.upeu.MatriculaBackend.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import pe.edu.upeu.MatriculaBackend.exception.dto.ErrorResponseDTO;
import java.time.LocalDateTime;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDTO> handleValidationErrors(MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult().getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .toList();

        ErrorResponseDTO dto = ErrorResponseDTO.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Error de Validación")
                .message("La solicitud contiene datos inválidos")
                .timestamp(LocalDateTime.now())
                .validationErrors(errors)
                .build();

        log.warn("Error de validación: {}", errors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(dto);
    }

    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<ErrorResponseDTO> handleNotFound(RecursoNoEncontradoException ex) {
        ErrorResponseDTO dto = ErrorResponseDTO.builder()
                .status(HttpStatus.NOT_FOUND.value())
                .error("Recurso no encontrado")
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .build();

        log.warn("Recurso no encontrado: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(dto);
    }

    @ExceptionHandler(ReglaNegocioException.class)
    public ResponseEntity<ErrorResponseDTO> handleBusinessRule(ReglaNegocioException ex) {
        ErrorResponseDTO dto = ErrorResponseDTO.builder()
                .status(HttpStatus.CONFLICT.value())
                .error("Conflicto de Negocio")
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .build();

        log.warn("Regla de negocio violada: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(dto);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleGenericException(Exception ex) {
        log.error("Error interno del servidor no controlado: ", ex);

        ErrorResponseDTO dto = ErrorResponseDTO.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error("Error Interno")
                .message("Ocurrió un error inesperado en el servidor")
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(dto);
    }
}