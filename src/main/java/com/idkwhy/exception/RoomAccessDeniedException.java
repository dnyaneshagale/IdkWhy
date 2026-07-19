package com.idkwhy.exception;

import org.springframework.http.HttpStatus;

public class RoomAccessDeniedException extends ApiException {

    public RoomAccessDeniedException() {
        super(HttpStatus.FORBIDDEN, "Invalid password.");
    }
}
