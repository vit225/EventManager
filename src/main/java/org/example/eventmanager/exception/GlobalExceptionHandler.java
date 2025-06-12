package org.example.eventmanager.exception;

import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ServerMessageDto> handleNotFoundException(EntityNotFoundException e) {
        log.error("Got exception", e);
        ServerMessageDto messageDto = new ServerMessageDto("Сущность не найдена", e.getMessage(),
                LocalDateTime.now());

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(messageDto);
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ServerMessageDto> handleValidationException(MethodArgumentNotValidException e) {
        log.error("Got validation exception", e);
        var messageDto = new ServerMessageDto("Ошибка валидации запроса", e.getMessage(), LocalDateTime.now());

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(messageDto);
    }


    @ExceptionHandler
    public ResponseEntity<ServerMessageDto> handleGenerisException(Exception e) {
        log.error("Server error", e);
        var errorDto = new ServerMessageDto("Server error", e.getMessage(), LocalDateTime.now());

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(errorDto);
    }
}
