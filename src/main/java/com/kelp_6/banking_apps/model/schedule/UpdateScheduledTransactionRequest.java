package com.kelp_6.banking_apps.model.schedule;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.Min;
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
    @Min(value = 0, message = "amount must be a positive number")
    private Double amount;

    private String description;

    @NotBlank(message = "frequency can't be blank")
    private String frequency;

    @NotBlank(message = "schedule can't be blank")
    private String schedule;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private Date startDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private Date endDate;

    private Integer numbersTransactions;

    @JsonIgnore
    private String scheduleID;

    @JsonIgnore
    private String userID;

    @JsonIgnore
    private String pinToken;
}
