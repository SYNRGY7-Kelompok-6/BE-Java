package com.kelp_6.banking_apps.entity;

import lombok.Getter;

@Getter
public enum EScheduleFrequency {
    ONCE("Sekali"),
    WEEKLY("Mingguan"),
    MONTHLY("Bulanan");

    private final String description;

    EScheduleFrequency(String description){
        this.description = description;
    }

}
