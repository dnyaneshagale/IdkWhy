package com.idkwhy.model;

import java.time.Instant;
import java.util.UUID;

public record Message(UUID id, String text, Instant createdAt) {
}
