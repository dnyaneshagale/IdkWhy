package com.idkwhy.exception;

import org.springframework.http.HttpStatus;

import java.util.UUID;

public class MessageNotFoundException extends ApiException {

    public MessageNotFoundException(UUID messageId) {
        super(HttpStatus.NOT_FOUND, "Message " + messageId + " was not found.");
    }
}
