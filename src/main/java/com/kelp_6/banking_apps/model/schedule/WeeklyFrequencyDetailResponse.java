package com.kelp_6.banking_apps.model.schedule;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WeeklyFrequencyDetailResponse implements FrequencyDetail {
    private String scheduledDate;
    private String dayOfWeek;
    private String startDate;
    private String endDate;
    private Integer numbersTransactions;

    @Override
    public String getScheduledDate() {
        return this.scheduledDate;
    }
}
