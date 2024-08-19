package com.kelp_6.banking_apps.controller.transfer.schedule;

import com.kelp_6.banking_apps.model.schedule.DetailScheduledTransactionResponse;
import com.kelp_6.banking_apps.model.schedule.ScheduledTransactionRequest;
import com.kelp_6.banking_apps.model.schedule.ScheduledTransactionResponse;
import com.kelp_6.banking_apps.model.schedule.UpdateScheduledTransactionRequest;
import com.kelp_6.banking_apps.model.web.WebResponse;
import com.kelp_6.banking_apps.service.ScheduledTransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/transfer-scheduler")
@Slf4j
public class ScheduledTransactionController {
    private final ScheduledTransactionService scheduledTransactionService;

    @PostMapping(
            value = {"", "/"},
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<ScheduledTransactionResponse> createSchedule(
            @RequestBody @Valid ScheduledTransactionRequest request,
            @RequestHeader(value = "X-PIN-TOKEN") String pinToken,
            Authentication authentication
    ){
        log.info("controller.transfer.schedule - ScheduledTransactionController - createSchedule - accessed");

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        request.setUserID(userDetails.getUsername());
        request.setPinToken(pinToken);

        ScheduledTransactionResponse scheduledTransactionResponse = scheduledTransactionService.createSchedule(request);

        return WebResponse.<ScheduledTransactionResponse>builder()
                .status("success")
                .message("schedule created successfully")
                .data(scheduledTransactionResponse)
                .build();
    }

    @GetMapping(
            value = {"", "/"},
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<List<ScheduledTransactionResponse>> getAllSchedules(Authentication authentication){
        log.info("controller.transfer.schedule - ScheduledTransactionController - getAllSchedules - accessed");

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        List<ScheduledTransactionResponse> allSchedules = scheduledTransactionService.getAllSchedule(userDetails.getUsername());

        return WebResponse.<List<ScheduledTransactionResponse>>builder()
                .status("success")
                .message("all schedules retrieved successfully")
                .data(allSchedules)
                .build();
    }

    @GetMapping(
            value = {"/{scheduleId}", "/{scheduleId}/"},
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<DetailScheduledTransactionResponse> getDetailSchedule(
            @PathVariable(value = "scheduleId") String scheduleId,
            Authentication authentication
    ){
        log.info("controller.transfer.schedule - ScheduledTransactionController - getDetailSchedule - accessed");

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        DetailScheduledTransactionResponse detailScheduledTransactionResponse = scheduledTransactionService.getScheduleByScheduleID(scheduleId, userDetails.getUsername());

        return WebResponse.<DetailScheduledTransactionResponse>builder()
                .status("success")
                .message("detail schedule retrieved successfully")
                .data(detailScheduledTransactionResponse)
                .build();
    }

    @PutMapping(
            value = {"/{scheduleId}", "/{scheduleId}/"},
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<ScheduledTransactionResponse> updateSchedule(
            @PathVariable(value = "scheduleId") String scheduleId,
            @RequestBody @Valid UpdateScheduledTransactionRequest request,
            @RequestHeader(value = "X-PIN-TOKEN") String pinToken,
            Authentication authentication
    ){
        log.info("controller.transfer.schedule - ScheduledTransactionController - updateSchedule - accessed");

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        request.setScheduleID(scheduleId);
        request.setUserID(userDetails.getUsername());
        request.setPinToken(pinToken);

        ScheduledTransactionResponse scheduledTransactionResponse = scheduledTransactionService.updateSchedule(request);

        return WebResponse.<ScheduledTransactionResponse>builder()
                .status("success")
                .message("schedule updated successfully")
                .data(scheduledTransactionResponse)
                .build();
    }

    @PutMapping(
            value = {"/cancel/{scheduleId}", "/cancel/{scheduleId}/"},
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<ScheduledTransactionResponse> cancelSchedule(
            @PathVariable(value = "scheduleId") String scheduleId,
            @RequestHeader(value = "X-PIN-TOKEN") String pinToken,
            Authentication authentication
    ){
        log.info("controller.transfer.schedule - ScheduledTransactionController - cancelSchedule - accessed");

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        ScheduledTransactionResponse scheduledTransactionResponse = scheduledTransactionService.cancelSchedule(scheduleId, userDetails.getUsername(), pinToken);

        return WebResponse.<ScheduledTransactionResponse>builder()
                .status("success")
                .message("schedule canceled successfully")
                .data(scheduledTransactionResponse)
                .build();
    }

}
