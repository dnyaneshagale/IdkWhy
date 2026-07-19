package com.idkwhy.dto.response;

import com.idkwhy.model.Room;

import java.time.Instant;
import java.util.List;

public record RoomResponse(
        String roomNumber,
        boolean passwordProtected,
        Instant createdAt,
        Instant lastActivity,
        int messageCount,
        List<MessageResponse> messages
) {
    public static RoomResponse from(Room room) {
        List<MessageResponse> messages = room.snapshotMessages().stream()
                .map(MessageResponse::from)
                .toList();
        return new RoomResponse(
                room.getRoomNumber(),
                room.isPasswordProtected(),
                room.getCreatedAt(),
                room.getLastActivity(),
                messages.size(),
                messages
        );
    }
}
