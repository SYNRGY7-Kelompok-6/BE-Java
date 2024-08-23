package com.kelp_6.banking_apps.service;

import com.kelp_6.banking_apps.model.calculator.FinancialCalculatorRequest;
import com.kelp_6.banking_apps.model.calculator.FinancialCalculatorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class FinancialCalculatorServiceImpl implements FinancialCalculatorService {
    private final static Logger LOGGER = LoggerFactory.getLogger(FinancialCalculatorServiceImpl.class);

    @Override
    public FinancialCalculatorResponse calculateFinance(FinancialCalculatorRequest request) {
        LOGGER.info("accessed");

        long needs = (long) (request.getAvailableBalance() * 0.5);
        long wants = (long) (request.getAvailableBalance() * 0.1);
        long savings = (long) (request.getAvailableBalance() * 0.1);
        long debts = (long) (request.getAvailableBalance() * 0.3);
        long invest = (long) (request.getAvailableBalance() * 0);

        if(!request.getDebt()){
            wants += (long) (request.getAvailableBalance() * 0.05);
            savings += (long) (request.getAvailableBalance() * 0.05);
            debts -= (long) (request.getAvailableBalance() * 0.3);
            invest += (long) (request.getAvailableBalance() * 0.2);
        }

        return FinancialCalculatorResponse.builder()
                .needs(needs)
                .wants(wants)
                .savings(savings)
                .debts(debts)
                .invest(invest)
                .build();
    }
}
