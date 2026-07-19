package com.idkwhy.util;

import com.idkwhy.exception.InvalidRoomNumberException;

import java.util.Locale;
import java.util.regex.Pattern;

public final class RoomNumberUtil {

    private static final Pattern ROOM_PATTERN = Pattern.compile("^[A-Za-z0-9]{6,12}$");

    private RoomNumberUtil() {
    }

    public static String normalize(String roomNumber) {
        if (roomNumber == null) {
            throw new InvalidRoomNumberException("Room number is required.");
        }

        String normalized = roomNumber.trim();
        if (!ROOM_PATTERN.matcher(normalized).matches()) {
            throw new InvalidRoomNumberException("Room number must be 6-12 characters and contain only letters and numbers.");
        }
        return normalized.toUpperCase(Locale.ROOT);
    }

    public static boolean isValid(String roomNumber) {
        return roomNumber != null && ROOM_PATTERN.matcher(roomNumber.trim()).matches();
    }
}
