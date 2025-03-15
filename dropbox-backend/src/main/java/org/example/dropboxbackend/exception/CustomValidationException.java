package org.example.dropboxbackend.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class CustomValidationException extends RuntimeException{

    private final HttpStatus status;

    public CustomValidationException(String message, HttpStatus status){
        super(message);
        this.status = status;
    }
}
