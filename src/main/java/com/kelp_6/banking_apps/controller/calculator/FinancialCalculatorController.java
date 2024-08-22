package com.kelp_6.banking_apps.controller.calculator;

import com.kelp_6.banking_apps.model.calculator.FinancialCalculatorRequest;
import com.kelp_6.banking_apps.model.calculator.FinancialCalculatorResponse;
import com.kelp_6.banking_apps.model.web.WebResponse;
import com.kelp_6.banking_apps.service.FinancialCalculatorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/financial-calculator")
@RequiredArgsConstructor
public class FinancialCalculatorController {
    private final static Logger LOGGER = LoggerFactory.getLogger(FinancialCalculatorController.class);
    private final FinancialCalculatorService financialCalculatorService;

    @GetMapping(
            path = {"", "/"},
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<FinancialCalculatorResponse> calculateFinancialAdvice(@RequestBody @Valid FinancialCalculatorRequest request){
        LOGGER.info("accessed");

        FinancialCalculatorResponse financialCalculatorResponse = financialCalculatorService.calculateFinance(request);

        return WebResponse.<FinancialCalculatorResponse>builder()
                .status("success")
                .message("financial advice calculated successfully")
                .data(financialCalculatorResponse)
                .build();
    }
}

