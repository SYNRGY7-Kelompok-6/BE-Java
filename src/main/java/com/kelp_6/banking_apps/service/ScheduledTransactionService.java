package com.kelp_6.banking_apps.service;

import com.kelp_6.banking_apps.entity.ScheduledTransaction;
import com.kelp_6.banking_apps.model.schedule.DetailScheduledTransactionResponse;
import com.kelp_6.banking_apps.model.schedule.ScheduledTransactionRequest;
import com.kelp_6.banking_apps.model.schedule.ScheduledTransactionResponse;
import com.kelp_6.banking_apps.model.schedule.UpdateScheduledTransactionRequest;

import java.util.List;

public interface ScheduledTransactionService {
    ScheduledTransactionResponse createSchedule(ScheduledTransactionRequest request);
    List<ScheduledTransactionResponse> getAllSchedule(String userID);
    DetailScheduledTransactionResponse getScheduleByScheduleID(String scheduleID, String userID);
    ScheduledTransactionResponse updateSchedule(UpdateScheduledTransactionRequest request);
    ScheduledTransactionResponse cancelSchedule(String scheduleID, String userID, String pinToken);
}
