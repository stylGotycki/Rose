package net.rose.rose_identity_service.core.rest.handler;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import net.rose.rose_identity_service.core.exception.AppException;
import net.rose.rose_identity_service.core.exception.ErrorCode;
import net.rose.rose_identity_service.core.rest.dto.ErrorResponseDto;
import net.rose.rose_identity_service.core.rest.dto.FieldValidationErrorDto;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestCookieException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.Comparator;
import java.util.List;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(AppException.class)
    public ResponseEntity<ErrorResponseDto> handleAppException(
            AppException exception,
            HttpServletRequest request
    ) {
        ErrorResponseDto body = ErrorResponseDto.of(
                exception.getStatus().value(),
                exception.getErrorCode(),
                exception.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity
                .status(exception.getStatus())
                .body(body);
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDto> handleValidationException(
            MethodArgumentNotValidException exception,
            HttpServletRequest request
    ) {
        List<FieldValidationErrorDto> errors = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .sorted(Comparator.comparing(FieldError::getField))
                .map(error -> new FieldValidationErrorDto(
                        error.getField(),
                        error.getDefaultMessage(),
                        getSafeRejectedValue(error)
                ))
                .toList();

        ErrorResponseDto body = ErrorResponseDto.of(
                HttpStatus.BAD_REQUEST.value(),
                ErrorCode.VALIDATION_ERROR,
                "Request validation failed.",
                request.getRequestURI(),
                errors
        );

        return ResponseEntity
                .badRequest()
                .body(body);
    }


    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponseDto> handleHttpMessageNotReadableException(
            HttpMessageNotReadableException exception,
            HttpServletRequest request
    ) {
        ErrorResponseDto body = ErrorResponseDto.of(
                HttpStatus.BAD_REQUEST.value(),
                ErrorCode.BAD_REQUEST,
                "Request body is missing or malformed.",
                request.getRequestURI()
        );

        return ResponseEntity
                .badRequest()
                .body(body);
    }


    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponseDto> handleMethodArgumentTypeMismatchException(
            MethodArgumentTypeMismatchException exception,
            HttpServletRequest request
    ) {
        String requiredType = exception.getRequiredType() != null
                ? exception.getRequiredType().getSimpleName()
                : "required type";

        ErrorResponseDto body = ErrorResponseDto.of(
                HttpStatus.BAD_REQUEST.value(),
                ErrorCode.BAD_REQUEST,
                "Parameter '" + exception.getName() + "' should be of type " + requiredType + ".",
                request.getRequestURI()
        );

        return ResponseEntity
                .badRequest()
                .body(body);
    }


    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponseDto> handleConstraintViolationException(
            ConstraintViolationException exception,
            HttpServletRequest request
    ) {
        List<FieldValidationErrorDto> errors = exception.getConstraintViolations()
                .stream()
                .map(violation -> new FieldValidationErrorDto(
                        violation.getPropertyPath().toString(),
                        violation.getMessage(),
                        null
                ))
                .toList();

        ErrorResponseDto body = ErrorResponseDto.of(
                HttpStatus.BAD_REQUEST.value(),
                ErrorCode.VALIDATION_ERROR,
                "Request validation failed.",
                request.getRequestURI(),
                errors
        );

        return ResponseEntity
                .badRequest()
                .body(body);
    }


    @ExceptionHandler(MissingRequestCookieException.class)
    public ResponseEntity<ErrorResponseDto> handleMissingCookieException(
            MissingRequestCookieException exception,
            HttpServletRequest request
    ) {
        ErrorResponseDto body = ErrorResponseDto.of(
                HttpStatus.UNAUTHORIZED.value(),
                ErrorCode.UNAUTHORIZED,
                "Required cookie '" + exception.getCookieName() + "' is missing.",
                request.getRequestURI()
        );

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(body);
    }


    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponseDto> handleMissingRequestParameterException(
            MissingServletRequestParameterException exception,
            HttpServletRequest request
    ) {
        ErrorResponseDto body = ErrorResponseDto.of(
                HttpStatus.BAD_REQUEST.value(),
                ErrorCode.BAD_REQUEST,
                "Required request parameter '" + exception.getParameterName() + "' is missing.",
                request.getRequestURI()
        );

        return ResponseEntity
                .badRequest()
                .body(body);
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleUnknownException(
            Exception exception,
            HttpServletRequest request
    ) {
        log.error("Unexpected exception occurred while processing request: {}", request.getRequestURI(), exception);

        ErrorResponseDto body = ErrorResponseDto.of(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                ErrorCode.INTERNAL_SERVER_ERROR,
                "Unexpected internal server error.",
                request.getRequestURI()
        );

        return ResponseEntity
                .internalServerError()
                .body(body);
    }


    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponseDto> handleDataIntegrityViolationException(
            DataIntegrityViolationException exception,
            HttpServletRequest request
    ) {
        ErrorResponseDto body = ErrorResponseDto.of(
                HttpStatus.CONFLICT.value(),
                ErrorCode.ENTITY_ALREADY_EXISTS,
                "Database constraint violation.",
                request.getRequestURI()
        );

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(body);
    }


    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ErrorResponseDto> handleHttpMediaTypeNotSupportedException(
            HttpMediaTypeNotSupportedException exception,
            HttpServletRequest request
    ) {
        ErrorResponseDto body = ErrorResponseDto.of(
                HttpStatus.UNSUPPORTED_MEDIA_TYPE.value(),
                ErrorCode.BAD_REQUEST,
                "Unsupported content type.",
                request.getRequestURI()
        );

        return ResponseEntity
                .status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                .body(body);
    }


    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponseDto> handleHttpRequestMethodNotSupportedException(
            HttpRequestMethodNotSupportedException exception,
            HttpServletRequest request
    ) {
        ErrorResponseDto body = ErrorResponseDto.of(
                HttpStatus.METHOD_NOT_ALLOWED.value(),
                ErrorCode.BAD_REQUEST,
                "HTTP method is not supported for this endpoint.",
                request.getRequestURI()
        );

        return ResponseEntity
                .status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(body);
    }


    @ExceptionHandler(MissingRequestHeaderException.class)
    public ResponseEntity<ErrorResponseDto> handleMissingRequestHeaderException(
            MissingRequestHeaderException exception,
            HttpServletRequest request
    ) {
        ErrorResponseDto body = ErrorResponseDto.of(
                HttpStatus.BAD_REQUEST.value(),
                ErrorCode.BAD_REQUEST,
                "Required header '" + exception.getHeaderName() + "' is missing.",
                request.getRequestURI()
        );

        return ResponseEntity
                .badRequest()
                .body(body);
    }


    @ExceptionHandler(UnsupportedOperationException.class)
    public ResponseEntity<ErrorResponseDto> handleUnsupportedOperationException(
            UnsupportedOperationException exception,
            HttpServletRequest request
    ) {
        ErrorResponseDto body = ErrorResponseDto.of(
                HttpStatus.NOT_IMPLEMENTED.value(),
                ErrorCode.INTERNAL_SERVER_ERROR,
                "Endpoint is not implemented yet.",
                request.getRequestURI()
        );

        return ResponseEntity
                .status(HttpStatus.NOT_IMPLEMENTED)
                .body(body);
    }


    private Object getSafeRejectedValue(FieldError error) {
        String field = error.getField().toLowerCase();

        if (
                field.contains("password") ||
                        field.contains("token") ||
                        field.contains("secret")
        ) {
            return null;
        }

        return error.getRejectedValue();
    }

}
