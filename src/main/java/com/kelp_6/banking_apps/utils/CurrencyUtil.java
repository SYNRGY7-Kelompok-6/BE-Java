package com.kelp_6.banking_apps.utils;

import com.kelp_6.banking_apps.model.transfer.intrabank.Amount;

import java.util.List;
import java.util.Optional;

public class CurrencyUtil {
    public static List<String> availableCurrency() {
        // TODO: fetch currency API

        return List.of("IDR");
    }

    public static Optional<Amount> convertAmountCurrency(Amount amount, String toCurrency) {
        List<String> availableCurr = availableCurrency();

        if ((!availableCurr.contains(amount.getCurrency())) || (!availableCurr.contains(toCurrency))) {
            return Optional.empty();
        }

        // TODO: fetch currency API

        // TODO: replace below with result of multiplication from rate and original amount
        return Optional.of(amount);
    }
}
