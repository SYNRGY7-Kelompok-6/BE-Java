package com.kelp_6.banking_apps.model.schedule;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OnceFrequencyDetailResponse implements FrequencyDetail {
    private String scheduledDate;

    @Override
    public String getScheduledDate() {
        return this.scheduledDate;
    }
}
