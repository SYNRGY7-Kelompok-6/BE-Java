package com.kelp_6.banking_apps.model.schedule;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DetailScheduledTransactionResponse {
    private String scheduleId;
    private String status;
    private String frequency;
    private Double amount;
    private String scheduledDate;
    private String days;
    private String startDate;
    private String endDate;
    private Long numbersTransactions;
    private SourceAccountResponse source;
}
