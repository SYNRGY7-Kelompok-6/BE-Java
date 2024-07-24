package com.kelp_6.banking_apps.model.mutation;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MutationRequest {
    @JsonIgnore
    private String userID;

    @JsonIgnore
    private int page;

    @JsonIgnore
    private int pageSize;

    @JsonIgnore
    private Date fromDate;

    @JsonIgnore
    private Date toDate;
}
