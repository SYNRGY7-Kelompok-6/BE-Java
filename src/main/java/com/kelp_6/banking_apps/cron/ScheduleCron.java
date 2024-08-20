package com.kelp_6.banking_apps.cron;

import com.kelp_6.banking_apps.repository.ScheduledTransactionRepository;
import com.kelp_6.banking_apps.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

@EnableAsync
@Component
@RequiredArgsConstructor
@Slf4j
public class ScheduleCron {
    private final ScheduleService scheduleService;
    private final ZoneId zoneId = ZoneId.of("Asia/Jakarta");

    @Async
    @Scheduled(cron = "0 0 0 * * *", zone = "Asia/Jakarta")
    public void transferAutomation(){
        log.info("cron - ScheduleCron - transferAutomation - accessed");

        LocalDate globalCurrentDate = LocalDate.now(zoneId);
        ZonedDateTime zonedDateTime = globalCurrentDate.atStartOfDay(zoneId);
        Date currentLocalDate = Date.from(zonedDateTime.toInstant());

        scheduleService.scheduleTransferAutomation(currentLocalDate);

        log.info("cron - ScheduleCron - transferAutomation - accessed after perform transfer automation");
    }

}
