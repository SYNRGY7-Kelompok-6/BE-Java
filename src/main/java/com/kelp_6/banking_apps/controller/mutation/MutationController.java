package com.kelp_6.banking_apps.controller.mutation;

import com.kelp_6.banking_apps.model.mutation.*;
import com.kelp_6.banking_apps.model.web.WebResponse;
import com.kelp_6.banking_apps.service.MutationService;
import com.kelp_6.banking_apps.utils.DateUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Date;

@RestController
@RequestMapping("/bank-statement")
@RequiredArgsConstructor
@Slf4j
public class MutationController {
    private final MutationService mutationService;

    @GetMapping({"", "/"})
    public WebResponse<MutationResponse> getMutationInfo(
            Authentication authentication,
            @RequestParam(value = "fromDate", required = false) @DateTimeFormat(pattern = "dd-MM-yyy") Date fromDate,
            @RequestParam(value = "toDate", required = false) @DateTimeFormat(pattern = "dd-MM-yyy") Date toDate,
            @RequestParam(value = "page", required = false, defaultValue = "0") int page,
            @RequestParam(value = "pageSize", required = false, defaultValue = "10") int pageSize
            ) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        fromDate = (fromDate == null) ? new Date() : fromDate;
        toDate = (toDate == null) ? new Date() : toDate;

        fromDate = DateUtil.getStartOfDay(fromDate);
        toDate = DateUtil.getEndOfDay(toDate);

        MutationRequest request = new MutationRequest();
        request.setUserID(userDetails.getUsername());
        request.setFromDate(fromDate);
        request.setToDate(toDate);
        request.setPage(page);
        request.setPageSize(pageSize);


        MutationResponse mutationResponse = mutationService.getMutation(request);

        return WebResponse.<MutationResponse>builder()
                .status("success")
                .message("success getting account info")
                .data(mutationResponse)
                .build();
    }

    @GetMapping("/mutations")
    public WebResponse<MutationsOnlyResponse> getMutationsOnly(
            Authentication authentication,
            @RequestHeader("X-PIN-TOKEN") String pinToken,
            @RequestParam(value = "fromDate", required = false) @DateTimeFormat(pattern = "dd-MM-yyy") Date fromDate,
            @RequestParam(value = "toDate", required = false) @DateTimeFormat(pattern = "dd-MM-yyy") Date toDate,
            @RequestParam(value = "page", required = false, defaultValue = "0") int page,
            @RequestParam(value = "pageSize", required = false, defaultValue = "10") int pageSize
    ) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        fromDate = (fromDate == null) ? new Date() : fromDate;
        toDate = (toDate == null) ? new Date() : toDate;

        fromDate = DateUtil.getStartOfDay(fromDate);
        toDate = DateUtil.getEndOfDay(toDate);

        MutationRequest request = new MutationRequest();
        request.setUserID(userDetails.getUsername());
        request.setFromDate(fromDate);
        request.setToDate(toDate);
        request.setPage(page);
        request.setPageSize(pageSize);
        request.setPinToken(pinToken);

        MutationsOnlyResponse mutationsOnlyResponse = mutationService.getMutationsOnly(request);

        return WebResponse.<MutationsOnlyResponse>builder()
                .status("success")
                .message("success getting mutations info")
                .data(mutationsOnlyResponse)
                .build();
    }

    @GetMapping("/mutations/detail")
    public WebResponse<TransactionDetailResponse> getTransactionDetail(
            Authentication authentication,
//            @RequestHeader("X-PIN-TOKEN") String pinToken,
            @RequestParam(value = "id_transaction") String id
    ){

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        TransactionDetailRequest request = new TransactionDetailRequest();
        request.setTransaction_id(id);
        request.setUserID(userDetails.getUsername());
//        request.setPinToken(pinToken);

        TransactionDetailResponse transactionDetailResponse = mutationService.getDetailTransaction(request);

        return WebResponse.<TransactionDetailResponse>builder()
                .status("success")
                .message("success getting detail transaction")
                .data(transactionDetailResponse)
                .build();
    }


    @GetMapping("/monthly")
    public WebResponse<AccountMonthlyResponse> getMutationMonthly(
            Authentication authentication,

            @RequestParam(value = "month") String months
    ){
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        try {
            int month = Integer.parseInt(months);
            AccountMonthlyResponse response = mutationService.getMonthlyMutation(month,userDetails.getUsername());

            return WebResponse.<AccountMonthlyResponse>builder()
                    .status("success")
                    .message("success getting account info")
                    .data(response)
                    .build();

        }catch (NumberFormatException e){
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY,"Cannot Find Month");
        }
    }
}
