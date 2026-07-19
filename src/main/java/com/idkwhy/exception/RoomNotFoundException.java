package com.idkwhy.exception;

import org.springframework.http.HttpStatus;

public class RoomNotFoundException extends ApiException {

    public RoomNotFoundException(String roomNumber) {
        super(HttpStatus.NOT_FOUND, "Room " + roomNumber + " was not found.");
    }
}
