package com.kelp_6.banking_apps.utils;

import java.util.Random;

public class Generator {
    public static String tenDigitNumberGenerator(){
        Random random = new Random();

        // Generate a random 10-digit number
        long number = 1_000_000_000L + (long) (random.nextDouble() * 9_000_000_000L);

        return number + "";
    }
}
