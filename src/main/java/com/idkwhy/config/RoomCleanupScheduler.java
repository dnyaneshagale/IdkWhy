package com.idkwhy.config;

import com.idkwhy.service.RoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class RoomCleanupScheduler {

    private final RoomService roomService;

    @Scheduled(fixedRate = 30 * 60 * 1000L)
    public void cleanupExpiredRooms() {
        log.debug("Running room cleanup scheduler");
        roomService.cleanupExpiredRooms();
    }
}
