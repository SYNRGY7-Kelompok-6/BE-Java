package com.kelp_6.banking_apps.utils;

import java.util.Date;
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

    public static String fourDigitNumberGenerator() {
        return String.valueOf(1000 + random.nextInt(9000));
    }

    public static String refNumberGenerator(Date transactionDate) {
        return String.format("%d%d%d%d%d%d%s%s",
                transactionDate.getYear() - 100,
                transactionDate.getMonth() + 1,
                transactionDate.getDate(),
                transactionDate.getHours(),
                transactionDate.getMinutes(),
                transactionDate.getSeconds(),
                Generator.fourDigitNumberGenerator(),
                "4");
    }
}
