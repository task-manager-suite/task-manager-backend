package org.montadhahri.taskmanager.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.montadhahri.taskmanager.dto.ApiErrorDto;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Global exception handler
 * @author mdh
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    private ApiErrorDto createApiError(HttpServletRequest request, HttpStatus status, String message, Map<String, String> fieldErrors) {
        return new ApiErrorDto(
                LocalDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                message,
                request.getRequestURI(),
                fieldErrors
        );
    }

    /**
     * Handles ResourceNotFoundException.
     * Returns 404 Not Found
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiErrorDto> handleNotFound(ResourceNotFoundException ex, HttpServletRequest request) {
        log.info("Resource not found at [{}]: {}", request.getRequestURI(), ex.getMessage());
        ApiErrorDto error = createApiError(request, HttpStatus.NOT_FOUND, ex.getMessage(), null);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    /**
     * Handles DuplicateResourceException.
     * Returns 409 Conflict
     */
    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ApiErrorDto> handleDuplicate(DuplicateResourceException ex, HttpServletRequest request) {
        ApiErrorDto error = createApiError(request, HttpStatus.CONFLICT, ex.getMessage(), null);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    /**
     * Handles validation failures on @RequestBody Dtos.
     * Returns 400 Bad Request
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorDto> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest request) {
        Map<String, String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        fe -> fe.getDefaultMessage() != null ? fe.getDefaultMessage() : "Invalid value"
                ));

        log.warn("Validation failed on [{}]: {}", request.getRequestURI(), errors);
        ApiErrorDto error = createApiError(request, HttpStatus.BAD_REQUEST, "Validation failed for one or more fields", errors);
        return ResponseEntity.badRequest().body(error);
    }

    /**
     * Handles validation exceptions on method parameters (@RequestParam, @PathVariable)
     * Returns 400 Bad Request
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiErrorDto> handleConstraintViolation(ConstraintViolationException ex, HttpServletRequest request) {
        Map<String, String> errors = ex.getConstraintViolations()
                .stream()
                .collect(Collectors.toMap(
                        v -> v.getPropertyPath().toString(),
                        v -> v.getMessage()
                ));

        ApiErrorDto error = createApiError(request, HttpStatus.BAD_REQUEST, "Constraint violations occurred", errors);
        return ResponseEntity.badRequest().body(error);
    }

    /**
     * Handles malformed JSON in request bodies.
     * Returns 400 Bad Request
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiErrorDto> handleMalformedJson(HttpMessageNotReadableException ex, HttpServletRequest request) {
        ApiErrorDto error = createApiError(request, HttpStatus.BAD_REQUEST, "Malformed JSON request body", null);
        return ResponseEntity.badRequest().body(error);
    }

    /**
     * Handles unsupported HTTP methods.
     * Returns 405 Method Not Allowed.
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiErrorDto> handleMethodNotSupported(HttpRequestMethodNotSupportedException ex, HttpServletRequest request) {
        String message = String.format("HTTP method %s not supported for this endpoint", ex.getMethod());
        ApiErrorDto error = createApiError(request, HttpStatus.METHOD_NOT_ALLOWED, message, null);
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(error);
    }

    /**
     * Handles database integrity violations such as unique constraint violations.
     * Returns 409 Conflict
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiErrorDto> handleDataIntegrityViolation(DataIntegrityViolationException ex, HttpServletRequest request) {
        // Log detailed internal error message server-side, not sent to client
        ApiErrorDto error = createApiError(request, HttpStatus.CONFLICT, "Conflict: Duplicate or invalid data.", null);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    /**
     * Handles all other exceptions.
     * Returns 500 Internal Server Error
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorDto> handleAll(Exception ex, HttpServletRequest request) {
        log.error("Unexpected error occurred at [{}]: {}", request.getRequestURI(), ex.getMessage(), ex);
        ApiErrorDto error = createApiError(request, HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred. Please try again later.", null);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}