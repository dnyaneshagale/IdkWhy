package com.idkwhy.service.impl;

import com.idkwhy.dto.request.CreateMessageRequest;
import com.idkwhy.dto.request.CreateRoomRequest;
import com.idkwhy.dto.request.JoinRoomRequest;
import com.idkwhy.dto.response.RoomResponse;
import com.idkwhy.exception.DuplicateRoomException;
import com.idkwhy.exception.InvalidRoomNumberException;
import com.idkwhy.exception.MessageNotFoundException;
import com.idkwhy.exception.RoomAccessDeniedException;
import com.idkwhy.exception.RoomNotFoundException;
import com.idkwhy.model.Room;
import com.idkwhy.service.RoomService;
import com.idkwhy.util.RoomNumberUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
@RequiredArgsConstructor
public class InMemoryRoomService implements RoomService {

    private final ConcurrentHashMap<String, Room> rooms = new ConcurrentHashMap<>();

    @Override
    public RoomResponse createRoom(CreateRoomRequest request) {
        String roomNumber = normalizeRoomNumber(request.roomNumber());
        String password = normalizePassword(request.password());
        Room room = new Room(roomNumber, password, Instant.now());
        Room existing = rooms.putIfAbsent(roomNumber, room);
        if (existing != null) {
            throw new DuplicateRoomException(roomNumber);
        }
        log.info("Created room {}", roomNumber);
        return RoomResponse.from(room);
    }

    @Override
    public RoomResponse joinRoom(JoinRoomRequest request) {
        String roomNumber = normalizeRoomNumber(request.roomNumber());
        Room room = requireRoom(roomNumber);
        verifyPassword(room, normalizePassword(request.password()));
        synchronized (room) {
            room.touch();
            return RoomResponse.from(room);
        }
    }

    @Override
    public RoomResponse getRoom(String roomNumber, String password) {
        Room room = requireRoom(normalizeRoomNumber(roomNumber));
        verifyPassword(room, normalizePassword(password));
        synchronized (room) {
            room.touch();
            return RoomResponse.from(room);
        }
    }

    @Override
    public RoomResponse sendMessage(String roomNumber, String password, CreateMessageRequest request) {
        String normalizedRoomNumber = normalizeRoomNumber(roomNumber);
        Room room = requireRoom(normalizedRoomNumber);
        verifyPassword(room, normalizePassword(password));
        synchronized (room) {
            room.addMessage(request.text());
            return RoomResponse.from(room);
        }
    }

    @Override
    public RoomResponse clearMessages(String roomNumber, String password) {
        Room room = requireRoom(normalizeRoomNumber(roomNumber));
        verifyPassword(room, normalizePassword(password));
        synchronized (room) {
            room.clearMessages();
            return RoomResponse.from(room);
        }
    }

    @Override
    public void deleteMessage(String roomNumber, String password, UUID messageId) {
        Room room = requireRoom(normalizeRoomNumber(roomNumber));
        verifyPassword(room, normalizePassword(password));
        synchronized (room) {
            boolean removed = room.deleteMessage(messageId);
            if (!removed) {
                throw new MessageNotFoundException(messageId);
            }
        }
    }

    @Override
    public void deleteRoom(String roomNumber, String password) {
        String normalizedRoomNumber = normalizeRoomNumber(roomNumber);
        Room room = requireRoom(normalizedRoomNumber);
        verifyPassword(room, normalizePassword(password));
        synchronized (room) {
            rooms.remove(normalizedRoomNumber, room);
            log.info("Deleted room {}", normalizedRoomNumber);
        }
    }

    @Override
    public void cleanupExpiredRooms() {
        Instant now = Instant.now();
        rooms.forEach((roomNumber, room) -> {
            if (room.isExpired(now) && rooms.remove(roomNumber, room)) {
                log.info("Removed expired room {}", roomNumber);
            }
        });
    }

    private Room requireRoom(String roomNumber) {
        Room room = rooms.get(roomNumber);
        if (room == null) {
            throw new RoomNotFoundException(roomNumber);
        }
        return room;
    }

    private void verifyPassword(Room room, String candidatePassword) {
        if (!room.matchesPassword(candidatePassword)) {
            throw new RoomAccessDeniedException();
        }
    }

    private String normalizeRoomNumber(String roomNumber) {
        try {
            return RoomNumberUtil.normalize(roomNumber);
        } catch (InvalidRoomNumberException exception) {
            throw exception;
        }
    }

    private String normalizePassword(String password) {
        if (password == null) {
            return null;
        }
        String normalized = password.trim();
        return normalized.isEmpty() ? null : normalized;
    }
}
