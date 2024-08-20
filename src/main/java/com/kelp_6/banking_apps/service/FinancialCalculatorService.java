package com.kelp_6.banking_apps.service;

import com.kelp_6.banking_apps.model.calculator.FinancialCalculatorRequest;
import com.kelp_6.banking_apps.model.calculator.FinancialCalculatorResponse;

public interface FinancialCalculatorService {
    FinancialCalculatorResponse calculateFinance(FinancialCalculatorRequest request);
}
