package com.kelp_6.banking_apps.utils;

import org.springframework.validation.FieldError;

public class StringsUtil {
    public static String formatFieldError(FieldError fieldError){
        return String.format("Field '%s': %s", fieldError.getField(), fieldError.getDefaultMessage());
    }
}
