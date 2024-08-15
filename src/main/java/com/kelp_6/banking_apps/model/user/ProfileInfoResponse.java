package com.kelp_6.banking_apps.model.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProfileInfoResponse {
    private String imageUrl;
    private String name;
    private String email;
    private String phone;
    private String birth;
    private String address;
}
