package com.kelp_6.banking_apps.cron;

import com.kelp_6.banking_apps.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

@EnableAsync
@Component
@RequiredArgsConstructor
public class ScheduleCron {
    private final static Logger LOGGER = LoggerFactory.getLogger(ScheduleCron.class);
    private final ScheduleService scheduleService;
    private final ZoneId zoneId = ZoneId.of("Asia/Jakarta");

    @Async
    @Scheduled(cron = "0 0 0 * * *", zone = "Asia/Jakarta")
    public void transferAutomation(){
        LOGGER.info("accessed");

        LocalDate globalCurrentDate = LocalDate.now(zoneId);
        ZonedDateTime zonedDateTime = globalCurrentDate.atStartOfDay(zoneId);
        Date currentLocalDate = Date.from(zonedDateTime.toInstant());

        LOGGER.info("scheduled cron for {}", currentLocalDate);
        scheduleService.scheduleTransferAutomation(currentLocalDate);

        LOGGER.info("accessed after perform transfer automation");
    }

}
