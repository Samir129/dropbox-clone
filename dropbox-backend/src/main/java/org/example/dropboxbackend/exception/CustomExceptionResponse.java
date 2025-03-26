package org.example.dropboxbackend.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class CustomExceptionResponse extends RuntimeException{

    private final HttpStatus status;

    public CustomExceptionResponse(String message, HttpStatus status){
        super(message);
        this.status = status;
    }
}
