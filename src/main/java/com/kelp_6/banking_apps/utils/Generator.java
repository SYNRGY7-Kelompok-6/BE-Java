package com.kelp_6.banking_apps.utils;

import java.util.Random;

public class Generator {
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final Random random = new Random();

    public static String tenDigitNumberGenerator(){

        // Generate a random 10-digit number
        long number = 1_000_000_000L + (long) (random.nextDouble() * 9_000_000_000L);

        return number + "";
    }

    public static String generateRandomAlphanumeric(int length){
        StringBuilder builder = new StringBuilder(length);
        for(int i=0; i<length; i++){
            builder.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        }
        return builder.toString();
    }
}
