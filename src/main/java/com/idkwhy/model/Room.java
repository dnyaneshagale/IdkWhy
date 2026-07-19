package com.idkwhy.model;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedDeque;

public class Room {

    private static final int MAX_MESSAGES = 50;
    private static final Duration EXPIRATION_DURATION = Duration.ofDays(2);

    private final String roomNumber;
    private final String password;
    private final Instant createdAt;
    private final ConcurrentLinkedDeque<Message> messages;
    private volatile Instant lastActivity;

    public Room(String roomNumber, String password, Instant createdAt) {
        this.roomNumber = roomNumber;
        this.password = password;
        this.createdAt = createdAt;
        this.messages = new ConcurrentLinkedDeque<>();
        this.lastActivity = createdAt;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public String getPassword() {
        return password;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getLastActivity() {
        return lastActivity;
    }

    public boolean isPasswordProtected() {
        return password != null;
    }

    public synchronized void touch() {
        lastActivity = Instant.now();
    }

    public synchronized Message addMessage(String text) {
        Message message = new Message(UUID.randomUUID(), text, Instant.now());
        messages.addFirst(message);
        trimToLimit();
        touch();
        return message;
    }

    public synchronized boolean deleteMessage(UUID messageId) {
        boolean removed = messages.removeIf(message -> message.id().equals(messageId));
        if (removed) {
            touch();
        }
        return removed;
    }

    public synchronized void clearMessages() {
        messages.clear();
        touch();
    }

    public synchronized List<Message> snapshotMessages() {
        return List.copyOf(messages);
    }

    public boolean matchesPassword(String candidatePassword) {
        return password == null || password.equals(candidatePassword);
    }

    public boolean isExpired(Instant now) {
        return !lastActivity.isAfter(now.minus(EXPIRATION_DURATION));
    }

    private void trimToLimit() {
        while (messages.size() > MAX_MESSAGES) {
            messages.pollLast();
        }
    }
}
