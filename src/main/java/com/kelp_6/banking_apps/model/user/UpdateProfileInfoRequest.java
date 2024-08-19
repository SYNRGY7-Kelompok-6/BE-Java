package com.kelp_6.banking_apps.model.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.Email;
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
public class UpdateProfileInfoRequest {
    private String name;

    @Email(message = "invalid email format")
    private String email;

    @Pattern(regexp = "^$|\\d{9,13}", message = "phone number must be empty or contain 9 to 13 digits")
    private String phone;

    @DateTimeFormat(pattern = "dd-MM-yyyy")
    private Date birth;

    private String address;
}
