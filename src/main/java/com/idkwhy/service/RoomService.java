package com.idkwhy.service;

import com.idkwhy.dto.request.CreateMessageRequest;
import com.idkwhy.dto.request.CreateRoomRequest;
import com.idkwhy.dto.request.JoinRoomRequest;
import com.idkwhy.dto.response.RoomResponse;

import java.util.UUID;

public interface RoomService {

    RoomResponse createRoom(CreateRoomRequest request);

    RoomResponse joinRoom(JoinRoomRequest request);

    RoomResponse getRoom(String roomNumber, String password);

    RoomResponse sendMessage(String roomNumber, String password, CreateMessageRequest request);

    RoomResponse clearMessages(String roomNumber, String password);

    void deleteMessage(String roomNumber, String password, UUID messageId);

    void deleteRoom(String roomNumber, String password);

    void cleanupExpiredRooms();
}
