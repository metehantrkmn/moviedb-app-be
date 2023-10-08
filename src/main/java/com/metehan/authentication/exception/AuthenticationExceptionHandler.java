package com.metehan.authentication.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

@ControllerAdvice
public class AuthenticationExceptionHandler {

    @ExceptionHandler( value = {NoSuchUserExists.class})
    public ResponseEntity handleNoSuchUserExistsException(NoSuchUserExists ex){
        HttpStatus status = HttpStatus.BAD_REQUEST;
        LocalDateTime date = LocalDateTime.now();
        AuthenticationException exception = AuthenticationException.builder()
                .date(date)
                .status(status.toString())
                .message(ex.getMessage())
                .throwable(ex.getCause())
                .build();
        return new ResponseEntity(exception,status);
    }

    @ExceptionHandler(value = {UserAlreadyExists.class})
    public ResponseEntity handleUserAlreadyExistsException(UserAlreadyExists ex){
        HttpStatus status = HttpStatus.BAD_REQUEST;
        LocalDateTime date = LocalDateTime.now();
        AuthenticationException exception = AuthenticationException.builder()
                .date(date)
                .status(status.toString())
                .message(ex.getMessage())
                .throwable(ex.getCause())
                .build();
        return new ResponseEntity(exception,status);
    }

    @ExceptionHandler(value={UsernameNotFoundException.class})
    public ResponseEntity<Object> handleAuthenticationUserNameNotFoundException(UsernameNotFoundException e){

        HttpStatus status = HttpStatus.NOT_FOUND;
        LocalDateTime date = LocalDateTime.now();

        //"1. create payload containing exception details
        AuthenticationException exception = AuthenticationException.builder()
                .date(date)
                .status(status.toString())
                .message(e.getMessage())
                .throwable(e.getCause())
                .build();

        //2. return response entity
        return new ResponseEntity<>(exception,status);
    }

    @ExceptionHandler(value = {NoSuchConfirmationToken.class})
    public ResponseEntity handleConfirmationTokenException(NoSuchConfirmationToken ex){

        HttpStatus status = HttpStatus.NOT_FOUND;
        LocalDateTime date = LocalDateTime.now();

        //"1. create payload containing exception details
        AuthenticationException exception = AuthenticationException.builder()
                .date(date)
                .status(status.toString())
                .message(ex.getMessage())
                .throwable(ex.getCause())
                .build();

        //2. return response entity
        return new ResponseEntity<>(exception,status);
    }

    @ExceptionHandler(value = {ExpiredConfirmationTokenException.class})
    public ResponseEntity handleExpiredConfirmationTokenException(ExpiredConfirmationTokenException ex){

        HttpStatus status = HttpStatus.BAD_REQUEST;
        LocalDateTime date = LocalDateTime.now();

        //"1. create payload containing exception details
        AuthenticationException exception = AuthenticationException.builder()
                .date(date)
                .status(status.toString())
                .message(ex.getMessage())
                .throwable(ex.getCause())
                .build();

        //2. return response entity
        return new ResponseEntity<>(exception,status);
    }

}
