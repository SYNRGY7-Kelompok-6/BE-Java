package com.kelp_6.banking_apps.utils;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

public class UuidUtil {
    public static UUID convertStringIntoUUID(String id) {
        try{
            return UUID.fromString(id);
        }catch (IllegalArgumentException e){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid id format");
        }
    }
}
