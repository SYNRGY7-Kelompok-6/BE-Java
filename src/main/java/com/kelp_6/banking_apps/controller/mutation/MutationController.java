package com.kelp_6.banking_apps.controller.mutation;

import com.kelp_6.banking_apps.model.mutation.MutationRequest;
import com.kelp_6.banking_apps.model.mutation.MutationResponse;
import com.kelp_6.banking_apps.model.web.WebResponse;
import com.kelp_6.banking_apps.service.MutationService;
import com.kelp_6.banking_apps.utils.DateUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

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
        request.setUsername(userDetails.getUsername());
        request.setFromDate(fromDate);
        request.setToDate(toDate);
        request.setPage(page);
        request.setPageSize(pageSize);


        MutationResponse mutationResponse = mutationService.getMutation(request);

        return WebResponse.<MutationResponse>builder()
                .data(mutationResponse)
                .build();
    }
}
