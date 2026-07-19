package com.idkwhy.exception;

import org.springframework.http.HttpStatus;

public class InvalidRoomNumberException extends ApiException {

    public InvalidRoomNumberException(String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }
}
