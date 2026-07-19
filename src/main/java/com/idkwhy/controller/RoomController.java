package com.idkwhy.controller;

import com.idkwhy.dto.request.CreateMessageRequest;
import com.idkwhy.dto.request.CreateRoomRequest;
import com.idkwhy.dto.request.JoinRoomRequest;
import com.idkwhy.dto.response.RoomResponse;
import com.idkwhy.service.RoomService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/rooms")
@RequiredArgsConstructor
public class RoomController {

    private static final String PASSWORD_HEADER = "X-Room-Password";

    private final RoomService roomService;

    @PostMapping
    public ResponseEntity<RoomResponse> createRoom(@Valid @RequestBody CreateRoomRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(roomService.createRoom(request));
    }

    @PostMapping("/join")
    public ResponseEntity<RoomResponse> joinRoom(@Valid @RequestBody JoinRoomRequest request) {
        return ResponseEntity.ok(roomService.joinRoom(request));
    }

    @GetMapping("/{room}")
    public ResponseEntity<RoomResponse> getRoom(
            @PathVariable String room,
            @RequestHeader(value = PASSWORD_HEADER, required = false) String password
    ) {
        return ResponseEntity.ok(roomService.getRoom(room, password));
    }

    @PostMapping("/{room}/messages")
    public ResponseEntity<RoomResponse> sendMessage(
            @PathVariable String room,
            @RequestHeader(value = PASSWORD_HEADER, required = false) String password,
            @Valid @RequestBody CreateMessageRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(roomService.sendMessage(room, password, request));
    }

    @DeleteMapping("/{room}/messages/{id}")
    public ResponseEntity<Void> deleteMessage(
            @PathVariable String room,
            @RequestHeader(value = PASSWORD_HEADER, required = false) String password,
            @PathVariable UUID id
    ) {
        roomService.deleteMessage(room, password, id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{room}/messages")
    public ResponseEntity<RoomResponse> clearMessages(
            @PathVariable String room,
            @RequestHeader(value = PASSWORD_HEADER, required = false) String password
    ) {
        return ResponseEntity.ok(roomService.clearMessages(room, password));
    }

    @DeleteMapping("/{room}")
    public ResponseEntity<Void> deleteRoom(
            @PathVariable String room,
            @RequestHeader(value = PASSWORD_HEADER, required = false) String password
    ) {
        roomService.deleteRoom(room, password);
        return ResponseEntity.noContent().build();
    }
}
