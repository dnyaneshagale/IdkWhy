package com.idkwhy.dto.response;

import com.idkwhy.model.Message;

import java.time.Instant;
import java.util.UUID;

public record MessageResponse(UUID id, String text, Instant createdAt) {

    public static MessageResponse from(Message message) {
        return new MessageResponse(message.id(), message.text(), message.createdAt());
    }
}
