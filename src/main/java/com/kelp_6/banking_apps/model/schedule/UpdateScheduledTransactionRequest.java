package com.kelp_6.banking_apps.model.schedule;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateScheduledTransactionRequest {
    @Pattern(regexp = "^$|\\d+", message = "amount must be empty or a numeric value")
    private Double amount;

    private String description;

    @NotBlank(message = "frequency can't be blank")
    private String frequency;

    @NotBlank(message = "schedule can't be blank")
    private String schedule;

    @DateTimeFormat(pattern = "dd-MM-yyyy")
    private Date startDate;

    @DateTimeFormat(pattern = "dd-MM-yyyy")
    private Date endDate;

    private Long numbersTransactions;

    @JsonIgnore
    private String scheduleID;

    @JsonIgnore
    private String userID;

    @JsonIgnore
    private String pinToken;
}
