package com.idkwhy.exception;

import org.springframework.http.HttpStatus;

public class DuplicateRoomException extends ApiException {

    public DuplicateRoomException(String roomNumber) {
        super(HttpStatus.CONFLICT, "Room " + roomNumber + " already exists.");
    }
}
