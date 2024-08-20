package com.kelp_6.banking_apps.model.schedule;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.*;
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
public class ScheduledTransactionRequest {
    @NotBlank(message = "account number can't be blank")
    @Size(min = 10, max = 10, message = "account number's length must be 10")
    @Pattern(regexp = "\\d++", message = "account number should contain only numbers")
    private String beneficiaryAccountNumber;

    @NotNull(message = "amount is required field")
    @Min(value = 0, message = "amount must be a positive number")
    private Double amount;

    private String description;

    @NotBlank(message = "frequency can't be blank")
    private String frequency;

    // can be filled with
    // dd-MM-yyyy (once scheduled)
    // "senin", "selasa", ..., "minggu" (weekly scheduled)
    // 1, 2, 3, ... 30, 31 (monthly scheduled)
    @NotBlank(message = "schedule can't be blank")
    private String schedule;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy", timezone = "Asia/Jakarta")
    private Date startDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy", timezone = "Asia/Jakarta")
    private Date endDate;

    private Integer numbersTransactions;

    @JsonIgnore
    private String userID;

    @JsonIgnore
    private String pinToken;
}
