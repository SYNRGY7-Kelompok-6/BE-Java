package com.kelp_6.banking_apps.entity;

import lombok.Getter;

@Getter
public enum EScheduleStatus {
    PENDING("Pending"),
    SUCCESS("Success"),
    FAILED("Failed"),
    CANCELED("Canceled");

    private final String description;

    EScheduleStatus(String description){
        this.description = description;
    }
}
