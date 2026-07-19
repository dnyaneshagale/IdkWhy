package com.idkwhy.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record JoinRoomRequest(
        @NotBlank(message = "Room number is required.")
        @Pattern(regexp = "^[A-Za-z0-9]{6,12}$", message = "Room number must be 6-12 characters and contain only letters and numbers.")
        String roomNumber,

        @Size(max = 30, message = "Password cannot exceed 30 characters.")
        String password
) {
}
